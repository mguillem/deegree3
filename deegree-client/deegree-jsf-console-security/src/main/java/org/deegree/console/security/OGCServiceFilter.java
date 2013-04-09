package org.deegree.console.security;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.kvp.KVPUtils;
import org.deegree.commons.xml.stax.XMLInputFactoryUtils;
import org.deegree.commons.xml.stax.XMLStreamUtils;
import org.slf4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Custom Filter for Spring Security for OGC Web Services
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * 
 */
public class OGCServiceFilter implements Filter {

    private static final Logger LOG = getLogger( OGCServiceFilter.class );

    private List<HttpKVPFilter> kvpFilter;

    private List<HttpPostXmlFilter> postXmlFilters;
    
    private static final String DEFAULT_ENCODING = "UTF-8";


    private boolean isAllowedOnNoMatch;

    public OGCServiceFilter( List<HttpKVPFilter> getFilters, List<HttpPostXmlFilter> postXmlFilter,
                             boolean isAllowedOnNoMatch ) {
        this.kvpFilter = getFilters;
        this.postXmlFilters = postXmlFilter;
        this.isAllowedOnNoMatch = isAllowedOnNoMatch;
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse result, FilterChain chain )
                            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        boolean isOneFilterMatching = false;
        boolean isRequestPermitted = true;
        HttpServletRequestWrapper wrapper = new CustomHttpServletRequestWrapper( req );
        try {
            Pair<Boolean, Boolean> resultOfFiltering = checkPermission( wrapper );
            isOneFilterMatching = resultOfFiltering.getFirst();
            isRequestPermitted = resultOfFiltering.getSecond();
        } catch ( XMLStreamException e ) {
            e.printStackTrace();
            LOG.error( "Could not parse request!" );
            throw new AccessDeniedException( "Request could not be performed" );
        }
        if ( !isOneFilterMatching ) {
            handleNotOneFilterMatching( result, chain, wrapper );
        } else {
            handleFilterMatching( result, chain, isRequestPermitted, wrapper );
        }
    }

    private void handleFilterMatching( ServletResponse result, FilterChain chain, boolean isRequestPermitted,
                                       HttpServletRequestWrapper wrapper )
                            throws IOException, ServletException {
        if ( isRequestPermitted ) {
            chain.doFilter( wrapper, result );
        } else {
            throw new AccessDeniedException( "Not permitted to access this resource" );
        }
    }

    private void handleNotOneFilterMatching( ServletResponse result, FilterChain chain,
                                             HttpServletRequestWrapper wrapper )
                            throws IOException, ServletException {
        if ( !isAllowedOnNoMatch ) {
            throw new AccessDeniedException( "Not permitted to access this resource" );
        } else {
            chain.doFilter( wrapper, result );
        }
    }

    private Pair<Boolean, Boolean> checkPermission( HttpServletRequest wrapper )
                            throws XMLStreamException, IOException {

        if ( wrapper.getMethod() == "GET" ) {
            return handleGet( wrapper );
        } else {
            return handlePost( wrapper );
        }
    }

    private Pair<Boolean, Boolean> handleGet( HttpServletRequest wrapper ) throws UnsupportedEncodingException {
        LOG.debug( "Filtering HTTP-GET request" );
        String requestUrl = wrapper.getRequestURL().toString();
        String queryString = wrapper.getQueryString();
        Map<String, String> normalizedKVPParams = KVPUtils.getNormalizedKVPMap( queryString, DEFAULT_ENCODING );
        return checkKVP(requestUrl, normalizedKVPParams);
    }

    private Pair<Boolean, Boolean> handlePost( HttpServletRequest wrapper )
                            throws XMLStreamException, IOException {
        String contentType = wrapper.getContentType();
        boolean isKVP = false;
        if ( contentType != null ) {
            isKVP = wrapper.getContentType().startsWith( "application/x-www-form-urlencoded" );
        }
        if ( isKVP ) {
            LOG.debug( "Handling POST-KVP request" );
            return checkPostKVP( wrapper );
        } else {
            LOG.debug( "Handling POST-XML request" );
            return checkPostXml( wrapper );
        }
    }

    private Pair<Boolean, Boolean> checkPostKVP( HttpServletRequest wrapper ) throws IOException {
        Map<String, String> normalizedKVPParams = null;
        String encoding = wrapper.getCharacterEncoding();
        String queryString = readPostBodyAsString( wrapper.getInputStream());
        String requestUrl = wrapper.getRequestURL().toString();
        if ( encoding == null ) {
            LOG.debug( "Request has no further encoding information. Defaulting to '" + DEFAULT_ENCODING
                       + "'." );
            normalizedKVPParams = KVPUtils.getNormalizedKVPMap( queryString, DEFAULT_ENCODING );
        } else {
            LOG.debug( "Client encoding information :" + encoding );
            normalizedKVPParams = KVPUtils.getNormalizedKVPMap( queryString, encoding );
        }
        return checkKVP(requestUrl, normalizedKVPParams);
    }

    private Pair<Boolean, Boolean> checkKVP( String requestUrl, Map<String, String> normalizedKVPParams ) {
        boolean isOneFilterMatched = false;
        boolean isRequestPermitted = true;
        for ( HttpKVPFilter getFilter : kvpFilter ) {
            if ( getFilter.canHandle( requestUrl, normalizedKVPParams ) ) {
                isOneFilterMatched = true;
                if ( !getFilter.isPermitted( requestUrl, normalizedKVPParams,
                                             SecurityContextHolder.getContext().getAuthentication() ) ) {
                    isRequestPermitted = false;
                }
            }
        }
        return new Pair<Boolean, Boolean>( isOneFilterMatched, isRequestPermitted );
    }

    private Pair<Boolean, Boolean> checkPostXml( HttpServletRequest wrapper )
                            throws XMLStreamException, IOException {
        String dummySystemId = "HTTP Post request from " + wrapper.getRemoteAddr() + ":" + wrapper.getRemotePort();
        String requestUrl = wrapper.getRequestURL().toString();
        boolean isPermitted = true;
        boolean isOneFilterMatching = false;
        for ( HttpPostXmlFilter filter : postXmlFilters ) {
            // The wrapped stream may be retrieved multiple times
            XMLStreamReader xmlStream = XMLInputFactoryUtils.newSafeInstance().createXMLStreamReader( dummySystemId,
                                                                                                      wrapper.getInputStream() );
            // Jump to first element
            XMLStreamUtils.nextElement( xmlStream );
            if ( filter.canHandle( requestUrl, xmlStream ) ) {
                isOneFilterMatching = true;
                if ( !filter.isPermitted( requestUrl, xmlStream, SecurityContextHolder.getContext().getAuthentication() ) )
                    isPermitted = false;
            }
        }
        return new Pair<Boolean, Boolean>( isOneFilterMatching, isPermitted );
    }

    private static String readPostBodyAsString( InputStream is )
                            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream( is );
        byte[] readBuffer = new byte[1024];
        int numBytes = -1;
        while ( ( numBytes = bis.read( readBuffer ) ) != -1 ) {
            bos.write( readBuffer, 0, numBytes );
        }
        return bos.toString().trim();
    }
    
    public List<HttpKVPFilter> getGetFilter() {
        return kvpFilter;
    }

    public void setGetFilter( List<HttpKVPFilter> getFilters ) {
        this.kvpFilter = getFilters;
    }

    public List<HttpPostXmlFilter> getPostXmlFilters() {
        return postXmlFilters;
    }

    public void setPostXmlFilters( List<HttpPostXmlFilter> postXmlFilters ) {
        this.postXmlFilters = postXmlFilters;
    }

    @Override
    public void init( FilterConfig filterConfig )
                            throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
