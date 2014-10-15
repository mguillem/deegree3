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
import static org.deegree.coverage.raster.geom.RasterGeoReference.OriginLocation.OUTER;
import static org.deegree.coverage.raster.interpolation.InterpolationType.BILINEAR;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataFromImage;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataToImage;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.deegree.commons.utils.math.MathUtils;
import org.deegree.coverage.raster.RasterTransformer;
import org.deegree.coverage.raster.SimpleRaster;
import org.deegree.coverage.raster.data.RasterData;
import org.deegree.coverage.raster.geom.RasterGeoReference;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryTransformer;
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
        //
        // try {
        // ICRS targetCRS = CRSManager.lookup( "epsg:25833" );
        // envelope = new GeometryTransformer( targetCRS ).transform( envelope );
        // } catch ( IllegalArgumentException e ) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch ( TransformationException e ) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch ( UnknownCRSException e ) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
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
                int tileWidth = imageToDraw.getWidth();
                int tileHeight = imageToDraw.getHeight();

                RasterGeoReference rasterGeoReference = RasterGeoReference.create( OUTER, env, tileWidth, tileHeight );
                RasterData data = rasterDataFromImage( imageToDraw );
                SimpleRaster raster = new SimpleRaster( data, env, rasterGeoReference, null );

                RasterTransformer rtrans = new RasterTransformer( requestedCrs );
                SimpleRaster transformed = rtrans.transform( raster, envelope, tileWidth, tileHeight, BILINEAR ).getAsSimpleRaster();
                imageToDraw = rasterDataToImage( transformed.getRasterData() );

                env = new GeometryTransformer( requestedCrs ).transform( env );
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

        int minxa = Math.min( minx, maxx );
        int minya = Math.min( miny, maxy );
        int maxxa = Math.max( minx, maxx );
        int maxya = Math.max( miny, maxy );
        try {
            graphics.drawImage( tile.getAsImage(), minxa, minya, maxxa - minxa, maxya - minya, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            graphics.setColor( RED );
            graphics.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }
}
