package org.deegree.console.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Custom Filter for Spring Security for OGC Web Services
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * 
 */
public class OGCServicesFilter extends OncePerRequestFilter {

    private List<HttpGetFilter> getFilters;

    public OGCServicesFilter( List<HttpGetFilter> getFilters ) {
        this.getFilters = getFilters;
    }

    @Override
    public void doFilterInternal( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
                            throws IOException, ServletException {

        boolean isRequestPermitted = true;
        isRequestPermitted = checkPermission( req, isRequestPermitted );
        if ( isRequestPermitted ) {
            doFilter( req, res, chain );
        } else {
            res.setStatus( 401 );
        }
    }

    private boolean checkPermission( HttpServletRequest req, boolean isRequestPermitted ) {
        String requestUrl = req.getRequestURL().toString();
        @SuppressWarnings("unchecked")
        Map<String, String[]> paramMap = req.getParameterMap();
        for ( HttpGetFilter getFilter : getFilters ) {
            if ( getFilter.canHandle( requestUrl, paramMap ) ) {
                if ( !getFilter.isPermitted( requestUrl, paramMap,
                                             SecurityContextHolder.getContext().getAuthentication() ) ) {
                    isRequestPermitted = false;
                }
            }
        }
        return isRequestPermitted;
    }

    public List<HttpGetFilter> getGetFilter() {
        return getFilters;
    }

    public void setGetFilter( List<HttpGetFilter> getFilters ) {
        this.getFilters = getFilters;
    }
}
