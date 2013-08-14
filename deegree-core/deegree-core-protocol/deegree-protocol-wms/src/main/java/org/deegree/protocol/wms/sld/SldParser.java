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
package org.deegree.protocol.wms.sld;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.deegree.commons.xml.CommonNamespaces.GMLNS;
import static org.deegree.commons.xml.stax.XMLStreamUtils.skipElement;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.tom.primitive.PrimitiveValue;
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
 * Parser for SLD 1.1.0
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SldParser {

    private static final Logger LOG = getLogger( SldParser.class );

    /**
     * Parses layer, style and filter information. Currently only NamedLayer, not UserLayers are parsed.
     * 
     * @param sldToParse
     *            never <code>null</code>
     * @return the parsed layer, style and filter information, for each NamedLayer one {@link SldNamedLayer} instance.
     * @throws XMLStreamException
     *             if a exception occurred during parsing the sld
     * @throws OWSException
     *             if the SLD contains a UserLayer
     * @throws ParseException
     */
    public List<SldNamedLayer> parseSld( XMLStreamReader sldToParse )
                            throws XMLStreamException, OWSException, ParseException {
        List<SldNamedLayer> sldNamedLayers = new ArrayList<SldNamedLayer>();

        fastForwardUntilLayers( sldToParse );
        while ( isNamedLayer( sldToParse ) || isUserLayer( sldToParse ) ) {
            if ( isNamedLayer( sldToParse ) ) {
                List<SldNamedLayer> parsedLayer = parseNamedLayer( sldToParse );
                sldNamedLayers.addAll( parsedLayer );
            } else {
                throw new OWSException( "UserLayer requests are currently not supported.",
                                        OWSException.NO_APPLICABLE_CODE );
            }
        }
        return sldNamedLayers;
    }

    private void fastForwardUntilLayers( XMLStreamReader in )
                            throws XMLStreamException {
        while ( !in.isStartElement() || in.getLocalName() == null || !( isNamedLayer( in ) || isUserLayer( in ) ) ) {
            in.nextTag();
        }
    }

    private List<SldNamedLayer> parseNamedLayer( XMLStreamReader in )
                            throws XMLStreamException, OWSException, ParseException {
        List<SldNamedLayer> sldNamedLayers = new ArrayList<SldNamedLayer>();

        in.nextTag();

        String layerName = parseRequiredLayerName( in );
        in.nextTag();

        LOG.debug( "Extracted layer '{}' from SLD.", layerName );
        skip( in, "Description" );

        OperatorFilter operatorFilter = null;
        Map<String, String> extents = new HashMap<String, String>();
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
                        parseExtentAndAddDimensions( in, extents );
                    }
                }
                in.nextTag();
            }
            in.nextTag();
        }
        if ( isNamedStyle( in ) ) {
            sldNamedLayers.add( parseNamedStyle( in, layerName, operatorFilter, extents ) );
        }
        if ( isUserStyle( in ) ) {
            sldNamedLayers.addAll( parseUserStyle( in, layerName, operatorFilter, extents ) );
        }
        in.nextTag();
        return sldNamedLayers;
    }

    private void parseExtentAndAddDimensions( XMLStreamReader in, Map<String, String> extents )
                            throws XMLStreamException, OWSException, ParseException {
        in.nextTag();

        in.require( START_ELEMENT, null, "Name" );
        String name = in.getElementText().toUpperCase();
        in.nextTag();
        in.require( START_ELEMENT, null, "Value" );
        String value = in.getElementText();
        in.nextTag();
        in.require( END_ELEMENT, null, "Extent" );

        extents.put( name, value );
    }

    private OperatorFilter parseFilter( XMLStreamReader in, OperatorFilter operatorFilter )
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

    private List<SldNamedLayer> parseUserStyle( XMLStreamReader in, String layerName, OperatorFilter operatorFilter,
                                                Map<String, String> extents )
                            throws XMLStreamException {
        List<SldNamedLayer> sldNamedLayers = new ArrayList<SldNamedLayer>();
        while ( !( in.isEndElement() && isUserStyle( in ) ) ) {
            in.nextTag();

            skip( in, "Name" );
            skip( in, "Description" );
            skip( in, "Title" );
            skip( in, "Abstract" );
            skip( in, "IsDefault" );

            if ( isFeatureTypeStyle( in ) || isCoverageStyle( in ) || isOnlineResource( in ) ) {
                Style style = SymbologyParser.INSTANCE.parseFeatureTypeOrCoverageStyle( in );
                LayerRef layerRef = new LayerRef( layerName );
                StyleRef styleRef = new StyleRef( style );
                sldNamedLayers.add( new SldNamedLayer( layerRef, styleRef, operatorFilter, extents ) );
            }
        }
        in.nextTag();
        return sldNamedLayers;
    }

    private SldNamedLayer parseNamedStyle( XMLStreamReader in, String layerName, OperatorFilter operatorFilter,
                                           Map<String, String> extents )
                            throws XMLStreamException {
        in.nextTag();
        String styleName = in.getElementText();
        in.nextTag(); // out of name
        in.nextTag(); // out of named style

        return new SldNamedLayer( new LayerRef( layerName ), new StyleRef( styleName ), operatorFilter, extents );
    }

    private String parseRequiredLayerName( XMLStreamReader in )
                            throws XMLStreamException {
        in.require( START_ELEMENT, null, "Name" );
        return in.getElementText();
    }

    private boolean isUserLayer( XMLStreamReader in ) {
        return in.getLocalName().equals( "UserLayer" );
    }

    private boolean isNamedLayer( XMLStreamReader in ) {
        return in.getLocalName().equals( "NamedLayer" );
    }

    private boolean isUserStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "UserStyle" );
    }

    private boolean isNamedStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "NamedStyle" );
    }

    private boolean isExtent( XMLStreamReader in ) {
        return in.getLocalName().equals( "Extent" );
    }

    private boolean isFilter( XMLStreamReader in ) {
        return in.getLocalName().equals( "Filter" );
    }

    private boolean isFeatureTypeConstraint( XMLStreamReader in ) {
        return in.getLocalName().equals( "FeatureTypeConstraint" );
    }

    private boolean isLayerFeatureConstraints( XMLStreamReader in ) {
        return in.getLocalName().equals( "LayerFeatureConstraints" );
    }

    private boolean isOnlineResource( XMLStreamReader in ) {
        return in.getLocalName().equals( "OnlineResource" );
    }

    private boolean isCoverageStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "CoverageStyle" );
    }

    private boolean isFeatureTypeStyle( XMLStreamReader in ) {
        return in.getLocalName().equals( "FeatureTypeStyle" );
    }

    private void skipFeatureTypeName( XMLStreamReader in )
                            throws XMLStreamException {
        // skip feature type name, it is useless in this context (or is it?) TODO
        if ( in.getLocalName().equals( "FeatureTypeName" ) ) {
            in.getElementText();
            in.nextTag();
        }
    }

    private void skip( XMLStreamReader in, String name )
                            throws XMLStreamException {
        if ( in.getLocalName().equals( name ) ) {
            skipElement( in );
        }
    }

}
