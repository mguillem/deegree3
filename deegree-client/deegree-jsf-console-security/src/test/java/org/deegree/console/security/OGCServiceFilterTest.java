package org.deegree.console.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
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

    private static final String GETRECORDS_POSTBODY = convertXmlFileToString( "/getRecords_PostBody.xml" );

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
    public void testGetKvp()
                            throws IOException, ServletException {
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

    @Test(expected = AccessDeniedException.class)
    public void testGetKvpNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setQueryString( "REQUEST=getRecordById" );
        req.setMethod( "GET" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test
    public void testPostXml()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY );
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test(expected = AccessDeniedException.class)
    public void testPostXmlNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY );
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test
    public void testPostKvp()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY_KVP );
        req.setMethod( "POST" );
        req.setContentType( "application/x-www-form-urlencoded" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test(expected = AccessDeniedException.class)
    public void testPostKvpNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY_KVP );
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

        public MockInputStreamServletRequest( String body ) {
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

    private static String convertXmlFileToString( String inputXmlFile ) {
        try {
            InputStream inputStream = FilterRuleImplTest.class.getResourceAsStream( inputXmlFile );
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy( inputStream, stringWriter, "UTF-8" );
            return stringWriter.toString();
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

}
