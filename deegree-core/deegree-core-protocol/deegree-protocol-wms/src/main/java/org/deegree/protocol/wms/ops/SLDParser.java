//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/deegree3/trunk/deegree-services/deegree-services-wms/src/main/java/org/deegree/services/wms/controller/sld/SLDParser.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.deegree.commons.xml.stax.XMLStreamUtils.skipElement;
import static org.deegree.layer.dims.Dimension.parseTyped;
import static org.deegree.protocol.wms.ops.GetMap.parseDimensionValues;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.commons.annotations.LoggingNotes;
import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.Triple;
import org.deegree.filter.Filter;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.xml.Filter110XMLDecoder;
import org.deegree.layer.LayerRef;
import org.deegree.protocol.wms.sld.SldNamedLayer;
import org.deegree.protocol.wms.sld.SldParser;
import org.deegree.style.StyleRef;
import org.deegree.style.se.parser.SymbologyParser;
import org.deegree.style.se.unevaluated.Style;

/**
 * <code>SLDParser</code>
 * 
 * Use org.deegree.protocol.wms.sld.SldParser instead
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: aschmitz $
 * 
 * @version $Revision: 31785 $, $Date: 2011-09-06 20:21:16 +0200 (Tue, 06 Sep 2011) $
 */
@LoggingNotes(debug = "logs which named layers were extracted from SLD")
@Deprecated
public class SLDParser {

    /**
     * Don'use this method, it will be removed in further versions of deegree! Use
     * org.deegree.protocol.wms.ops.SLDParser.parseSld(XMLStreamReader, RequestBase) instead!
     * 
     * @param in
     * @param service
     * @param gm
     *            filters will be added to this GetMap instance
     * @return a list of layers parsed from SLD
     * @throws XMLStreamException
     * @throws OWSException
     * @throws ParseException
     */
    @Deprecated
    public static Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parse( XMLStreamReader in,
                                                                                                        RequestBase gm )
                            throws XMLStreamException, OWSException, ParseException {
        LinkedList<LayerRef> layers = new LinkedList<LayerRef>();
        LinkedList<StyleRef> styles = new LinkedList<StyleRef>();
        LinkedList<OperatorFilter> filters = new LinkedList<OperatorFilter>();

        SldParser sldParser = new SldParser();
        List<SldNamedLayer> sldNamedLayers = sldParser.parseSld( in );
        for ( SldNamedLayer sldNamedLayer : sldNamedLayers ) {
            layers.add( sldNamedLayer.getLayer() );
            styles.add( sldNamedLayer.getStyle() );
            filters.add( sldNamedLayer.getFilter() );
            Map<String, String> extents = sldNamedLayer.getExtents();
            for ( Entry<String, String> extent : extents.entrySet() ) {
                String name = extent.getKey();
                String value = extent.getValue();
                List<?> list = parseDimensionValues( value, name.toLowerCase() );
                if ( name.toUpperCase().equals( "TIME" ) ) {
                    gm.addDimensionValue( "time", (List<?>) parseTyped( list, true ) );
                } else {
                    List<?> values = (List<?>) parseTyped( list, false );
                    gm.addDimensionValue( name, values );
                }
            }
        }

        return new Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>>( layers, styles,
                                                                                                   filters );
    }

