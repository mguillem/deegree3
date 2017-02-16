package org.deegree.protocol.ows.http;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class OwsHttpClientImplIT {

    @Before
    public void setUp() {
        System.setProperty("javax.net.debug", "all");
    }

    @Test
    public void testDoGetHttp()
                            throws Exception {
        OwsHttpClient client = new OwsHttpClientImpl();
        URL url = new URL( "http://geo.noe.gv.at/inspire/rest/services/dynamicServices/inspire/MapServer/exts/InspireView/service?VERSION=1.3.0&SERVICE=WMS&REQUEST=GetCapabilities" );
        OwsHttpResponse response = client.doGet( url, null, null );
        assertEquals(response.getAsHttpResponse().getStatusLine().getStatusCode(),200);
    }

    @Test
    public void testDoGetHttpsWithValidCertificate()
            throws Exception {
        OwsHttpClient client = new OwsHttpClientImpl();
        URL url = new URL( "https://gis.tirol.gv.at/arcgis/services/INSPIRE/AT_0024_17_Bodennutzung/MapServer/WMSServer?request=GetCapabilities&service=WMS" );
        OwsHttpResponse response = client.doGet( url, null, null );
        assertEquals(response.getAsHttpResponse().getStatusLine().getStatusCode(),200);
    }

    @Test
    public void testDoGetHttpsWithInvalidCertificate()
            throws Exception {
        OwsHttpClient client = new OwsHttpClientImpl();
        URL url = new URL( "https://msdi.data.gov.mt/geonetwork/srv/eng/csw?SERVICE=CSW&VERSION=2.0.2&REQUEST=GetCapabilities" );
        OwsHttpResponse response = client.doGet( url, null, null );
        assertEquals(response.getAsHttpResponse().getStatusLine().getStatusCode(),200);
    }

    @Test(expected = java.io.IOException.class)
    public void testDoGetHttpsWithSocketTimeoutException()
            throws Exception {
        OwsHttpClient client = new OwsHttpClientImpl();
        URL url = new URL( "https://featureservices.kystverket.no/deegree-webservices/services/wfs.inspire-tn-wa?service=WFS&Version=2.0.0&service=WFS&acceptversions=2.0.0&request=GetCapabilities" );
        OwsHttpResponse response = client.doGet( url, null, null );
    }


}
