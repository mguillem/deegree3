//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2015 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.filter.version;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.deegree.commons.utils.Pair;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class DefaultResourceIdConverterTest {

    private final DefaultResourceIdConverter resourceIdConverter = new DefaultResourceIdConverter();

    @Test
    public void generateResourceId_WithFid() {
        FeatureMetadata featureMetadata = new FeatureMetadata( "testFid_65656" );

        String resourceId = resourceIdConverter.generateResourceId( featureMetadata );

        assertThat( resourceId, is( "testFid_65656" ) );
    }

    @Test
    public void generateResourceId_WithFidAndVersion() {
        FeatureMetadata featureMetadata = new FeatureMetadata( "testFid_65656", "1" );

        String resourceId = resourceIdConverter.generateResourceId( featureMetadata );

        assertThat( resourceId, is( "testFid_65656_version1" ) );
    }

    @Test(expected = NullPointerException.class)
    public void generateResourceId_Null() {
        resourceIdConverter.generateResourceId( null );
    }

    @Test
    public void testHasVersion_withVersion() {
        boolean hasVersion = resourceIdConverter.hasVersion( "testFid_65656_version1" );

        assertThat( hasVersion, is( true ) );
    }

    @Test
    public void testHasVersion_withoutVersion() {
        boolean hasVersion = resourceIdConverter.hasVersion( "testFid_65656" );

        assertThat( hasVersion, is( false ) );
    }

    @Test(expected = NullPointerException.class)
    public void testHasVersion_Null() {
        resourceIdConverter.hasVersion( null );
    }

    @Test
    public void testConvertToFeatureMetadata_withVersion() {
        Pair<String, Integer> fetaureMeatadata = resourceIdConverter.parseRid( "testFid_65656_version1" );

        assertThat( fetaureMeatadata.getFirst(), is( "testFid_65656" ) );
        assertThat( fetaureMeatadata.getSecond(), is( 1 ) );
    }

    @Test
    public void testConvertToFeatureMetadata_withInvalidVersion() {
        Pair<String, Integer> fetaureMeatadata = resourceIdConverter.parseRid( "testFid_65656_versionA1" );

        assertThat( fetaureMeatadata.getFirst(), is( "testFid_65656_versionA1" ) );
        assertThat( fetaureMeatadata.getSecond(), is( -1 ) );
    }

    @Test
    public void testConvertToFeatureMetadata_withoutVersion() {
        Pair<String, Integer> fetaureMeatadata = resourceIdConverter.parseRid( "testFid_65656" );

        assertThat( fetaureMeatadata.getFirst(), is( "testFid_65656" ) );
        assertThat( fetaureMeatadata.getSecond(), is( -1 ) );
    }

    @Test(expected = NullPointerException.class)
    public void testConvertToFeatureMetadata_Null() {
        resourceIdConverter.parseRid( null );
    }

}