package org.deegree.console.security;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HttpGetFilterTest {

    private static final String GETRECORDS_OPERATION = "getrecords";
    
    private static final String RECORDS_ROLE = "records";
    
    private final HttpGetFilter recordsFilter = new HttpGetFilterImpl(GETRECORDS_OPERATION,RECORDS_ROLE);

    @Test
    public void testCanHandle() {
        String requestUrl = "http://foo.bar/services/csw";
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        String[] params = { "GetRecords" };
        paramMap.put( "request", params );
        assertTrue( recordsFilter.canHandle( requestUrl, paramMap ) );
    }

    @Test
    public void testCanHandleFalseOperation() {
        String requestUrl = "http://foo.bar/services/csw";
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        String[] params = { "GetCapabilities" };
        paramMap.put( "request", params );
        assertFalse( recordsFilter.canHandle( requestUrl, paramMap ) );
    }

    @Test
    public void testCanHandleDoubleKVP() {
        String requestUrl = "http://foo.bar/services/csw";
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        String[] params = { "GetRecords", "GetRecordById" };
        paramMap.put( "request", params );
        assertTrue( recordsFilter.canHandle( requestUrl, paramMap ) );
    }
}
