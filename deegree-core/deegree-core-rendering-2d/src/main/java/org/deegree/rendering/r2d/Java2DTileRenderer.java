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
import static java.lang.Math.abs;
import static org.deegree.commons.utils.math.MathUtils.round;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataFromImage;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataToImage;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.deegree.commons.utils.DoublePair;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.math.MathUtils;
import org.deegree.coverage.raster.AbstractRaster;
import org.deegree.coverage.raster.RasterTransformer;
import org.deegree.coverage.raster.SimpleRaster;
import org.deegree.coverage.raster.data.RasterData;
import org.deegree.coverage.raster.geom.Grid;
import org.deegree.coverage.raster.geom.RasterGeoReference;
import org.deegree.coverage.raster.interpolation.InterpolationType;
import org.deegree.coverage.raster.interpolation.RasterInterpolater;
import org.deegree.coverage.raster.utils.CoverageTransform;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.GeometryTransformer;
import org.deegree.rendering.r2d.context.RenderingInfo;
import org.deegree.style.utils.ImageUtils;
import org.deegree.tile.Tile;
import org.deegree.tile.TileIOException;
import org.slf4j.Logger;

import javax.imageio.ImageIO;

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

    private RenderingInfo renderingInfo;

    private Envelope envelope;

    /**
     * @param graphics
     * @param width
     * @param height
     * @param envelope
     */
    public Java2DTileRenderer( Graphics2D graphics, int width, int height, Envelope envelope, RenderingInfo renderingInfo) {
        this.renderingInfo = renderingInfo;
        this.graphics = graphics;
        this.envelope = envelope;
        RenderHelper.getWorldToScreenTransform( worldToScreen, envelope, width, height );
    }

    @Override
    public void render (Tile tile) {
        render (tile, graphics, worldToScreen);
    }

    private void render( Tile tile, Graphics renderGraphics, AffineTransform worldToScreenForTiles) {
        if ( tile == null ) {
            LOG.debug( "Not rendering null tile." );
            return;
        }
        int minx, miny, maxx, maxy;
        Envelope env = tile.getEnvelope();
        Point2D.Double p = (Point2D.Double) worldToScreenForTiles.transform( new Point2D.Double( env.getMin().get0(),
                                                                                         env.getMin().get1() ), null );
        minx = round(p.x);
        miny = round(p.y);
        p = (Point2D.Double) worldToScreenForTiles.transform( new Point2D.Double( env.getMax().get0(), env.getMax().get1() ),
                                                      null );
        maxx = round(p.x);
        maxy = round(p.y);
        try {
            renderGraphics.drawImage( tile.getAsImage(), minx, miny, maxx - minx, maxy - miny, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            renderGraphics.setColor( RED );
            renderGraphics.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }

    public void render( Iterator<Tile> tiles ) {

        BufferedImage image = null;
        boolean needsTransformation = false;
        Tile tile;
        Graphics renderGraphics = null;
        AffineTransform worldToScreenForTiles = null;
        Envelope renderEnv = null;
        DoublePair scale = null;

        if (tiles.hasNext()) {
            tile = tiles.next();
            Envelope tileEnvelope = tile.getEnvelope();
            if (tileEnvelope.getCoordinateSystem().equals(envelope.getCoordinateSystem())) {
                worldToScreenForTiles = worldToScreen;
                renderGraphics = graphics;
            } else {
                needsTransformation = true;
                System.out.println("I still need to transform from " + tileEnvelope.getCoordinateSystem() + " to "
                        + envelope.getCoordinateSystem());


                try {
                    renderEnv = new GeometryTransformer(tileEnvelope.getCoordinateSystem()).transform(envelope);
                } catch (TransformationException e) {
                    e.printStackTrace();
                } catch (UnknownCRSException e) {
                    e.printStackTrace();
                }

                worldToScreenForTiles = new AffineTransform();

                scale = RenderHelper.getWorldToScreenTransform( worldToScreenForTiles, renderEnv,
                                                                renderingInfo.getWidth(), renderingInfo.getHeight() ).getSecond();

                image = ImageUtils.prepareImage( renderingInfo.getFormat(), renderingInfo.getWidth(),
                        renderingInfo.getHeight(), renderingInfo.getTransparent(),
                                                 renderingInfo.getBgColor() );

                renderGraphics = image.getGraphics();

            }

            render(tile, renderGraphics, worldToScreenForTiles);
        }

        while (tiles.hasNext()) {
            tile = tiles.next();
            render(tile, renderGraphics, worldToScreenForTiles);
        }



        if (needsTransformation) renderFullImage(image, renderEnv, scale);
    }

    private void renderFullImage( BufferedImage image, Envelope tileEnv, DoublePair scale ) {

        try {
            ImageIO.write(image, "png", new File("/home/wanhoff/tiling-untransformed.png"));
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        int minx, miny, maxx, maxy;
        Point2D.Double p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( envelope.getMin().get0(),
            envelope.getMin().get1() ), null );
        minx = round(p.x);
        miny = round(p.y);
        p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( envelope.getMax().get0(),
            envelope.getMax().get1() ), null );
        maxx = round(p.x);
        maxy = round(p.y);

        RasterData data = rasterDataFromImage( image );
        RasterGeoReference geoReference = RasterGeoReference.create(RasterGeoReference.OriginLocation.OUTER, tileEnv,
                image.getWidth(), image.getHeight());

        SimpleRaster raster = new SimpleRaster( data, tileEnv, geoReference, null );

        RasterTransformer rtrans = new RasterTransformer( envelope.getCoordinateSystem() );
        AbstractRaster transformed = null;

        try {
            transformed = rtrans.transform( raster, InterpolationType.NEAREST_NEIGHBOR ).getAsSimpleRaster();
        } catch ( TransformationException e ) {
            e.printStackTrace();
        } catch (UnknownCRSException e) {
            e.printStackTrace();
        }
        image = rasterDataToImage(transformed.getAsSimpleRaster().getRasterData());

        try {
            ImageIO.write(image, "png", new File("/home/wanhoff/tiling-full-transformed.png"));
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        /*
        int minxa = Math.min( minx, maxx );
        int minya = Math.min( miny, maxy );
        int maxxa = Math.max( minx, maxx );
        int maxya = Math.max( miny, maxy );
        */

        try {
            graphics.drawImage( image, minx, miny, maxx - minx, maxy - miny, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            graphics.setColor( RED );
            graphics.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }

}
