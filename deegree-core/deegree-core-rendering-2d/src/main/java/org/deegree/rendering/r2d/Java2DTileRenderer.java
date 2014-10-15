//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany
 http://www.occamlabs.de/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.rendering.r2d;

import static java.awt.Color.RED;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static org.deegree.commons.utils.math.MathUtils.round;
import static org.deegree.coverage.raster.geom.RasterGeoReference.OriginLocation.OUTER;
import static org.deegree.coverage.raster.interpolation.InterpolationType.BILINEAR;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataFromImage;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataToImage;
import static org.deegree.cs.coordinatesystems.GeographicCRS.WGS84;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.deegree.commons.utils.MapUtils;
import org.deegree.commons.utils.math.MathUtils;
import org.deegree.coverage.raster.AbstractRaster;
import org.deegree.coverage.raster.RasterTransformer;
import org.deegree.coverage.raster.SimpleRaster;
import org.deegree.coverage.raster.data.RasterData;
import org.deegree.coverage.raster.geom.RasterGeoReference;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryTransformer;
import org.deegree.rendering.r2d.context.MapOptions.Interpolation;
import org.deegree.tile.Tile;
import org.deegree.tile.TileIOException;
import org.slf4j.Logger;

/**
 * <code>Java2DTileRenderer</code>
 * 
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 31882 $, $Date: 2011-09-15 02:05:04 +0200 (Thu, 15 Sep 2011) $
 */

public class Java2DTileRenderer implements TileRenderer {

    private static final Logger LOG = getLogger( Java2DTileRenderer.class );

    private Graphics2D graphics;

    private AffineTransform worldToScreen = new AffineTransform();

    private Envelope envelope;

    private int width;

    private int height;

    /**
     * @param graphics
     * @param width
     * @param height
     * @param envelope
     */
    public Java2DTileRenderer( Graphics2D graphics, int width, int height, Envelope envelope ) {
        this.graphics = graphics;
        this.width = width;
        this.height = height;
        this.envelope = envelope;
        RenderHelper.getWorldToScreenTransform( worldToScreen, envelope, width, height );
    }
    
    @Override
    public void render( Tile tile ) {
        if ( tile == null ) {
            LOG.debug( "Not rendering null tile." );
            return;
        }
        int minx, miny, maxx, maxy;
        Envelope env = tile.getEnvelope();

        BufferedImage imageToDraw = tile.getAsImage();
        ICRS requestedCrs = envelope.getCoordinateSystem();
        if ( !requestedCrs.equals( env.getCoordinateSystem() ) ) {
            try {

//                int tileWidth = imageToDraw.getWidth();
//                int tileHeight = imageToDraw.getHeight();
//
//                if ( imageToDraw.getType() != TYPE_4BYTE_ABGR ) {
//                    BufferedImage img = new BufferedImage( tileWidth, tileHeight, TYPE_4BYTE_ABGR );
//                    Graphics2D g = img.createGraphics();
//                    g.drawImage( imageToDraw, 0, 0, null );
//                    g.dispose();
//                    imageToDraw = img;
//                }

                Envelope targetEnv = new GeometryTransformer( requestedCrs ).transform( env );

//                RasterGeoReference rasterGeoReference = RasterGeoReference.create( OUTER, env, tileWidth, tileHeight );
//                RasterData data = rasterDataFromImage( imageToDraw );
//                SimpleRaster raster = new SimpleRaster( data, env, rasterGeoReference, null );
//
//                RasterTransformer rtrans = new RasterTransformer( requestedCrs );
////
////                double ratio = calculateRatio( env, tileWidth, tileHeight, targetEnv );
////
////                int newTileWidth = abs( round( ratio * tileWidth ) );
////                int newTileHeight = abs( round( ratio * tileHeight ) );
//
//                AbstractRaster transformedRaster = rtrans.transform( raster, targetEnv, tileWidth+10, tileHeight+10, BILINEAR );
//                SimpleRaster transformed = transformedRaster.getAsSimpleRaster();
//                imageToDraw = rasterDataToImage( transformed.getRasterData() );

                env = targetEnv;
            } catch ( TransformationException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch ( IllegalArgumentException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch ( UnknownCRSException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Point2D.Double p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( env.getMin().get0(),
                                                                                         env.getMin().get1() ), null );
        minx = MathUtils.round( p.x );
        miny = MathUtils.round( p.y );
        p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( env.getMax().get0(), env.getMax().get1() ),
                                                      null );
        maxx = MathUtils.round( p.x );
        maxy = MathUtils.round( p.y );

        try {
            graphics.drawImage( imageToDraw, minx, maxy, maxx - minx, miny - maxy, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            graphics.setColor( RED );
            graphics.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }

    private double calculateRatio( Envelope env, int tileWidth, int tileHeight, Envelope targetEnv ) {
        double scale = calculateScale( tileWidth, tileHeight, env );
        double newScale = calculateScale( tileWidth, tileHeight, targetEnv );
        return scale / newScale;
    }

    // TODO copy of org.deegree.rendering.r2d.RenderHelper.calcScaleWMS111(int, int, Envelope, ICRS)
    private double calculateScale( int tileWidth, int tileHeight, Envelope bbox ) {
        if ( tileWidth == 0 || tileHeight == 0 ) {
            return 0;
        }
        ICRS crs = bbox.getCoordinateSystem();
        if ( "m".equalsIgnoreCase( crs.getAxis()[0].getUnits().toString() ) ) {
            /*
             * this method to calculate a maps scale as defined in OGC WMS and SLD specification is not required for
             * maps having a projected reference system. Direct calculation of scale avoids uncertainties
             */
            double dx = bbox.getSpan0() / tileWidth;
            double dy = bbox.getSpan1() / tileHeight;
            return sqrt( dx * dx + dy * dy );
        } else {
            if ( !crs.equals( WGS84 ) ) {
                // transform the bounding box of the request to EPSG:4326
                GeometryTransformer trans = new GeometryTransformer( WGS84 );
                try {
                    bbox = trans.transform( bbox, crs );
                } catch ( IllegalArgumentException e ) {
                    LOG.error( "Unknown error", e );
                } catch ( TransformationException e ) {
                    LOG.error( "Unknown error", e );
                }
            }
            double dx = bbox.getSpan0() / tileWidth;
            double dy = bbox.getSpan1() / tileHeight;
            double minx = bbox.getMin().get0() + dx * ( tileWidth / 2d - 1 );
            double miny = bbox.getMin().get1() + dy * ( tileHeight / 2d - 1 );
            double maxx = bbox.getMin().get0() + dx * ( tileWidth / 2d );
            double maxy = bbox.getMin().get1() + dy * ( tileHeight / 2d );

            double distance = MapUtils.calcDistance( minx, miny, maxx, maxy );
            return distance / MapUtils.SQRT2;
        }
    }
}
