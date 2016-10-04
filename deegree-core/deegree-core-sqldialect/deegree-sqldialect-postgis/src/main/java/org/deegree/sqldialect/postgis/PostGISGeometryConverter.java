//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/deegree3/trunk/deegree-core/deegree-core-base/src/main/java/org/deegree/filter/sql/postgis/PostGISGeometryConverter.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.sqldialect.postgis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.GeometryTransformer;
import org.deegree.geometry.io.WKBReader;
import org.deegree.geometry.io.WKBWriter;
import org.deegree.geometry.utils.GeometryParticleConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link GeometryParticleConverter} for PostGIS databases.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 31055 $, $Date: 2011-06-14 17:19:48 +0200 (Di, 14. Jun 2011) $
 */
public class PostGISGeometryConverter extends AbstractPostGISConverter {


    private final boolean useLegacyPredicates;

    /**
     * Creates a new {@link PostGISGeometryConverter} instance.
     * 
     * @param column
     *            (unqualified) column that stores the geometry, must not be <code>null</code>
     * @param crs
     *            CRS of the stored geometries, can be <code>null</code>
     * @param srid
     *            PostGIS spatial reference identifier, must not be <code>null</code>
     * @param useLegacyPredicates
     *            if true, legacy-style PostGIS spatial predicates are used (e.g. <code>Intersects</code> instead of
     *            <code>ST_Intersects</code>)
     */
    public PostGISGeometryConverter( String column, ICRS crs, String srid, boolean useLegacyPredicates ) {
        super( column, crs, srid );
        this.useLegacyPredicates = useLegacyPredicates;
    }

    @Override
    public String getSelectSnippet( String tableAlias ) {
        String asewkb = useLegacyPredicates ? "AsEWKB" : "ST_AsEWKB";
        if ( tableAlias != null ) {
            return asewkb + "(" + tableAlias + "." + column + ")";
        }
        return asewkb + "(" + column + ")";
    }

    @Override
    public Geometry toParticle( ResultSet rs, int colIndex )
                            throws SQLException {
        byte[] wkb = rs.getBytes( colIndex );
        if ( wkb == null ) {
            return null;
        }
        try {
            return WKBReader.read( wkb, crs );
        } catch ( Throwable t ) {
            throw new IllegalArgumentException( t.getMessage(), t );
        }
    }

    @Override
    public String getSetSnippet( Geometry particle ) {
        StringBuilder sb = new StringBuilder();
        if ( useLegacyPredicates ) {
            sb.append( "SetSRID(GeomFromWKB(?)," );
        } else {
            sb.append( "ST_SetSRID(ST_GeomFromWKB(?)," );
        }
        sb.append( srid == null ? "-1" : srid );
        sb.append( ")" );
        return sb.toString();
    }

    @Override
    public void setParticle( PreparedStatement stmt, Geometry particle, int paramIndex )
                            throws SQLException {
        byte[] wkb = null;
        if ( particle != null ) {
            try {
                Geometry compatible = getCompatibleGeometry( particle );
                wkb = WKBWriter.write( compatible );
            } catch ( Throwable t ) {
                throw new IllegalArgumentException( t.getMessage(), t );
            }
        }
        stmt.setBytes( paramIndex, wkb );
    }

}