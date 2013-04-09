package org.deegree.console.security;

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
public class HttpKVPFilterImpl implements HttpKVPFilter {

    private final String allowedRole;

    private final String matchMethod;

    public HttpKVPFilterImpl( String matchMethod, String allowedRole ) {
        this.allowedRole = allowedRole;
        this.matchMethod = matchMethod;
    }

    @Override
    public boolean canHandle( String requestUrl, Map<String, String> paramMap ) {
        String operation = paramMap.get( "REQUEST" );
        return matchMethod.equalsIgnoreCase( operation );
    }

    @Override
    public boolean isPermitted( String requestUrl, Map<String, String> paramMap, Authentication authentication ) {
        if ( !canHandle( requestUrl, paramMap ) )
            return false;
        for ( GrantedAuthority auth : authentication.getAuthorities() ) {
            if ( allowedRole.equals( auth.getAuthority() ) )
                return true;
        }
        return false;
    }

    public String getAllowedRole() {
        return allowedRole;
    }

}
