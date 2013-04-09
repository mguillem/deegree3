package org.deegree.console.security;

import java.util.Iterator;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Generic HTTP GET Filter for Spring Security
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * 
 */
public class HttpGetFilterImpl implements HttpGetFilter {

    private final String allowedRole;
    
    private final String matchMethod;

    public HttpGetFilterImpl( String matchMethod, String allowedRole ) {
        this.allowedRole = allowedRole;
        this.matchMethod = matchMethod;
    }

    @Override
    public boolean canHandle( String requestUrl, Map<String, String[]> paramMap ) {
        String operation = retrieveFirstValueOfKey( paramMap, "request" );
        return matchMethod.equalsIgnoreCase( operation );
    }
    
    @Override
    public boolean isPermitted( String requestUrl, Map<String, String[]> paramMap, Authentication authentication ) {
        if ( !canHandle( requestUrl, paramMap ) )
            return false;
        for ( GrantedAuthority auth : authentication.getAuthorities() ) {
            if ( allowedRole.equals( auth.getAuthority() ) )
                return true;
        }
        return false;
    }

    private String retrieveFirstValueOfKey( Map<String, String[]> paramMap, String checkString ) {
        Iterator<String> i = paramMap.keySet().iterator();
        while ( i.hasNext() ) {
            String key = (String) i.next();
            String value = ( (String[]) paramMap.get( key ) )[0];
            if ( checkString.equals( key.toLowerCase() ) ) {
                return value;
            }
        }
        return null;
    }

    public String getAllowedRole() {
        return allowedRole;
    }

}
