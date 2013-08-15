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

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.deegree.commons.ows.exception.OWSException.INVALID_PARAMETER_VALUE;
import static org.deegree.commons.ows.exception.OWSException.MISSING_PARAMETER_VALUE;
import static org.deegree.protocol.wms.WMSConstants.VERSION_130;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.tom.ows.Version;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryFactory;
import org.deegree.protocol.wms.sld.SldNamedLayer;
import org.deegree.protocol.wms.sld.SldParser;
import org.slf4j.Logger;

/**
 * Builder for WMS requests. Currently only GetMap requests for Feature Portrayal Services is suppotred.
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsRequestBuilder {

    private static final Logger LOG = getLogger( WmsRequestBuilder.class );

    private static GeometryFactory geometryFactory = new GeometryFactory();

    private SldParser sldParser;

    public WmsRequestBuilder( SldParser sldParser ) {
        this.sldParser = sldParser;
    }

    /**
     * Creates a {@link FeaturePortrayalGetMap} out of the passed parameters.
     * 
     * @param parameterMap
     *            contains the request parameters, keys must be upper case, never <code>null</code>. Parameter
     *            REMOTE_OWS_TYPE must have value WFS.
     * @param version
     *            currently only 1.3.0 is supported, never <code>null</code>
     * @return a new {@link FeaturePortrayalGetMap} instance if the version and all parameters are valid for a Feature
     *         Portrayal Service GetMap request,
     * @throws OWSException
     *             if a parameter is not a valid parameter for Get Map request for Feature Portrayal Service or the
     *             version parameter is not 1.3.0
     */
    public FeaturePortrayalGetMap buildFeaturePortrayalGetMapRequest( Map<String, String> parameterMap, Version version )
                            throws OWSException {
        checkVersion( version );
        checkRemoteWfsType( parameterMap );

        URL remoteWfsUrl = parseParseRemoteWfsUrlValue( parameterMap );
        String format = parameterMap.get( "FORMAT" );
        int height = parseIntValue( parameterMap, "HEIGHT" );
        int width = parseIntValue( parameterMap, "WIDTH" );
        List<SldNamedLayer> style = parseSld( parameterMap );
        ICRS crs = parseCrs( parameterMap );
        Envelope bbox = parseBbox( parameterMap, crs );
        try {
            return new FeaturePortrayalGetMap( crs, bbox, style, width, height, format, remoteWfsUrl );
        } catch ( MalformedURLException e ) {
            throw new OWSException( "", INVALID_PARAMETER_VALUE );
        }
    }

    private void checkVersion( Version version )
                            throws OWSException {
        if ( !VERSION_130.equals( version ) ) {
            throw new OWSException(
                                    "GetMap requests for Feature Portrayal Service are supported only for WMS version 1.3.0",
                                    INVALID_PARAMETER_VALUE );
        }
    }

    private void checkRemoteWfsType( Map<String, String> parameterMap )
                            throws OWSException {
        String remoteWfsType = parameterMap.get( "REMOTE_OWS_TYPE" );
        if ( !"WFS".equalsIgnoreCase( remoteWfsType ) )
            throw new OWSException( "Parameter 'REMOTE_OWS_TYPE' must have a fix value of WFS!",
                                    INVALID_PARAMETER_VALUE );
    }

    private URL parseParseRemoteWfsUrlValue( Map<String, String> parameterMap )
                            throws OWSException {
        String remoteWfsUrl = parameterMap.get( "REMOTE_OWS_URL" );
        if ( remoteWfsUrl == null ) {
            throw new OWSException( "Mandatory parameter REMOTE_OWS_URL is missing", MISSING_PARAMETER_VALUE );
        }
        try {
            return new URL( remoteWfsUrl );
        } catch ( MalformedURLException e ) {
            throw new OWSException( "REMOTE_OWS_URL is not a valid url", INVALID_PARAMETER_VALUE );
        }
    }

    private List<SldNamedLayer> parseSld( Map<String, String> parameterMap )
                            throws OWSException {
        String sld = parameterMap.get( "SLD" );
        String sldBody = parameterMap.get( "SLD_BODY" );
        if ( sld != null && sldBody != null ) {
            throw new OWSException( "SLD and SLD_Body are set. Must be one of it!", MISSING_PARAMETER_VALUE );
        }
        if ( sld != null ) {
            // return sldParser.parseFromExternalReference( sld );
        }
        if ( sldBody != null ) {
            try {
                return sldParser.parseSld( sldBody );
            } catch ( XMLStreamException e ) {
                LOG.info( "Could not parse SLD_BODY parameter value {} as SLD: {}", sldBody, e.getMessage() );
                throw new OWSException( "SLD_BODY could not be parsed as SLD!", MISSING_PARAMETER_VALUE );
            }
        }
        throw new OWSException( "Either SLD or SLD_BODY must be set!", MISSING_PARAMETER_VALUE );
    }

    private ICRS parseCrs( Map<String, String> parameterMap )
                            throws OWSException {
        String crs = parameterMap.get( "CRS" );
        if ( crs == null ) {
            throw new OWSException( "Mandatory parameter CRS is missing", MISSING_PARAMETER_VALUE );
        }
        try {
            return CRSManager.lookup( crs );
        } catch ( UnknownCRSException e ) {
            throw new OWSException( "CRS '" + crs + "' is not known!", INVALID_PARAMETER_VALUE );
        }
    }

    private Envelope parseBbox( Map<String, String> parameterMap, ICRS crs )
                            throws OWSException {
        String bbox = parameterMap.get( "BBOX" );
        if ( bbox == null ) {
            throw new OWSException( "Mandatory parameter SLD is missing", MISSING_PARAMETER_VALUE );
        }
        String[] coordinates = bbox.split( "," );
        if ( coordinates.length != 4 ) {
            String msg = "Invalid parameter BBOX, must be a valid BoundingBox [minx,miny,maxx,maxy], but was " + bbox;
            throw new OWSException( msg, INVALID_PARAMETER_VALUE );
        }
        double minx = parseAsDouble( coordinates, 0, bbox );
        double miny = parseAsDouble( coordinates, 1, bbox );
        double maxx = parseAsDouble( coordinates, 2, bbox );
        double maxy = parseAsDouble( coordinates, 3, bbox );
        return geometryFactory.createEnvelope( minx, miny, maxx, maxy, crs );
    }

    private double parseAsDouble( String[] coordinates, int index, String bbox )
                            throws OWSException {
        try {
            return parseDouble( coordinates[index] );
        } catch ( NumberFormatException e ) {
            String msg = "Invalid parameter BBOX, must be a valid BoundingBox [minx,miny,maxx,maxy], but was " + bbox;
            throw new OWSException( msg, INVALID_PARAMETER_VALUE );
        }
    }

    private int parseIntValue( Map<String, String> parameterMap, String key )
                            throws OWSException {
        String valueAsString = parameterMap.get( key );
        try {
            return parseInt( valueAsString );
        } catch ( NumberFormatException e ) {
            throw new OWSException( "Value of parmeter '" + key + "' must be an integer value, but is " + valueAsString
                                    + "!", INVALID_PARAMETER_VALUE );
        }
    }

}