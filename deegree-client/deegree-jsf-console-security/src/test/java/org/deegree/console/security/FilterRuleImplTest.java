package org.deegree.console.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.commons.utils.kvp.KVPUtils;
import org.deegree.commons.xml.stax.XMLInputFactoryUtils;
import org.deegree.commons.xml.stax.XMLStreamUtils;
import org.deegree.console.security.services.FilterRuleImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml", "/testSecurityLayer.xml" })
public class FilterRuleImplTest {

    @Autowired
    private UserDetailsService userDetailsService;

    private static final String GETCAPABILITIES_OPERATION = "getcapabilities";

    private static final String GETRECORDS_OPERATION = "getrecords";

    private static final String GETRECORDBYID_OPERATION = "getrecordbyid";

    private static final String GETCAPABILITIES_ROLE = "ROLE_ANONYMOUS";

    private static final String GETRECORDS_ROLE = "ROLE_GETRECORDS";

    private static final String GETRECORDBYID_ROLE = "ROLE_GETRECORDBYID";

    private static final String GETRECORDS_USER = "getrecords";

    private static final String GETRECORDBYID_USER = "getrecordbyid";

    private static final String GETCAPABILITIES_POSTBODY = "<GetCapabilities xmlns=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd\"></GetCapabilities>";

    private static final String GETRECORDS_POSTBODY = "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ows=\"http://www.opengis.net/ows\" resultType=\"results\" service=\"CSW\" version=\"2.0.2\" outputSchema=\"http://www.isotc211.org/2005/gmd\" xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2                        http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd\">  <csw:Query typeNames=\"csw:Record\">    <csw:ElementSetName typeNames=\"csw:Record\">full</csw:ElementSetName>    <csw:Constraint version=\"1.1.0\">      <ogc:Filter>        <ogc:BBOX>          <ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>          <gml:Envelope>            <gml:lowerCorner>7.30 49.30</gml:lowerCorner>            <gml:upperCorner>10.70 51.70</gml:upperCorner>          </gml:Envelope>        </ogc:BBOX>      </ogc:Filter>    </csw:Constraint>  </csw:Query></csw:GetRecords>";

    private static final String GETRECORDBYID_POSTBODY = "<GetRecordById xmlns=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd\" service=\"CSW\" version=\"2.0.2\" outputSchema=\"http://www.isotc211.org/2005/gmd\">  <Id>655e5998-a20e-66b5-c888-00005553421</Id>  <ElementSetName>full</ElementSetName></GetRecordById>";

    // The query strings are not complete, but sufficient to check the request parameter
    private static final String GETCAPABILITIES_POSTBODY_KVP = "version=2.0.2&REQUEST=GetCapabilities";
    
    private static final String GETRECORDS_POSTBODY_KVP = "version=2.0.2&REQUEST=GetRecords";
    
    private static final String GETRECORDBYID_POSTBODY_KVP = "version=2.0.2&REQUEST=GetRecordById";

    
    private final FilterRuleImpl getRecordsFilterRule = new FilterRuleImpl( GETRECORDS_OPERATION, GETRECORDS_ROLE );

    private final FilterRuleImpl getRecordByIdFilterRule = new FilterRuleImpl( GETRECORDBYID_OPERATION,
                                                                               GETRECORDBYID_ROLE );

    private final FilterRuleImpl getCapabilitiesFilterRule = new FilterRuleImpl( GETCAPABILITIES_OPERATION,
                                                                                 GETCAPABILITIES_ROLE );

    @Test
    public void testCanHandle() {
        String requestUrl = "http://foo.bar/services/csw";

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecords" );
        assertTrue( getRecordsFilterRule.canHandle( requestUrl, paramMap ) );

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetCapabilities" );
        assertTrue( getCapabilitiesFilterRule.canHandle( requestUrl, paramMap ) );

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecordById" );
        assertTrue( getRecordByIdFilterRule.canHandle( requestUrl, paramMap ) );
    }

