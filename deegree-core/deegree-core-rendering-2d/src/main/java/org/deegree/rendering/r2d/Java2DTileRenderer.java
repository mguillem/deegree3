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

import org.deegree.coverage.raster.RasterTransformer;
import org.deegree.coverage.raster.SimpleRaster;
import org.deegree.coverage.raster.data.RasterData;
import org.deegree.coverage.raster.geom.RasterGeoReference;
import org.deegree.coverage.raster.interpolation.InterpolationType;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryTransformer;
import org.deegree.tile.Tile;
import org.deegree.tile.TileIOException;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static java.awt.Color.RED;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.lang.StrictMath.abs;
import static org.deegree.commons.utils.math.MathUtils.round;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataFromImage;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataToImage;
import static org.slf4j.LoggerFactory.getLogger;

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
        this.envelope = envelope;
        this.width = width;
        this.height = height;
        RenderHelper.getWorldToScreenTransform( worldToScreen, envelope, width, height );
    }

    public void render( Iterator<Tile> tiles ) {
        BufferedImage image = new BufferedImage(width, height, TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();
        while ( tiles.hasNext() ) {
            Tile tile = tiles.next();
            render( tile, g );
//            g.drawImage( tile.getAsImage(),0,0,null );
        }
        try {
            ImageIO.write( image, "png", new File( "/home/stenger/tiling.png" ) );
            render( image );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void render( Tile tile, Graphics graphic ) {
        AffineTransform worldToScreen2  = new AffineTransform();
        try {
            RenderHelper.getWorldToScreenTransform( worldToScreen2, new GeometryTransformer( CRSManager.lookup("EPSG:25833") ).transform( envelope ), width, height );
        } catch ( TransformationException e ) {
            e.printStackTrace();
        } catch ( UnknownCRSException e ) {
            e.printStackTrace();
        }

        if ( tile == null ) {
            LOG.debug( "Not rendering null tile." );
            return;
        }
        int minx, miny, maxx, maxy;
        Envelope env = tile.getEnvelope();
//        try {
//            env = new GeometryTransformer( envelope.getCoordinateSystem() ).transform( env );
//        } catch ( TransformationException e ) {
//            e.printStackTrace();
//        } catch ( UnknownCRSException e ) {
//            e.printStackTrace();
//        }
        Point2D.Double p = (Point2D.Double) worldToScreen2.transform( new Point2D.Double( env.getMin().get0(),
                                                                                         env.getMin().get1() ), null );
        minx = round( p.x );
        miny = round( p.y );
        p = (Point2D.Double) worldToScreen2.transform( new Point2D.Double( env.getMax().get0(), env.getMax().get1() ),
                                                      null );
        maxx = round( p.x );
        maxy = round( p.y );
        try {
            BufferedImage image = tile.getAsImage();

//            // hack to ensure correct raster transformations. 4byte_abgr seems to be working best with current api
//            if ( image != null && image.getType() != TYPE_4BYTE_ABGR ) {
//                BufferedImage img = new BufferedImage( image.getWidth(), image.getHeight(), TYPE_4BYTE_ABGR );
//                Graphics2D g = img.createGraphics();
//                g.drawImage( image, 0, 0, null );
//                g.dispose();
//                image = img;
//            }
//
//            RasterData data = rasterDataFromImage( image );
//            RasterGeoReference geoReference = RasterGeoReference.create( RasterGeoReference.OriginLocation.OUTER,
//                                                                         tile.getEnvelope(), image.getWidth(),
//                                                                         image.getHeight() );
//            SimpleRaster raster = new SimpleRaster( data, tile.getEnvelope(), geoReference, null );
//            RasterTransformer rtrans = new RasterTransformer( envelope.getCoordinateSystem() );
//            SimpleRaster transformed = null;
//            try {
//
//                double scale = RenderHelper.calcScaleWMS111( image.getWidth(), image.getHeight(), tile.getEnvelope(),
//                                                             tile.getEnvelope().getCoordinateSystem() );
//                double newScale = RenderHelper
//                      .calcScaleWMS111( image.getWidth(), image.getHeight(), env, envelope.getCoordinateSystem() );
//                double ratio = scale / newScale;
//
//                int newWidth = abs( round( ratio * image.getWidth() ) );
//                int newHeight = abs( round( ratio * image.getHeight() ) );
//
//                transformed = rtrans.transform( raster, env, newWidth, newHeight,
//                                                InterpolationType.BILINEAR ).getAsSimpleRaster();
//            } catch ( TransformationException e ) {
//                e.printStackTrace();
//            }
//            image = rasterDataToImage( transformed.getRasterData() );

            int minxa = Math.min( minx, maxx );
            int minya = Math.min( miny, maxy );
            int maxxa = Math.max( minx, maxx );
            int maxya = Math.max( miny, maxy );

            graphic.drawImage( image, minxa, minya, maxxa - minxa, maxya - minya, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            graphic.setColor( RED );
            graphic.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }

    private void render( BufferedImage image ) {
//        if ( tile == null ) {
//            LOG.debug( "Not rendering null tile." );
//            return;
//        }
        int minx, miny, maxx, maxy;

        Point2D.Double p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( envelope.getMin().get0(),
                                                                                         envelope.getMin().get1() ), null );
        minx = round( p.x );
        miny = round( p.y );
        p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( envelope.getMax().get0(), envelope.getMax().get1() ),
                                                      null );
        maxx = round( p.x );
        maxy = round( p.y );
        try {
            Envelope envOfImage = null;
            try {
                envOfImage = new GeometryTransformer( CRSManager.lookup("EPSG:25833") ).transform( envelope );
            } catch ( TransformationException e ) {
                e.printStackTrace();
            } catch ( UnknownCRSException e ) {
                e.printStackTrace();
            }
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            double scale = RenderHelper.calcScaleWMS111( imageWidth, imageHeight, envelope, envelope.getCoordinateSystem() );
            double newScale = RenderHelper.calcScaleWMS111( imageWidth, imageHeight, envOfImage, envOfImage.getCoordinateSystem() );
            double ratio = newScale / scale;

            int newWidth = abs( round( ratio * imageWidth ) );
            int newHeight = abs( round( ratio * imageHeight ) );

            // hack to ensure correct raster transformations. 4byte_abgr seems to be working best with current api
            if ( image != null && image.getType() != TYPE_4BYTE_ABGR ) {
                BufferedImage img = new BufferedImage( imageWidth, imageHeight, TYPE_4BYTE_ABGR );
                Graphics2D g = img.createGraphics();
                g.drawImage( image, 0, 0, null );
                g.dispose();
                image = img;
            }

            RasterData data = rasterDataFromImage( image );
            RasterGeoReference geoReference = RasterGeoReference.create( RasterGeoReference.OriginLocation.OUTER,
                                                                         envOfImage, imageWidth, imageHeight );
            SimpleRaster raster = new SimpleRaster( data, envOfImage, geoReference, null );
            try {
                ImageIO.write( rasterDataToImage( raster.getRasterData() ), "png", new File(
                                "/home/stenger/tiling-georef-raster.png" ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            RasterTransformer rtrans = new RasterTransformer( envelope.getCoordinateSystem() );
            SimpleRaster transformed = null;
            try {
                transformed = rtrans.transform( raster, envelope, width, height, InterpolationType.BILINEAR ).getAsSimpleRaster();
            } catch ( TransformationException e ) {
                e.printStackTrace();
            }
            image = rasterDataToImage( transformed.getRasterData() );

            int minxa = Math.min( minx, maxx );
            int minya = Math.min( miny, maxy );
            int maxxa = Math.max( minx, maxx );
            int maxya = Math.max( miny, maxy );

            try {
                ImageIO.write( image, "png", new File( "/home/stenger/tiling-transformed.png" ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            graphics.drawImage( image, minxa, minya, maxxa - minxa, maxya - minya, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            graphics.setColor( RED );
            graphics.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }

    @Override
    public void render( Tile tile ) {
        if ( tile == null ) {
            LOG.debug( "Not rendering null tile." );
            return;
        }
        int minx, miny, maxx, maxy;
        Envelope env = tile.getEnvelope();
        try {
            env = new GeometryTransformer( envelope.getCoordinateSystem() ).transform( env );
        } catch ( TransformationException e ) {
            e.printStackTrace();
        } catch ( UnknownCRSException e ) {
            e.printStackTrace();
        }
        Point2D.Double p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( env.getMin().get0(),
                                                                                         env.getMin().get1() ), null );
        minx = round( p.x );
        miny = round( p.y );
        p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( env.getMax().get0(), env.getMax().get1() ),
                                                      null );
        maxx = round( p.x );
        maxy = round( p.y );
        try {
            BufferedImage image = tile.getAsImage();


            // hack to ensure correct raster transformations. 4byte_abgr seems to be working best with current api
            if ( image != null && image.getType() != TYPE_4BYTE_ABGR ) {
                BufferedImage img = new BufferedImage( image.getWidth(), image.getHeight(), TYPE_4BYTE_ABGR );
                Graphics2D g = img.createGraphics();
                g.drawImage( image, 0, 0, null );
                g.dispose();
                image = img;
            }

            RasterData data = rasterDataFromImage( image );
            RasterGeoReference geoReference = RasterGeoReference.create( RasterGeoReference.OriginLocation.OUTER,
                                                                         tile.getEnvelope(), image.getWidth(),
                                                                         image.getHeight() );
            SimpleRaster raster = new SimpleRaster( data, tile.getEnvelope(), geoReference, null );
            RasterTransformer rtrans = new RasterTransformer( envelope.getCoordinateSystem() );
            SimpleRaster transformed = null;
            try {

                double scale = RenderHelper.calcScaleWMS111( image.getWidth(), image.getHeight(), tile.getEnvelope(),
                                                             tile.getEnvelope().getCoordinateSystem() );
                double newScale = RenderHelper.calcScaleWMS111( image.getWidth(), image.getHeight(), env, envelope.getCoordinateSystem() );
                double ratio = scale / newScale;

                int newWidth = abs( round( ratio * image.getWidth() ) );
                int newHeight = abs( round( ratio * image.getHeight() ) );

                transformed = rtrans.transform( raster, env, newWidth, newHeight,
                                                InterpolationType.BILINEAR ).getAsSimpleRaster();
            } catch ( TransformationException e ) {
                e.printStackTrace();
            }
            image = rasterDataToImage( transformed.getRasterData() );

            int minxa = Math.min( minx,  maxx );
            int minya = Math.min( miny,  maxy );
            int maxxa = Math.max( minx,  maxx );
            int maxya = Math.max( miny,  maxy );

            graphics.drawImage( image, minxa, minya, maxxa - minxa, maxya - minya, null );
        } catch ( TileIOException e ) {
            LOG.debug( "Error retrieving tile image: " + e.getMessage() );
            graphics.setColor( RED );
            graphics.fillRect( minx, miny, maxx - minx, maxy - miny );
        }
    }
}
