package org.deegree.console.security;

import javax.xml.stream.XMLStreamReader;

import org.springframework.security.core.Authentication;

/**
 * Interface for filters operating against HTTP POST XML requests
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 */
public interface HttpPostXmlFilter {

    /**
     * Checks if the given instance is capable of evaluating the request
     * 
     * @param requestUrl
     * @param xmlStream
     * @param authentication
     * @return
     */
    boolean canHandle( String requestUrl, XMLStreamReader reader );

    /**
     * Checks if a given user is authorized to perform the request
     * 
     * @param requestUrl
     *            the request url, never null
     * @param paramMap
     *            the request parameter map, never null
     * @return
     */
    boolean isPermitted( String requestUrl, XMLStreamReader xmlStream, Authentication authentication );

}