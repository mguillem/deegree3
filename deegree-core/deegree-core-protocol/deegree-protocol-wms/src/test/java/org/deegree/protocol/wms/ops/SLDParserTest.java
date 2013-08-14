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

import static org.deegree.protocol.wms.ops.SLDParser.parse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.commons.utils.Triple;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.PropertyIsLessThanOrEqualTo;
import org.deegree.layer.LayerRef;
import org.deegree.style.StyleRef;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SLDParserTest {

    @Test
    public void testParseSimpleSldWithNamedStyle()
                            throws Exception {
        RequestBase gm = mock( RequestBase.class );
        XMLStreamReader in = readSldWithOneNamedLayerWithOneNamedStyle();
        Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parsedSld = parse( in, gm );

        LinkedList<LayerRef> layers = parsedSld.first;
        assertThat( layers.size(), is( 1 ) );
        assertThat( layers.get( 0 ).getName(), is( "OCEANSEA_1M:Foundation" ) );

        LinkedList<StyleRef> styles = parsedSld.second;
        assertThat( styles.size(), is( 1 ) );
        assertThat( styles.get( 0 ).getName(), is( "GEOSYM" ) );
        assertThat( styles.get( 0 ).getStyle(), nullValue() );

        LinkedList<OperatorFilter> filters = parsedSld.third;
        assertThat( filters.size(), is( 1 ) );
        assertThat( filters.get( 0 ), nullValue() );
    }

    @Test
    public void testParseSimpleSldWithOnlyOneNamedLayer()
                            throws Exception {
        RequestBase gm = mock( RequestBase.class );
        XMLStreamReader in = readSldWithOneNamedLayerWithOneUserStyle();
        Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parsedSld = parse( in, gm );

        LinkedList<LayerRef> layers = parsedSld.first;
        assertThat( layers.size(), is( 1 ) );
        assertThat( layers.get( 0 ).getName(), is( "OCEANSEA_1M:Foundation" ) );

        LinkedList<StyleRef> styles = parsedSld.second;
        assertThat( styles.size(), is( 1 ) );
        // First se:FeatureTypeStyle does not have a name
        assertThat( styles.get( 0 ).getName(), nullValue() );
        assertThat( styles.get( 0 ).getStyle(), notNullValue() );

        LinkedList<OperatorFilter> filters = parsedSld.third;
        assertThat( filters.size(), is( 1 ) );
        assertThat( filters.get( 0 ), nullValue() );
    }

    @Test
    public void testParseSldWithOneUserStyle()
                            throws Exception {
        RequestBase gm = mock( RequestBase.class );
        XMLStreamReader in = readSldWithOneNamedLayerWithOneUserStyleAndFilter();
        Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parsedSld = parse( in, gm );

        LinkedList<LayerRef> layers = parsedSld.first;
        assertThat( layers.size(), is( 1 ) );
        assertThat( layers.get( 0 ).getName(), is( "StateBoundary" ) );

        LinkedList<StyleRef> styles = parsedSld.second;
        assertThat( styles.size(), is( 1 ) );
        assertThat( styles.get( 0 ).getName(), is( "SGID024_StateBoundary" ) );
        assertThat( styles.get( 0 ).getStyle(), notNullValue() );

        LinkedList<OperatorFilter> filters = parsedSld.third;
        assertThat( filters.size(), is( 1 ) );
        assertThat( filters.get( 0 ), hasOperation( PropertyIsLessThanOrEqualTo.class ) );
    }

    @Test
    public void testParseSldWithTwoUserStyles()
                            throws Exception {
        RequestBase gm = mock( RequestBase.class );
        XMLStreamReader in = readSldWithOneNamedLayerWithTwoUserStyles();
        Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parsedSld = parse( in, gm );
        LinkedList<LayerRef> layers = parsedSld.first;
        assertThat( layers.size(), is( 1 ) );
        assertThat( layers.get( 0 ).getName(), is( "ElevationContours" ) );

        LinkedList<StyleRef> styles = parsedSld.second;
        assertThat( styles.size(), is( 1 ) );
        assertThat( styles.get( 0 ).getName(), is( "default:ElevationContours" ) );
        assertThat( styles.get( 0 ).getStyle(), notNullValue() );
        // TODO: Currently only the first user style is parsed (is this expected behavior?)
        // assertThat( styles.get( 1 ).getName(), is( "ElevationContoursSimple" ) );
        // assertThat( styles.get( 1 ).getStyle(), notNullValue() );

        LinkedList<OperatorFilter> filters = parsedSld.third;
        assertThat( filters.size(), is( 1 ) );
        assertThat( filters.get( 0 ), nullValue() );
    }

    @Test
    public void testParseSldWithTwoNamedLayers()
                            throws Exception {
        RequestBase gm = mock( RequestBase.class );
        XMLStreamReader in = readSldWithTwoNamedLayers();
        Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> parsedSld = parse( in, gm );

        LinkedList<LayerRef> layers = parsedSld.first;
        assertThat( layers.size(), is( 2 ) );
        assertThat( layers.get( 0 ).getName(), is( "OCEANSEA_1M:Foundation1" ) );
        assertThat( layers.get( 1 ).getName(), is( "OCEANSEA_1M:Foundation2" ) );

        LinkedList<StyleRef> styles = parsedSld.second;
        assertThat( styles.size(), is( 2 ) );
        assertThat( styles.get( 0 ).getName(), is( "FoundationName_NL1" ) );
        assertThat( styles.get( 0 ).getStyle(), notNullValue() );
        assertThat( styles.get( 1 ).getName(), is( "FoundationName_NL2" ) );
        assertThat( styles.get( 1 ).getStyle(), notNullValue() );

        LinkedList<OperatorFilter> filters = parsedSld.third;
        assertThat( filters.size(), is( 2 ) );
        assertThat( filters.get( 0 ), hasOperation( PropertyIsLessThanOrEqualTo.class ) );
        assertThat( filters.get( 1 ), nullValue() );
    }

    private XMLStreamReader readSldWithOneNamedLayerWithOneUserStyle()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-oneNamedLayerOneUserStyle.xml" );
    }

    private XMLStreamReader readSldWithOneNamedLayerWithOneUserStyleAndFilter()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-oneNamedLayerWithFilter.xml" );
    }

    private XMLStreamReader readSldWithOneNamedLayerWithTwoUserStyles()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-oneNamedLayerTwoUserStyles.xml" );
    }

    private XMLStreamReader readSldWithTwoNamedLayers()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-twoNamedLayers.xml" );
    }

    private XMLStreamReader readSldWithOneNamedLayerWithOneNamedStyle()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-oneNamedLayerWithNamedStyle.xml" );
    }

    private XMLStreamReader readSld( String name )
                            throws XMLStreamException, FactoryConfigurationError {
        InputStream sldInputStream = SLDParser.class.getResourceAsStream( name );
        return XMLInputFactory.newInstance().createXMLStreamReader( sldInputStream );
    }

    private Matcher<OperatorFilter> hasOperation( final Class<?> expectedClass ) {
        return new BaseMatcher<OperatorFilter>() {

            @Override
            public boolean matches( Object item ) {
                Operator operator = ( (OperatorFilter) item ).getOperator();
                return expectedClass.equals( operator.getClass() );
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Must be an instance of " + expectedClass );
            }
        };
    }

}