    /**
     * Don't use this method, it will be removed in one of the next versions!
     * 
     * @param in
     * @param layerName
     * @param styleNames
     * @return the filters defined for the NamedLayer, and the matching styles
     * @throws XMLStreamException
     */
    @Deprecated
    public static Pair<LinkedList<Filter>, LinkedList<StyleRef>> getStyles( XMLStreamReader in, String layerName,
                                                                            Map<String, String> styleNames )
                            throws XMLStreamException {
        fastForwardUntilLayers( in );

        LinkedList<StyleRef> styles = new LinkedList<StyleRef>();
        LinkedList<Filter> filters = new LinkedList<Filter>();

        while ( in.hasNext() && ( isNamedLayer( in ) && !in.isEndElement() ) || isUserLayer( in ) ) {
            if ( isUserLayer( in ) ) {
                skipElement( in );
            }
            if ( isNamedLayer( in ) ) {
                in.nextTag();

                String name = parseRequiredLayerName( in );
                if ( !name.equals( layerName ) ) {
                    while ( !( in.isEndElement() && isNamedLayer( in ) ) ) {
                        in.next();
                    }
                    in.nextTag();
                    continue;
                }
                in.nextTag();

                skip( in, "Description" );

                if ( isLayerFeatureConstraints( in ) ) {

                    while ( !( in.isEndElement() && isLayerFeatureConstraints( in ) ) ) {
                        in.nextTag();

                        while ( !( in.isEndElement() && isFeatureTypeConstraint( in ) ) ) {
                            in.nextTag();

                            skipFeatureTypeName( in );

                            if ( isFilter( in ) ) {
                                filters.add( Filter110XMLDecoder.parse( in ) );
                            }

                            if ( isExtent( in ) ) {
                                // skip extent, does not make sense to parse it here
                                skipElement( in );
                            }
                        }
                        in.nextTag();
                    }

                    in.nextTag();
                }

                if ( isNamedStyle( in ) ) {
                    // does not make sense to reference a named style when configuring it...
                    skipElement( in );
                }

                String styleName = null;

                while ( in.hasNext() && isUserStyle( in ) ) {

                    while ( in.hasNext() && !( in.isEndElement() && isUserStyle( in ) ) ) {

                        in.nextTag();

                        if ( in.getLocalName().equals( "Name" ) ) {
                            styleName = in.getElementText();
                            if ( !( styleNames.isEmpty() || styleNames.containsKey( styleName ) ) ) {
                                continue;
                            }
                        }

                        skip( in, "Description" );

                        // TODO skipped
                        if ( in.getLocalName().equals( "Title" ) ) {
                            in.getElementText();
                        }

                        // TODO skipped
                        if ( in.getLocalName().equals( "Abstract" ) ) {
                            in.getElementText();
                        }

                        // TODO skipped
                        if ( in.getLocalName().equals( "IsDefault" ) ) {
                            in.getElementText();
                        }

                        if ( isFeatureTypeStyle( in ) || isCoverageStyle( in ) || isOnlineResource( in ) ) {
                            Style style = SymbologyParser.INSTANCE.parseFeatureTypeOrCoverageStyle( in );
                            if ( styleNames.get( styleName ) != null ) {
                                style.setName( styleNames.get( styleName ) );
                            }
                            styles.add( new StyleRef( style ) );
                        }

                    }
                    in.nextTag();

                }

            }
        }

        return new Pair<LinkedList<Filter>, LinkedList<StyleRef>>( filters, styles );
    }

    private static void fastForwardUntilLayers( XMLStreamReader in )
                            throws XMLStreamException {
        while ( !in.isStartElement() || in.getLocalName() == null || !( isNamedLayer( in ) || isUserLayer( in ) ) ) {
            in.nextTag();
        }
    }

    private static String parseRequiredLayerName( XMLStreamReader in )
                            throws XMLStreamException {
        in.require( START_ELEMENT, null, "Name" );
        return in.getElementText();
    }

    private static boolean isUserLayer( XMLStreamReader in ) {
        return in.getLocalName().equals( "UserLayer" );
    }

    private static boolean isNamedLayer( XMLStreamReader in ) {
        return in.getLocalName().equals( "NamedLayer" );
    }

    private static boolean isUserStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "UserStyle" );
    }

    private static boolean isNamedStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "NamedStyle" );
    }

    private static boolean isExtent( XMLStreamReader in ) {
        return in.getLocalName().equals( "Extent" );
    }

    private static boolean isFilter( XMLStreamReader in ) {
        return in.getLocalName().equals( "Filter" );
    }

    private static boolean isFeatureTypeConstraint( XMLStreamReader in ) {
        return in.getLocalName().equals( "FeatureTypeConstraint" );
    }

    private static boolean isLayerFeatureConstraints( XMLStreamReader in ) {
        return in.getLocalName().equals( "LayerFeatureConstraints" );
    }

    private static boolean isOnlineResource( XMLStreamReader in ) {
        return in.getLocalName().equals( "OnlineResource" );
    }

    private static boolean isCoverageStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "CoverageStyle" );
    }

    private static boolean isFeatureTypeStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "FeatureTypeStyle" );
    }

    private static void skipFeatureTypeName( XMLStreamReader in )
                            throws XMLStreamException {
        // skip feature type name, it is useless in this context (or is it?) TODO
        if ( in.getLocalName().equals( "FeatureTypeName" ) ) {
            in.getElementText();
            in.nextTag();
        }
    }

    private static void skip( XMLStreamReader in, String name )
                            throws XMLStreamException {
        if ( in.getLocalName().equals( name ) ) {
            skipElement( in );
        }
    }

}