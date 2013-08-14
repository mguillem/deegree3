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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.deegree.commons.xml.CommonNamespaces.GMLNS;
import static org.deegree.commons.xml.stax.XMLStreamUtils.skipElement;
import static org.deegree.layer.dims.Dimension.parseTyped;
import static org.deegree.protocol.wms.ops.GetMap.parseDimensionValues;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.commons.annotations.LoggingNotes;
import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.tom.primitive.PrimitiveValue;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.Triple;
import org.deegree.commons.xml.NamespaceBindings;
import org.deegree.filter.Filter;
import org.deegree.filter.IdFilter;
import org.deegree.filter.MatchAction;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.ResourceId;
import org.deegree.filter.comparison.PropertyIsEqualTo;
import org.deegree.filter.expression.Literal;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.logical.Or;
import org.deegree.filter.xml.Filter110XMLDecoder;
import org.deegree.layer.LayerRef;
import org.deegree.style.StyleRef;
import org.deegree.style.se.parser.SymbologyParser;
import org.deegree.style.se.unevaluated.Style;
import org.slf4j.Logger;

/**
 * <code>SLDParser</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: aschmitz $
 * 
 * @version $Revision: 31785 $, $Date: 2011-09-06 20:21:16 +0200 (Tue, 06 Sep 2011) $
 */
@LoggingNotes(debug = "logs which named layers were extracted from SLD")
public class SLDParser {

    private static final Logger LOG = getLogger( SLDParser.class );

    /**
     * @param in
     * @param service
     * @param gm
     *            filters will be added to this GetMap instance
     * @return a list of layers parsed from SLD
     * @throws XMLStreamException
     * @throws OWSException
     * @throws ParseException
     */
    public static Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parse( XMLStreamReader in,
                                                                                                        RequestBase gm )
                            throws XMLStreamException, OWSException, ParseException {
        while ( !in.isStartElement() || in.getLocalName() == null || !( isNamedLayer( in ) || isUserLayer( in ) ) ) {
            in.nextTag();
        }

        LinkedList<LayerRef> layers = new LinkedList<LayerRef>();
        LinkedList<StyleRef> styles = new LinkedList<StyleRef>();
        LinkedList<OperatorFilter> filters = new LinkedList<OperatorFilter>();

        while ( isNamedLayer( in ) || isUserLayer( in ) ) {
            if ( isNamedLayer( in ) ) {
                parseNamedLayer( in, gm, layers, styles, filters );
            } else {
                throw new OWSException( "UserLayer requests are currently not supported.",
                                        OWSException.NO_APPLICABLE_CODE );
            }
        }
        return new Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>>( layers, styles,
                                                                                                   filters );
    }

