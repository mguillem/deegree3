//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.filter.sql.expression;

import java.sql.Types;
import java.util.List;

import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.cs.coordinatesystems.ICRS;

/**
 * Marks (a node of) an SQL expression with type information.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public interface SQLExpression {

    /**
     * Returns the type code from {@link Types}.
     * 
     * @return the type code
     */
    public int getSQLType();

    /**
     * Returns the primitive type of this expression.
     * 
     * @return the primitive type, can be <code>null</code> (no type information / spatial)
     */
    public PrimitiveType getPrimitiveType();

    /**
     * Returns whether the expression denotes a spatial value.
     * 
     * @return true, if the expression is spatial, false otherwise
     */
    public boolean isSpatial();

    /**
     * Returns whether the expression has multiple values (currently this can only be a string column that stores
     * multiple values in concatenated form).
     * 
     * @return true, if the expresion is multi-valued, false otherwise
     */
    public boolean isMultiValued();

    /**
     * Returns the CRS of the expression (only for spatial ones).
     * 
     * @return the CRS, can be <code>null</code> (unknown or not a spatial expression)
     */
    public ICRS getCRS();

    /**
     * Returns the databases' SRID of the expression (only for spatial ones).
     * 
     * @return the SRID, can be <code>null</code> (unknown or not a spatial expression)
     */
    public String getSRID();

    /**
     * Returns the corresponding SQL snippet, with question marks for every {@link SQLLiteral} argument.
     * 
     * @see #getLiterals()
     * 
     * @return the corresponding SQL snippet, never <code>null</code>
     */
    public StringBuilder getSQL();

    /**
     * Returns the {@link SQLLiteral} that occur in the expression, in same order as in the SQL snippet.
     * 
     * @see #getSQL()
     * 
     * @return the SQL literals, never <code>null</code>
     */
    public List<SQLLiteral> getLiterals();

    /**
     * Propagates type information to this expression (=performs a type cast).
     * 
     * @param pt
     *            primitive type, must not be <code>null</code>
     * @throws IllegalArgumentException
     *             if the cast cannot be performed
     */
    public void cast( PrimitiveType pt );
}
