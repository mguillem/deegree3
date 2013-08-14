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
import static java.util.Collections.emptyMap;
import static org.deegree.protocol.wms.WMSConstants.VERSION_130;
import static org.deegree.protocol.wms.ops.FeaturePortrayalGetMap.DEFAULT_FORMAT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.tom.ows.Version;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryFactory;
import org.deegree.style.StyleRef;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsRequestBuilderTest {

    private static final String VALID_WFS_URL = "http://Fanothersite.com/WFS";

    private static final String TYPE_WFS = "WFS";

    private static final String VALID_FORMAT = "image/jpg;";

    private static final int VALID_HEIGHT = 200;

    private static final int VALID_WIDTH = 100;

    private static final String VALID_BBOX = "6.5,53.1,7.0,53.9";

    private static final String VALID_CRS = "EPSG:4326";

    private static final String VALID_SLD_BODY = "%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%3CStyledLayerDescriptor+version%3D%221.1.0%22%3E%3CNamedLayer%3E%3CName%3ERivers%3C%2FName%3E%3CNamedStyle%3E%3CName%3ECenterLine%3C%2FName%3E%3C%2FNamedStyle%3E%3C%2FNamedLayer%3E%3CNamedLayer%3E%3CName%3ERoads%3C%2FName%3E%3CNamedStyle%3E%3CName%3ECenterLine%3C%2FName%3E%3C%2FNamedStyle%3E%3C%2FNamedLayer%3E%3CNamedLayer%3E%3CName%3EHouses%3C%2FName%3E%3CNamedStyle%3E%3CName%3EOutline%3C%2FName%3E%3C%2FNamedStyle%3E%3C%2FNamedLayer%3E%3C%2FStyledLayerDescriptor%3E";

    private static WmsRequestBuilder wmsRequestBuilder;

    private static URL VALID_SLD_REF;

    @BeforeClass
    public static void initWmsRequestBuilder() {
        SLDParser sldParser = Mockito.mock( SLDParser.class );
//        when( sldParser.parseFromExternalReference( anyString() ) ).thenReturn( new StyleRef( "TEST" ) );
//        when( sldParser.parseFromString( anyString() ) ).thenReturn( new StyleRef( "TEST" ) );

        wmsRequestBuilder = new WmsRequestBuilder( sldParser );
        VALID_SLD_REF = WmsRequestBuilderTest.class.getResource( "example-sld.xml" );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithUnsupportedVersionShouldFail()
                            throws Exception {
        Version version = new Version( 0, 0, 0 );
        Map<String, String> map = emptyMap();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, version );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithUnsupportedRemoteWfsTypeShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithInvalidRemoteWfsType();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithoutRemoteWfsTypeShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithoutRemoteWfsType();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithInvalidRemoteWfsUrlShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithInvalidRemoteWfsUrl();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithoutRemoteWfsUrlShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithoutRemoteWfsUrl();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithInvalidCrsShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithInvalidCrs();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithoutCrsShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithoutCrs();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithInvalidBboxMissingValueShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithInvalidBboxMissingValue();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithInvalidBboxInvalidDoubleShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithInvalidBboxInvalidDouble();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithoutBboxShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithoutBbox();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test(expected = OWSException.class)
    public void testBuildFeaturePortrayalGetMapRequestWithSldAndSldBodyShouldFail()
                            throws Exception {
        Map<String, String> map = createFpsGetMapParameterMapWithSldAndSldBody();
        wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
    }

    @Test
    public void testBuildFeaturePortrayalGetMapRequestWithSldBody()
                            throws Exception {
        Map<String, String> map = createValidFpsGetMapParameterMapWithSldBody();
        FeaturePortrayalGetMap request = wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( map, VERSION_130 );
        assertThat( request.getStyle(), notNullValue() );
    }

    @Test
    public void testBuildFeaturePortrayalGetMapRequestWithValidParameters()
                            throws Exception {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        FeaturePortrayalGetMap getMapRequest = wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( parameterMap,
                                                                                                     VERSION_130 );
        assertThat( getMapRequest.getFormat(), is( VALID_FORMAT ) );
        assertThat( getMapRequest.getWidth(), is( VALID_WIDTH ) );
        assertThat( getMapRequest.getHeight(), is( VALID_HEIGHT ) );
        assertThat( getMapRequest.getRemoteWfsUrl(), is( new URL( VALID_WFS_URL ) ) );
        assertThat( getMapRequest.getBbox(), isEnvelope( VALID_BBOX, VALID_CRS ) );
        assertThat( getMapRequest.getCrs(), isCrs( VALID_CRS ) );
        // assertThat( getMapRequest.getStyle(), isValidStyleFromRef() );
    }

    @Test
    public void testBuildFeaturePortrayalGetMapRequestWithoutFormatShoudBeDefault()
                            throws Exception {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMapWithoutFormat();
        FeaturePortrayalGetMap getMapRequest = wmsRequestBuilder.buildFeaturePortrayalGetMapRequest( parameterMap,
                                                                                                     VERSION_130 );
        assertThat( getMapRequest.getFormat(), is( DEFAULT_FORMAT ) );
    }

    private Map<String, String> createFpsGetMapParameterMapWithInvalidRemoteWfsType() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.put( "REMOTE_OWS_TYPE", "UNKNOWN" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithoutRemoteWfsType() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.remove( "REMOTE_OWS_TYPE" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithInvalidRemoteWfsUrl() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.put( "REMOTE_OWS_URL", "NOT A VALID URL" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithoutRemoteWfsUrl() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.remove( "REMOTE_OWS_URL" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithInvalidCrs() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.put( "CRS", "UNKNOWN CRS" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithoutCrs() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.remove( "CRS" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithInvalidBboxMissingValue() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.put( "BBOX", "5.5,3.2,7.8,D" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithInvalidBboxInvalidDouble() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.put( "BBOX", "5.5,3.2,7.8,D" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithoutBbox() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.remove( "BBOX" );
        return parameterMap;
    }

    private Map<String, String> createFpsGetMapParameterMapWithSldAndSldBody() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.put( "SLD_BODY", VALID_SLD_BODY );
        return parameterMap;
    }

    private Map<String, String> createValidFpsGetMapParameterMapWithSldBody() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.remove( "SLD" );
        parameterMap.put( "SLD_BODY", VALID_SLD_BODY );
        return parameterMap;
    }

    private Map<String, String> createValidFpsGetMapParameterMapWithoutFormat() {
        Map<String, String> parameterMap = createValidFpsGetMapParameterMap();
        parameterMap.remove( "FORMAT" );
        return parameterMap;
    }

    private Map<String, String> createValidFpsGetMapParameterMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put( "CRS", VALID_CRS );
        map.put( "BBOX", VALID_BBOX );
        map.put( "SLD", VALID_SLD_REF.toExternalForm() );
        map.put( "WIDTH", Integer.toString( VALID_WIDTH ) );
        map.put( "HEIGHT", Integer.toString( VALID_HEIGHT ) );
        map.put( "FORMAT", VALID_FORMAT );
        map.put( "REMOTE_OWS_TYPE", TYPE_WFS );
        map.put( "REMOTE_OWS_URL", VALID_WFS_URL );
        return map;
    }

    private Matcher<Envelope> isEnvelope( final String expectedBbox, final String expectedCrs ) {
        return new BaseMatcher<Envelope>() {

            @Override
            public boolean matches( Object item ) {
                Envelope actualBbox = (Envelope) item;
                Envelope expectedBbox = createExpectedCrs();
                return expectedBbox.equals( actualBbox );
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Expected BouniidngBox is " + expectedBbox );
            }

            private Envelope createExpectedCrs() {
                GeometryFactory gf = new GeometryFactory();
                ICRS crs = null;
                try {
                    crs = CRSManager.lookup( expectedCrs );
                } catch ( UnknownCRSException e ) {
                }
                String[] bboxParts = expectedBbox.split( "," );
                return gf.createEnvelope( parseDouble( bboxParts[0] ), parseDouble( bboxParts[1] ),
                                          parseDouble( bboxParts[2] ), parseDouble( bboxParts[3] ), crs );
            }

        };
    }

    private Matcher<ICRS> isCrs( final String expectedCrsName ) {
        return new BaseMatcher<ICRS>() {

            @Override
            public boolean matches( Object item ) {
                ICRS crs = (ICRS) item;
                return expectedCrsName.equalsIgnoreCase( crs.getAlias() );
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Expected name of the CRS is '" + expectedCrsName + "'" );
            }

        };
    }

    private Matcher<StyleRef> isValidStyleFromRef() {
        return new BaseMatcher<StyleRef>() {

            @Override
            public boolean matches( Object item ) {
                StyleRef styleRef = (StyleRef) item;
                // TODO: Is GEOSYM the correct name of the parsed sld?
                return "GEOSYM".equals( styleRef.getName() );
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Name of the parsed SLD nmust be 'GEOSYM'" );
            }
        };
    }

}