    /**
     * @param in
     * @param layerName
     * @param styleNames
     * @return the filters defined for the NamedLayer, and the matching styles
     * @throws XMLStreamException
     */
    public static Pair<LinkedList<Filter>, LinkedList<StyleRef>> getStyles( XMLStreamReader in, String layerName,
                                                                            Map<String, String> styleNames )
                            throws XMLStreamException {
        while ( !in.isStartElement() || in.getLocalName() == null || !( isNamedLayer( in ) || isUserLayer( in ) ) ) {
            in.nextTag();
        }

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

    private static void parseNamedLayer( XMLStreamReader in, RequestBase gm, LinkedList<LayerRef> layers,
                                         LinkedList<StyleRef> styles, LinkedList<OperatorFilter> filters )
                            throws XMLStreamException, OWSException, ParseException {
        in.nextTag();

        String layerName = parseRequiredLayerName( in );
        in.nextTag();

        LOG.debug( "Extracted layer '{}' from SLD.", layerName );
        skip( in, "Description" );

        OperatorFilter operatorFilter = null;
        if ( isLayerFeatureConstraints( in ) ) {
            while ( !( in.isEndElement() && isLayerFeatureConstraints( in ) ) ) {
                in.nextTag();
                while ( !( in.isEndElement() && isFeatureTypeConstraint( in ) ) ) {
                    in.nextTag();
                    skipFeatureTypeName( in );
                    if ( isFilter( in ) ) {
                        operatorFilter = parseFilter( in, operatorFilter );
                    }
                    if ( isExtent( in ) ) {
                        parseExtentAndAddDimensions( in, gm );
                    }
                }
                in.nextTag();
            }
            in.nextTag();
        }
        if ( isNamedStyle( in ) ) {
            parseAndAddNamedStyle( in, layers, styles, filters, layerName, operatorFilter );
        }
        if ( isUserStyle( in ) ) {
            parseAndAddUserStyle( in, layers, styles, filters, layerName, operatorFilter );
        }
        in.nextTag();
    }

    private static void parseExtentAndAddDimensions( XMLStreamReader in, RequestBase gm )
                            throws XMLStreamException, OWSException, ParseException {
        in.nextTag();

        in.require( START_ELEMENT, null, "Name" );
        String name = in.getElementText().toUpperCase();
        in.nextTag();
        in.require( START_ELEMENT, null, "Value" );
        String value = in.getElementText();
        in.nextTag();
        in.require( END_ELEMENT, null, "Extent" );

        List<?> list = parseDimensionValues( value, name.toLowerCase() );
        if ( name.toUpperCase().equals( "TIME" ) ) {
            gm.addDimensionValue( "time", (List<?>) parseTyped( list, true ) );
        } else {
            List<?> values = (List<?>) parseTyped( list, false );
            gm.addDimensionValue( name, values );
        }
    }

    private static OperatorFilter parseFilter( XMLStreamReader in, OperatorFilter operatorFilter )
                            throws XMLStreamException {
        Filter filter = Filter110XMLDecoder.parse( in );
        if ( filter instanceof OperatorFilter ) {
            operatorFilter = (OperatorFilter) filter;
        } else if ( filter instanceof IdFilter ) {
            IdFilter idFilter = (IdFilter) filter;
            List<ResourceId> ids = idFilter.getSelectedIds();

            NamespaceBindings nsContext = new NamespaceBindings();
            nsContext.addNamespace( "gml", GMLNS );
            ValueReference idReference = new ValueReference( "@gml:id", nsContext );

            int idCount = ids.size(), i = 0;
            Operator[] operators = new Operator[idCount];
            for ( ResourceId id : ids ) {
                operators[i++] = new PropertyIsEqualTo( idReference, new Literal<PrimitiveValue>( id.getRid() ),
                                                        Boolean.TRUE, MatchAction.ONE );
            }

            if ( idCount == 1 ) {
                operatorFilter = new OperatorFilter( operators[0] );
            } else {
                operatorFilter = new OperatorFilter( new Or( operators ) );
            }
        }
        return operatorFilter;
    }

    private static void parseAndAddUserStyle( XMLStreamReader in, LinkedList<LayerRef> layers,
                                              LinkedList<StyleRef> styles, LinkedList<OperatorFilter> filters,
                                              String layerName, OperatorFilter operatorFilter )
                            throws XMLStreamException {
        while ( !( in.isEndElement() && isUserStyle( in ) ) ) {
            in.nextTag();

            skip( in, "Name" );
            skip( in, "Description" );
            skip( in, "Title" );
            skip( in, "Abstract" );
            skip( in, "IsDefault" );

            if ( isFeatureTypeStyle( in ) || isCoverageStyle( in ) || isOnlineResource( in ) ) {
                Style style = SymbologyParser.INSTANCE.parseFeatureTypeOrCoverageStyle( in );
                layers.add( new LayerRef( layerName ) );
                styles.add( new StyleRef( style ) );
                filters.add( operatorFilter );
            }
        }
        in.nextTag();
    }

    private static void parseAndAddNamedStyle( XMLStreamReader in, LinkedList<LayerRef> layers,
                                               LinkedList<StyleRef> styles, LinkedList<OperatorFilter> filters,
                                               String layerName, OperatorFilter operatorFilter )
                            throws XMLStreamException {
        in.nextTag();
        String name = in.getElementText();
        layers.add( new LayerRef( layerName ) );
        styles.add( new StyleRef( name ) );
        filters.add( operatorFilter );

        in.nextTag(); // out of name
        in.nextTag(); // out of named style
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
