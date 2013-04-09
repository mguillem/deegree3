package org.deegree.console.security;

import javax.xml.stream.XMLStreamReader;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class HttpPostXmlFilterImpl implements HttpPostXmlFilter {

    private final String allowedRole;

    private final String matchMethod;

    public HttpPostXmlFilterImpl( String matchMethod, String allowedRole ) {
        this.matchMethod = matchMethod;
        this.allowedRole = allowedRole;
    }

    @Override
    public boolean canHandle( String requestUrl, XMLStreamReader reader ) {
        return matchMethod.equalsIgnoreCase( reader.getName().getLocalPart().toString() );
    }

    @Override
    public boolean isPermitted( String requestUrl, XMLStreamReader reader, Authentication authentication ) {
        if ( !canHandle( requestUrl, reader ) )
            return true;
        for ( GrantedAuthority auth : authentication.getAuthorities() ) {
            if ( allowedRole.equals( auth.getAuthority() ) )
                return true;
        }
        return false;
    }
}
