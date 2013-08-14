//$HeadURL$
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
package org.deegree.protocol.wms.sld;

import org.deegree.filter.OperatorFilter;
import org.deegree.layer.LayerRef;
import org.deegree.style.StyleRef;

/**
 * Encapsulates a NamedLayer parsed from SLD 1.1.0
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SldNamedLayer {

    private final LayerRef layer;

    private final StyleRef style;

    private final OperatorFilter filter;

    /**
     * @param layer
     *            contains the name of the named layer, never <code>null</code>
     * @param style
     *            never <code>null</code>
     * @param filter
     *            may be <code>null</code> if no filter is specified
     */
    public SldNamedLayer( LayerRef layer, StyleRef style, OperatorFilter filter ) {
        this.layer = layer;
        this.style = style;
        this.filter = filter;
    }

    /**
     * @return never <code>null</code>
     */
    public LayerRef getLayer() {
        return layer;
    }

    /**
     * @return never <code>null</code>
     */
    public StyleRef getStyle() {
        return style;
    }

    /**
     * @return <code>null</code> if no filter is specified
     */
    public OperatorFilter getFilter() {
        return filter;
    }

}