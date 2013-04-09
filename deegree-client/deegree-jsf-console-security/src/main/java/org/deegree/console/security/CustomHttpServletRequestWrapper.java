package org.deegree.console.security;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String body;
    
    private static final Logger LOG = getLogger( CustomHttpServletRequestWrapper.class );

    CustomHttpServletRequestWrapper( HttpServletRequest request ) {
        super( request );

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();

            if ( inputStream != null ) {
                bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );

                char[] charBuffer = new char[128];
                int bytesRead = -1;

                while ( ( bytesRead = bufferedReader.read( charBuffer ) ) > 0 ) {
                    stringBuilder.append( charBuffer, 0, bytesRead );
                }
            } else {
                stringBuilder.append( "" );
            }
        } catch ( IOException ex ) {
            LOG.warn( "Error reading the request body." );
        } finally {
            if ( bufferedReader != null ) {
                try {
                    bufferedReader.close();
                } catch ( IOException ex ) {
                    LOG.warn( "Error closing bufferedReader." );
                }
            }
        }

        body = stringBuilder.toString();
    }

    @Override
    public ServletInputStream getInputStream()
                            throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );

        ServletInputStream inputStream = new ServletInputStream() {
            public int read()
                                    throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return inputStream;
    }

}