    @Test
    public void testCanHandleFalseOperation() {
        String requestUrl = "http://foo.bar/services/csw";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecordById" );
        assertFalse( getRecordsFilterRule.canHandle( requestUrl, paramMap ) );
        // The filter rule should return true for isPermitted if it cannot handle the request
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, paramMap, null ) );

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecords" );
        assertFalse( getRecordByIdFilterRule.canHandle( requestUrl, paramMap ) );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, paramMap, null ) );
        
        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetCapabilities" );
        assertFalse( getRecordsFilterRule.canHandle( requestUrl, paramMap ) );
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, paramMap, null ) );
    }

    @Test
    public void testIsPermittedKVP() {
        String requestUrl = "http://foo.bar/services/csw";
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetCapabilities" );
        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, paramMap, getContext().getAuthentication() ) );

        paramMap.put( "REQUEST", "GetRecordById" );
        authenticate( GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, paramMap, getContext().getAuthentication() ) );

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecords" );
        authenticate( GETRECORDS_USER );

        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, paramMap, getContext().getAuthentication() ) );

    }

    @Test
    public void testIsNotPermittedKVP() {
        String requestUrl = "http://foo.bar/services/csw";
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecords" );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, paramMap, getContext().getAuthentication() ) );

        paramMap.put( "REQUEST", "GetRecordById" );
        assertFalse( getRecordByIdFilterRule.isPermitted( requestUrl, paramMap, getContext().getAuthentication() ) );

        paramMap = new HashMap<String, String>();
        paramMap.put( "REQUEST", "GetRecordById" );
        authenticate( GETRECORDS_USER );
        assertFalse( getRecordByIdFilterRule.isPermitted( requestUrl, paramMap, getContext().getAuthentication() ) );
    }

    @Test
    public void testIsPermittedPostXml()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";

        XMLStreamReader getCapabilitiesStream = getPostBody( GETCAPABILITIES_POSTBODY );
        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, getCapabilitiesStream,
                                                           getContext().getAuthentication() ) );

        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        authenticate( GETRECORDS_USER );
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );

        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDBYID_POSTBODY );
        authenticate( GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdStream, getContext().getAuthentication() ) );

    }
    
    @Test
    public void testIsNotPermittedPostXml()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";

        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );

        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDS_POSTBODY );
        authenticate( GETRECORDBYID_USER );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordByIdStream, getContext().getAuthentication() ) );

    }
    
    @Test
    public void testIsPermittedPostKvp()
                            throws XMLStreamException, UnsupportedEncodingException {
        String requestUrl = "http://foo.bar/services/csw";
        
        Map<String,String> getCapabilitiesMap = getPostKvpMap( GETCAPABILITIES_POSTBODY_KVP );
        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, getCapabilitiesMap,
                                                           getContext().getAuthentication() ) );

        Map<String,String> getRecordsMap = getPostKvpMap( GETRECORDS_POSTBODY_KVP );
        authenticate( GETRECORDS_USER );
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsMap, getContext().getAuthentication() ) );

        Map<String,String> getRecordByIdMap = getPostKvpMap( GETRECORDBYID_POSTBODY_KVP );
        authenticate( GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdMap, getContext().getAuthentication() ) );
    }


    private Map<String,String> getPostKvpMap( String body ) throws UnsupportedEncodingException {
        return KVPUtils.getNormalizedKVPMap( body, "UTF-8" );
    }

    private XMLStreamReader getPostBody( String body )
                            throws XMLStreamException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );
        XMLStreamReader xmlStream = XMLInputFactoryUtils.newSafeInstance().createXMLStreamReader( "...",
                                                                                                  byteArrayInputStream );
        // Jump to first element
        XMLStreamUtils.nextElement( xmlStream );
        return xmlStream;
    }

    private Authentication authenticate( String userName ) {

        UserDetails userDetails = userDetailsService.loadUserByUsername( userName );

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( userDetails,
                                                                                             userDetails.getPassword() );
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                                                                                              userDetails,
                                                                                              token.getCredentials(),
                                                                                              userDetails.getAuthorities() );
        result.setDetails( token.getDetails() );
        Authentication auth = result;
        getContext().setAuthentication( auth );
        auth = getContext().getAuthentication();
        assertTrue( auth.isAuthenticated() );
        return auth;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }
}
