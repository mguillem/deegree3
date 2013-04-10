package org.deegree.console.security;

import static org.deegree.console.security.AuthenticationUtils.authenticate;
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
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final Map<String, String> getCapabilitiesMap = getPostKvpMap( GETCAPABILITIES_POSTBODY_KVP );

    private final Map<String, String> getRecordsMap = getPostKvpMap( GETRECORDS_POSTBODY_KVP );

    private final Map<String, String> getRecordByIdMap = getPostKvpMap( GETRECORDBYID_POSTBODY_KVP );

    private final Map<String, String> getRecordsParamMap = getKvpMap( GETRECORDS_OPERATION );

    private final Map<String, String> getCapabilitiesParamMap = getKvpMap( GETCAPABILITIES_OPERATION );

    private final Map<String, String> getRecordByIdParamMap = getKvpMap( GETRECORDBYID_OPERATION );

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanHandle() {
        String requestUrl = "http://foo.bar/services/csw";
        assertTrue( getRecordsFilterRule.canHandle( requestUrl, getRecordsParamMap ) );
        assertTrue( getCapabilitiesFilterRule.canHandle( requestUrl, getCapabilitiesParamMap ) );
        assertTrue( getRecordByIdFilterRule.canHandle( requestUrl, getRecordByIdParamMap ) );
    }

    @Test
    public void testCanHandleFalseOperation() {
        String requestUrl = "http://foo.bar/services/csw";

        assertFalse( getRecordsFilterRule.canHandle( requestUrl, getCapabilitiesParamMap ) );
        // The filter rule should return true for isPermitted if it cannot handle the request
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getCapabilitiesParamMap, null ) );
    }

    @Test
    public void testIsPermittedKVP() {
        String requestUrl = "http://foo.bar/services/csw";
        
        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdParamMap, getContext().getAuthentication() ) );
    }

    @Test
    public void testIsNotPermittedKVP() {
        String requestUrl = "http://foo.bar/services/csw";

        assertFalse( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdParamMap,
                                                          getContext().getAuthentication() ) );
        authenticate( userDetailsService, GETRECORDS_USER );
        assertFalse( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdParamMap,
                                                          getContext().getAuthentication() ) );
    }

    @Test
    public void testIsPermittedPostXml()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";
        XMLStreamReader getCapabilitiesStream = getPostBody( GETCAPABILITIES_POSTBODY );
        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDBYID_POSTBODY );

        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, getCapabilitiesStream,
                                                           getContext().getAuthentication() ) );
        authenticate( userDetailsService, GETRECORDS_USER );

        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdStream,
                                                         getContext().getAuthentication() ) );
    }

    @Test
    public void testIsNotPermittedPostXml()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";

        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDS_POSTBODY );

        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordByIdStream,
                                                       getContext().getAuthentication() ) );
    }

    @Test
    public void testIsPermittedPostKvp()
                            throws XMLStreamException, UnsupportedEncodingException {
        String requestUrl = "http://foo.bar/services/csw";

        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, getCapabilitiesMap,
                                                           getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDS_USER );
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsMap, getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdMap, getContext().getAuthentication() ) );
    }

    @Test
    public void testIsPermittedPostKvpNotPermitted()
                            throws XMLStreamException, UnsupportedEncodingException {
        String requestUrl = "http://foo.bar/services/csw";

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordsMap, getContext().getAuthentication() ) );
    }

    private Map<String, String> getPostKvpMap( String body ) {
        try {
            return KVPUtils.getNormalizedKVPMap( body, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return null;
        }
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

    private Map<String, String> getKvpMap( String operation ) {
        Map<String, String> getRecordsParamMap = new HashMap<String, String>();
        getRecordsParamMap.put( "REQUEST", operation );
        return getRecordsParamMap;
    }

}
