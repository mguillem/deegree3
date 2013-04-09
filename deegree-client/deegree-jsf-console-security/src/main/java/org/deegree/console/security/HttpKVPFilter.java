package org.deegree.console.security;

import java.util.Map;

import org.springframework.security.core.Authentication;

/**
 * Interface for filters operating against HTTP GET/POST KVP requests
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public interface HttpKVPFilter {

    /**
     * Checks if this instance is capable of evaluating a given request
     * 
     * @param requestUrl
     *            the request url, never null
     * @param paramMap
     *            the request parameter map, never null
     * @return
     */
    boolean canHandle( String requestUrl, Map<String, String> paramMap );

    /**
     * Checks if a given user is permitted to access the resource
     * 
     * @param requestUrl
     *            the request url, never null
     * @param paramMap
     *            the request parameter map, never null
     * @param auth
     *            the user authentication
     * @return
     */
    boolean isPermitted( String requestUrl, Map<String, String> paramMap, Authentication auth );
}
