//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.protocol.wms.ops;

import static java.awt.Color.WHITE;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.geometry.Envelope;
import org.deegree.protocol.wms.sld.SldNamedLayer;

/**
 * Encapsulates a GetMap request for Feature Portrayal Services described in the "Styled Layer Descriptor profile of the
 * Web Map Service Implementation Specification", version 1.1.0
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class FeaturePortrayalGetMap {

    static final String DEFAULT_FORMAT = "image/png;";

    private final ICRS crs;

    private final Envelope bbox;

    private final List<SldNamedLayer> style;

    private final int width;

    private final int height;

    private final String format;

    private final URL remoteWfsUrl;

    private final Color bgColor;

    private final boolean transparent;

    /**
     * Instantiates a new {@link FeaturePortrayalGetMap}.
     * 
     * @param crs
     * @param bbox
     * @param style
     *            sld used to render the WFS data, never <code>null</code>
     * @param width
     *            of the map to return, must be greater than 0
     * @param height
     *            of the map to return, must be greater than 0
     * @param format
     *            of the map to return, may be <code>null</code>, default value is 'image/png'
     * @param remoteWfsUrl
     *            the URL of the remote WFS containing the data to render, never <code>null</code>
     * @param bgColor
     *            background color, may be <code>null</code>, default is white
     * @param transparent
     *            if background of the map should be transparent
     * @throws IllegalArgumentException
     *             if one of the passed parameters is not valid
     */
    FeaturePortrayalGetMap( ICRS crs, Envelope bbox, List<SldNamedLayer> style, int width, int height, String format,
                            URL remoteWfsUrl, Color bgColor, boolean transparent ) throws MalformedURLException {
        checkParameters( style, width, height, remoteWfsUrl );
        this.crs = crs;
        this.bbox = bbox;
        this.style = style;
        this.width = width;
        this.height = height;
        if ( format == null ) {
            this.format = DEFAULT_FORMAT;
        } else {
            this.format = format;
        }
        this.remoteWfsUrl = remoteWfsUrl;
        if ( bgColor == null ) {
            this.bgColor = WHITE;
        } else {
            this.bgColor = bgColor;
        }
        this.transparent = transparent;
    }

    /**
     * @return the crs
     */
    public ICRS getCrs() {
        return crs;
    }

    /**
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @return the style, never <code>null</code>
     */
    public List<SldNamedLayer> getStyle() {
        return style;
    }

    /**
     * @return the width of the requested map, greater than 0
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the requested map, greater than 0
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the format of the requested map
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the url of the remote wfs, never <code>null</code>
     */
    public URL getRemoteWfsUrl() {
        return remoteWfsUrl;
    }

    /**
     * @return the background color of the map, never <code>null</code>
     */
    public Color getBgColor() {
        return bgColor;
    }

    /**
     * @return true if the background of the map is transparent, false otherwise
     */
    public boolean isTransparent() {
        return transparent;
    }

    private void checkParameters( List<SldNamedLayer> style, int width, int height, URL remoteWfsUrl ) {
        if ( style == null || style.isEmpty() )
            throw new IllegalArgumentException( "SLD must not be null" );
        if ( width <= 0 )
            throw new IllegalArgumentException( "Width must be greater than 0" );
        if ( height <= 0 )
            throw new IllegalArgumentException( "Height must be greater than 0" );
        if ( remoteWfsUrl == null )
            throw new IllegalArgumentException( "RemoteWFS url must not be null" );
    }

}