//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/deegree3/trunk/deegree-core/src/test/java/org/deegree/feature/persistence/postgis/PostGISFeatureStoreTest.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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
package org.deegree.sqldialect.filter;

import java.util.HashMap;
import java.util.Map;

import org.deegree.commons.jdbc.TableName;
import org.deegree.filter.expression.ValueReference;

/**
 * Creates and tracks table aliases that are needed for mapping {@link ValueReference}s to a relational schema.
 * 
 * @see AbstractWhereBuilder
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 25462 $, $Date: 2010-07-21 18:45:40 +0200 (Mi, 21. Jul 2010) $
 */
public class TableAliasManager {

    private final Map<AliasedTableName, String> aliases = new HashMap<AliasedTableName, String>();

    private final String rootTableAlias;

    private int currentIdx = 1;

    /**
     * Creates a new {@link TableAliasManager} instance.
     */
    public TableAliasManager() {
        rootTableAlias = generateNew();
    }

    /**
     * Deprecated: Use #getTableAlias(TableName) instead.
     * 
     * Returns the table alias for the root table.
     * 
     * @return the table alias for the root table, never <code>null</code>
     */
    @Deprecated
    public String getRootTableAlias() {
        return rootTableAlias;
    }

    /**
     * Returns the table alias for the passed {@link TableName}.
     * 
     * @param tableName
     *            to retrieve the alias for, never <code>null</code>
     * @return the table alias of the passed {@link TableName}, never <code>null</code>
     */
    @Deprecated
    public String getTableAlias( TableName tableName ) {
        return getTableAlias( tableName, null );
    }

    /**
     * Returns the table alias for the passed {@link TableName} and alias from the query.
     * 
     * @param tableName
     *            to retrieve the alias for, never <code>null</code>
     * @param alias
     *            the alias of the query, may be <code>null</code>
     * @return the table alias of the passed {@link TableName}, never <code>null</code>
     */
    public String getTableAlias( TableName tableName, String alias ) {
        AliasedTableName key = new AliasedTableName( tableName, alias );
        if ( !aliases.containsKey( key ) ) {
            aliases.put( key, generateNew() );
        }
        return aliases.get( key );
    }

    /**
     * Returns a new unique table alias.
     * 
     * @return a new unique table alias, never <code>null</code>
     */
    public String generateNew() {
        return "X" + ( currentIdx++ );
    }

    private class AliasedTableName {

        private TableName tableName;

        private String alias;

        public AliasedTableName( TableName tableName, String alias ) {
            this.tableName = tableName;
            this.alias = alias;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ( ( alias == null ) ? 0 : alias.hashCode() );
            result = prime * result + ( ( tableName == null ) ? 0 : tableName.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            AliasedTableName other = (AliasedTableName) obj;
            if ( !getOuterType().equals( other.getOuterType() ) )
                return false;
            if ( alias == null ) {
                if ( other.alias != null )
                    return false;
            } else if ( !alias.equals( other.alias ) )
                return false;
            if ( tableName == null ) {
                if ( other.tableName != null )
                    return false;
            } else if ( !tableName.equals( other.tableName ) )
                return false;
            return true;
        }

        private TableAliasManager getOuterType() {
            return TableAliasManager.this;
        }

    }

}