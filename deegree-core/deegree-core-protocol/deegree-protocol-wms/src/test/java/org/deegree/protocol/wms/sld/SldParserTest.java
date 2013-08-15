package org.deegree.protocol.wms.sld;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.PropertyIsLessThanOrEqualTo;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;

public class SldParserTest {

    private static SldParser sldParser;

    @BeforeClass
    public static void initParser() {
        sldParser = new SldParser();
    }

    @Test
    public void testParseSimpleSldWithNamedStyle()
                            throws Exception {
        XMLStreamReader in = readSldWithOneNamedLayerWithOneNamedStyle();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( in );

        assertThat( parsedSld.size(), is( 1 ) );
        SldNamedLayer layer = parsedSld.get( 0 );

        assertThat( layer.getLayer().getName(), is( "OCEANSEA_1M:Foundation" ) );
        assertThat( layer.getStyle().getName(), is( "GEOSYM" ) );
        assertThat( layer.getStyle().getStyle(), nullValue() );
        assertThat( layer.getFilter(), nullValue() );
    }

    @Test
    public void testParseSimpleSldWithOnlyOneNamedLayer()
                            throws Exception {
        XMLStreamReader in = readSldWithOneNamedLayerWithOneUserStyle();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( in );

        assertThat( parsedSld.size(), is( 1 ) );
        SldNamedLayer layer = parsedSld.get( 0 );

        assertThat( layer.getLayer().getName(), is( "OCEANSEA_1M:Foundation" ) );

        // First se:FeatureTypeStyle does not have a name
        assertThat( layer.getStyle().getName(), nullValue() );
        assertThat( layer.getStyle().getStyle(), notNullValue() );

        assertThat( layer.getFilter(), nullValue() );
    }

    @Test
    public void testParseSldWithOneUserStyle()
                            throws Exception {
        XMLStreamReader in = readSldWithOneNamedLayerWithOneUserStyleAndFilter();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( in );

        assertThat( parsedSld.size(), is( 1 ) );
        SldNamedLayer layer = parsedSld.get( 0 );

        assertThat( layer.getLayer().getName(), is( "StateBoundary" ) );

        assertThat( layer.getStyle().getName(), is( "SGID024_StateBoundary" ) );
        assertThat( layer.getStyle().getStyle(), notNullValue() );

        assertThat( layer.getFilter(), hasOperation( PropertyIsLessThanOrEqualTo.class ) );
    }

    @Test
    public void testParseSldWithTwoUserStyles()
                            throws Exception {
        XMLStreamReader in = readSldWithOneNamedLayerWithTwoUserStyles();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( in );

        assertThat( parsedSld.size(), is( 1 ) );
        SldNamedLayer layer = parsedSld.get( 0 );

        assertThat( layer.getLayer().getName(), is( "ElevationContours" ) );

        assertThat( layer.getStyle().getName(), is( "default:ElevationContours" ) );
        assertThat( layer.getStyle().getStyle(), notNullValue() );
        // TODO: Currently only the first user style is parsed (is this expected behavior?)
        // assertThat( styles.get( 1 ).getName(), is( "ElevationContoursSimple" ) );
        // assertThat( styles.get( 1 ).getStyle(), notNullValue() );

        assertThat( layer.getFilter(), nullValue() );
    }

    @Test
    public void testParseSldWithExtent()
                            throws Exception {
        XMLStreamReader in = readSldWithOneNamedLayerWithOneUserStyleAndExtent();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( in );

        assertThat( parsedSld.size(), is( 1 ) );
        SldNamedLayer layer = parsedSld.get( 0 );

        Map<String, String> extents = layer.getExtents();
        assertThat( extents, hasItem( "DIM1", "TEST" ) );
        assertThat( extents, hasItem( "DIM2", "98" ) );
    }

    @Test
    public void testParseSldWithTwoNamedLayers()
                            throws Exception {
        XMLStreamReader in = readSldWithTwoNamedLayers();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( in );

        assertThat( parsedSld.size(), is( 2 ) );
        SldNamedLayer layerFirst = parsedSld.get( 0 );
        SldNamedLayer layerSecond = parsedSld.get( 1 );

        assertThat( layerFirst.getLayer().getName(), is( "OCEANSEA_1M:Foundation1" ) );
        assertThat( layerSecond.getLayer().getName(), is( "OCEANSEA_1M:Foundation2" ) );

        assertThat( layerFirst.getStyle().getName(), is( "FoundationName_NL1" ) );
        assertThat( layerFirst.getStyle().getStyle(), notNullValue() );
        assertThat( layerSecond.getStyle().getName(), is( "FoundationName_NL2" ) );
        assertThat( layerSecond.getStyle().getStyle(), notNullValue() );

        assertThat( layerFirst.getFilter(), hasOperation( PropertyIsLessThanOrEqualTo.class ) );
        assertThat( layerSecond.getFilter(), nullValue() );
    }

    @Test
    public void testParseFromStringSimpleSldWithOnlyOneNamedLayer()
                            throws Exception {
        String sld = readSldWithOneNamedLayerWithOneUserStyleAsString();
        List<SldNamedLayer> parsedSld = sldParser.parseSld( sld );

        assertThat( parsedSld.size(), is( 1 ) );
        SldNamedLayer layer = parsedSld.get( 0 );

        assertThat( layer.getLayer().getName(), is( "OCEANSEA_1M:Foundation" ) );

        // First se:FeatureTypeStyle does not have a name
        assertThat( layer.getStyle().getName(), nullValue() );
        assertThat( layer.getStyle().getStyle(), notNullValue() );

        assertThat( layer.getFilter(), nullValue() );
    }

    private XMLStreamReader readSldWithOneNamedLayerWithOneUserStyle()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-oneNamedLayerOneUserStyle.xml" );
    }

    private String readSldWithOneNamedLayerWithOneUserStyleAsString()
                            throws XMLStreamException, FactoryConfigurationError {
        InputStream sldInputStream = SldParser.class.getResourceAsStream( "sld-oneNamedLayerOneUserStyle.xml" );
        return convertInputStreamToString( sldInputStream );
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

    private XMLStreamReader readSldWithOneNamedLayerWithOneUserStyleAndExtent()
                            throws XMLStreamException, FactoryConfigurationError {
        return readSld( "sld-oneNamedLayerWithExtent.xml" );
    }

    private XMLStreamReader readSld( String name )
                            throws XMLStreamException, FactoryConfigurationError {
        InputStream sldInputStream = SldParser.class.getResourceAsStream( name );
        return XMLInputFactory.newInstance().createXMLStreamReader( sldInputStream );
    }

    private static String convertInputStreamToString( InputStream streamToConvert ) {
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader( streamToConvert ) );
            StringBuilder sb = new StringBuilder();
            String line;
            while ( ( line = br.readLine() ) != null ) {
                sb.append( line );
            }
            return sb.toString();
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly( br );
            IOUtils.closeQuietly( streamToConvert );
        }
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

    private Matcher<Map<String, String>> hasItem( final String expectedKey, final String expectedValue ) {
        return new BaseMatcher<Map<String, String>>() {

            @Override
            public boolean matches( Object item ) {
                @SuppressWarnings("unchecked")
                Map<String, String> actualMap = (Map<String, String>) item;
                if ( actualMap.containsKey( expectedKey ) )
                    return actualMap.get( expectedKey ).equals( expectedValue );
                return false;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Map must contain an entry " + expectedKey + ":" + expectedValue );
            }
        };
    }

}