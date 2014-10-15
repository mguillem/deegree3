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
package org.deegree.layer.persistence.tile;

import static org.deegree.coverage.raster.geom.RasterGeoReference.OriginLocation.OUTER;
import static org.deegree.coverage.raster.utils.RasterFactory.rasterDataFromImage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.deegree.coverage.raster.SimpleRaster;
import org.deegree.coverage.raster.data.RasterData;
import org.deegree.coverage.raster.geom.RasterGeoReference;
import org.deegree.feature.FeatureCollection;
import org.deegree.geometry.Envelope;
import org.deegree.layer.LayerData;
import org.deegree.rendering.r2d.Java2DTileRenderer;
import org.deegree.rendering.r2d.RasterRenderer;
import org.deegree.rendering.r2d.context.RenderContext;
import org.deegree.rendering.r2d.context.RenderingInfo;
import org.deegree.style.utils.ImageUtils;
import org.deegree.tile.Tile;

/**
 * <code>TileLayerData</code>
 * 
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 31882 $, $Date: 2011-09-15 02:05:04 +0200 (Thu, 15 Sep 2011) $
 */

public class TileLayerData implements LayerData {

    private final Iterator<Tile> tiles;

    public TileLayerData( Iterator<Tile> tiles ) {
        this.tiles = tiles;
    }

    @Override
    public void render( RenderContext context ) {
        RenderingInfo info = context.getInfo();
        int width = info.getWidth();
        int height = info.getHeight();
        BufferedImage image = ImageUtils.prepareImage( info.getFormat(), width, height, info.getTransparent(),
                                                       info.getBgColor() );
        Graphics2D graphics = image.createGraphics();

//        if ( image.getType() != BufferedImage.TYPE_4BYTE_ABGR ) {
//            BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
//            Graphics2D g = img.createGraphics();
//            g.drawImage( image, 0, 0, null );
//            g.dispose();
//            image = img;
//        }

        Envelope envelope = info.getEnvelope();
        Java2DTileRenderer tileRenderer = new Java2DTileRenderer( graphics, width, height, envelope );
        while ( tiles.hasNext() ) {
            tileRenderer.render( tiles.next() );
        }

        try {
            ImageIO.write( image, "png",File.createTempFile( "alles_", ".png" ) );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        RasterRenderer rasterRenderer = context.getRasterRenderer();
        RasterGeoReference rasterGeoReference = RasterGeoReference.create( OUTER, envelope, width, height );
        RasterData data = rasterDataFromImage( image );
        SimpleRaster raster = new SimpleRaster( data, envelope, rasterGeoReference, null );
        rasterRenderer.render( null, raster );
    }

    @Override
    public FeatureCollection info() {
        return null;
    }

}
