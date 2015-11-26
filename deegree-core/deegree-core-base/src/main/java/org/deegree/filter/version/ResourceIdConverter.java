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

import org.deegree.commons.utils.Pair;

/**
 * Encapsulates the creation of resource ids.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public interface ResourceIdConverter {

    /**
     * Creates a ResourceId from the passed {@link FeatureMetadata}. If version is missing the encapsulated fid is
     * returned.
     * 
     * @param featureMetadata
     *            never <code>null</code>
     * @return the ResourceId of the feature, never <code>null</code>
     * @throws NullPointerException
     *             if FeatureMetadata is <code>null</code>
     */
    String generateResourceId( FeatureMetadata featureMetadata );

    /**
     * Checks if the passed id has a version.
     * 
     * @param id
     *            never <code>null</code>
     * @return <code>true</code> if the id has a version, <code>false</code> otherwise
     * @throws NullPointerException
     *             if id is <code>null</code>
     */
    boolean hasVersion( String id );

    /**
     * Splits the passed id into FID and version (if the id has a version).
     * 
     * @param id
     *            never <code>null</code>
     * @return the FID (first, never <code>null</code>) and version (second, may be -1 if not specified, otherwise > 0)
     *         from the id, never <code>null</code>
     */
    Pair<String, Integer> parseRid( String id );

}