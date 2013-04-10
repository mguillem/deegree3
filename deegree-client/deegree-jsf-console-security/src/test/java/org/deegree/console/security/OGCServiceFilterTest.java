package org.deegree.console.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;

import org.deegree.console.security.services.FilterRule;
import org.deegree.console.security.services.FilterRuleImpl;
import org.deegree.console.security.services.OGCServiceFilter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml", "/testSecurityLayer.xml" })
public class OGCServiceFilterTest {

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

    private static final String GETRECORDS_POSTBODY = "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ows=\"http://www.opengis.net/ows\" resultType=\"results\" service=\"CSW\" version=\"2.0.2\" outputSchema=\"http://www.isotc211.org/2005/gmd\" xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2                        http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd\">  <csw:Query typeNames=\"csw:Record\">    <csw:ElementSetName typeNames=\"csw:Record\">full</csw:ElementSetName>    <csw:Constraint version=\"1.1.0\">      <ogc:Filter>        <ogc:BBOX>          <ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>          <gml:Envelope>            <gml:lowerCorner>7.30 49.30</gml:lowerCorner>            <gml:upperCorner>10.70 51.70</gml:upperCorner>          </gml:Envelope>        </ogc:BBOX>      </ogc:Filter>    </csw:Constraint>  </csw:Query></csw:GetRecords>";

    // The query strings are not complete, but sufficient to check the request parameter
    private static final String GETRECORDS_POSTBODY_KVP = "version=2.0.2&REQUEST=GetRecords";

    private final FilterRuleImpl getRecordsFilterRule = new FilterRuleImpl( GETRECORDS_OPERATION, GETRECORDS_ROLE );

    private final FilterRuleImpl getRecordByIdFilterRule = new FilterRuleImpl( GETRECORDBYID_OPERATION,
                                                                               GETRECORDBYID_ROLE );

    private final FilterRuleImpl getCapabilitiesFilterRule = new FilterRuleImpl( GETCAPABILITIES_OPERATION,
                                                                                 GETCAPABILITIES_ROLE );

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testGetKvp() throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setQueryString( "REQUEST=getCapabilities" );
        req.setMethod( "GET" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
        
        req.setQueryString( "REQUEST=getRecords" );
        req.setMethod( "GET" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
        
        req.setQueryString( "REQUEST=getRecordById" );
        req.setMethod( "GET" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDBYID_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    @Test(expected=AccessDeniedException.class)
    public void testGetKvpNotPermitted() throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setQueryString( "REQUEST=getRecordById" );
        req.setMethod( "GET" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    @Test
    public void testPostXml() throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest(GETRECORDS_POSTBODY);
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    @Test(expected=AccessDeniedException.class)
    public void testPostXmlNotPermitted() throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest(GETRECORDS_POSTBODY);
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    @Test
    public void testPostKvp() throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest(GETRECORDS_POSTBODY_KVP);
        req.setMethod( "POST" );
        req.setContentType( "application/x-www-form-urlencoded" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    @Test(expected=AccessDeniedException.class)
    public void testPostKvpNotPermitted() throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest(GETRECORDS_POSTBODY_KVP);
        req.setMethod( "POST" );
        req.setContentType( "application/x-www-form-urlencoded" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    private OGCServiceFilter getFilter() {
        List<FilterRule> rules = new ArrayList<FilterRule>();
        rules.add( getRecordsFilterRule );
        rules.add( getRecordByIdFilterRule );
        rules.add( getCapabilitiesFilterRule );
        final OGCServiceFilter filter = new OGCServiceFilter( rules, false );
        return filter;
    }
    
    private class MockInputStreamServletRequest extends MockHttpServletRequest {
        
        private String body;
        
        public MockInputStreamServletRequest (String body) {
            this.body = body;
        }
        
        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );

            ServletInputStream inputStream = new ServletInputStream() {
                public int read()
                                        throws IOException {
                    return byteArrayInputStream.read();
                }
            };

            return inputStream;
        }
    }
    
}
