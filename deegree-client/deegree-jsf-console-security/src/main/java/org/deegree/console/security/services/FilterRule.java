package org.deegree.console.security.services;

import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.springframework.security.core.Authentication;

public interface FilterRule {

    boolean canHandle( String requestUrl, Map<String, String> paramMap );

    boolean isPermitted( String requestUrl, Map<String, String> paramMap, Authentication authentication );

    boolean canHandle( String requestUrl, XMLStreamReader reader );

    boolean isPermitted( String requestUrl, XMLStreamReader reader, Authentication authentication );

}