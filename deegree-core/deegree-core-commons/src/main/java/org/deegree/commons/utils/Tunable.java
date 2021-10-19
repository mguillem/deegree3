/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -
 and
 - grit graphische Informationstechnik Beratungsgesellschaft mbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 grit graphische Informationstechnik Beratungsgesellschaft mbH
 Landwehrstr. 143, 59368 Werne
 Germany
 http://www.grit.de/

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.commons.utils;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains static utility methods to access system tunable settings
 * 
 * If a settings is requested for the first time, the lookup order is JNDI first
 * followed by System.getProperty. The results are cached so that no further lookups 
 * are made.
 * 
 * @author <a href="mailto:reichhelm@grit.de">Stephan Reichhelm</a>
 */
public class Tunable {

    private static final Logger LOG = LoggerFactory.getLogger( Tunable.class );

    private static final Map<String, String> CONFIG_STR = new HashMap<>();

    private static final Map<String, Number> CONFIG_NUM = new HashMap<>();

    public static String get( String key, String defaultValue ) {
        boolean has = CONFIG_STR.containsKey( key );
        String val = CONFIG_STR.get( key );

        if ( !has ) {
            val = getFromJndi( key );

            if ( val == null ) {
                val = System.getProperty( key );
            }

            CONFIG_STR.put( key, val );
        }

        if ( val == null ) {
            return defaultValue;
        } else {
            return val;
        }
    }

    public static double get( String key, double defaultValue ) {
        return get( key, Double.valueOf( defaultValue ) ).doubleValue();
    }

    public static float get( String key, float defaultValue ) {
        return get( key, Float.valueOf( defaultValue ) ).floatValue();
    }

    public static long get( String key, long defaultValue ) {
        return get( key, Long.valueOf( defaultValue ) ).longValue();
    }

    public static int get( String key, int defaultValue ) {
        return get( key, Integer.valueOf( defaultValue ) ).intValue();
    }

    public static short get( String key, short defaultValue ) {
        return get( key, Short.valueOf( defaultValue ) ).shortValue();
    }

    public static byte get( String key, byte defaultValue ) {
        return get( key, Byte.valueOf( defaultValue ) ).byteValue();
    }

    private static Number get( String key, Number defaultValue ) {
        boolean has = CONFIG_NUM.containsKey( key );
        Number val = CONFIG_NUM.get( key );

        if ( !has ) {
            val = getFromJndi( key );

            if ( val == null ) {
                val = getFromSystem( key );
            }

            CONFIG_NUM.put( key, val );
        }

        if ( val == null ) {
            return defaultValue;
        } else {
            return val;
        }
    }

    private static Number getFromSystem( String key ) {
        try {
            String str = System.getProperty( key );
            if ( str != null ) {
                return Double.valueOf( str );
            }
        } catch ( Exception ex ) {
            LOG.warn( "Could not parse tuneable '{}' as double: {}", key, ex.getMessage() );
            LOG.trace( "Exception", ex );
        }
        return null;
    }

    private static <T> T getFromJndi( String key ) {
        try {
            return InitialContext.doLookup( "java:comp/env/" + key );
        } catch ( NameNotFoundException nfex ) {
            LOG.trace( "Name 'java:comp/env/{}' not found.", key );
        } catch ( NamingException ex ) {
            LOG.debug( "Could not resolve 'java:comp/env/{}' from JNDI: {}", ex.getMessage() );
            LOG.trace( "NamingException", ex );
        }
        return null;
    }
}
