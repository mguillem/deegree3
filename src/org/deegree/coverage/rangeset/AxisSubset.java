//$HeadURL: svn+ssh://rbezema@svn.wald.intevation.org/deegree/deegree3/services/trunk/src/org/deegree/services/wcs/model/AxisSubset.java $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.coverage.rangeset;

import java.util.Iterator;
import java.util.List;

/**
 * The <code>AxisSubset</code> class represents the subset defined on one of the axis of the coverage.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: rbezema $
 * @version $Revision: 19041 $, $Date: 2009-08-11 17:04:57 +0200 (Di, 11 Aug 2009) $
 * 
 */
public class AxisSubset {

    private final List<Interval<?, ?>> intervals;

    private final List<SingleValue<?>> singleValues;

    private final String name;

    private final String label;

    /**
     * @param name
     * @param label
     *            may be <code>null</code>, in this case the name will be returned as label.
     * @param intervals
     * @param singleValues
     */
    public AxisSubset( String name, String label, List<Interval<?, ?>> intervals, List<SingleValue<?>> singleValues ) {
        this.name = name;
        this.label = label;
        this.intervals = intervals;
        this.singleValues = singleValues;

    }

    /**
     * @return the intervals
     */
    public final List<Interval<?, ?>> getIntervals() {
        return intervals;
    }

    /**
     * @return the singleValues
     */
    public final List<SingleValue<?>> getSingleValues() {
        return singleValues;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param other
     * @return true if this {@link AxisSubset} matches the given AxisSubset, e.g. if the names are equal and the axis
     *         values have matching parameters in the given one.
     */
    public boolean match( AxisSubset other ) {
        boolean result = other.getName().equalsIgnoreCase( name );
        if ( result ) {
            boolean ic = checkIntervals( other.getIntervals() );
            boolean sc = checkSingles( other.getSingleValues() );
            result = ic && sc;
        }
        return result;
    }

    /**
     * @param otherValues
     * @return true if the given singleValues match these of the given single values, the types are considered.
     */
    private boolean checkSingles( List<SingleValue<?>> otherValues ) {
        boolean result = false;
        if ( singleValues == null || singleValues.isEmpty() ) {
            // if this axissubset has no singlevalues, than they match
            return true;
        }

        if ( otherValues != null && !otherValues.isEmpty() ) {
            for ( SingleValue<?> sv : singleValues ) {
                // rb: iterate over all values, if one of them mismatches this method will return false.
                if ( sv != null ) {
                    // if the value == null, the default value must be taken into account, therefore no validity check
                    // can
                    // be done.
                    if ( sv.value != null ) {
                        Iterator<SingleValue<?>> iterator = otherValues.iterator();
                        while ( iterator.hasNext() && !result ) {
                            SingleValue<?> ov = iterator.next();
                            result = sv.equals( ov );
                        }

                        if ( !result ) {
                            // could not find a single value matching.
                            break;
                        }

                    }
                } else {
                    result = true;
                }

            }
        }

        return result;
    }

    /**
     * @param otherIntervals
     * @return true if the given intervals match this intervals.
     */
    private boolean checkIntervals( List<Interval<?, ?>> otherIntervals ) {
        boolean result = false;
        if ( intervals == null || intervals.isEmpty() ) {
            // if this axissubset has no intervals, than they match
            return true;
        }
        for ( Interval<?, ?> inter : intervals ) {
            if ( inter != null ) {
                Iterator<Interval<?, ?>> iterator = otherIntervals.iterator();
                while ( iterator.hasNext() && !result ) {
                    Interval<?, ?> oi = iterator.next();
                    result = oi.isInBounds( inter );
                }
                if ( !result ) {
                    // could not find a single value matching.
                    break;
                }
            }
        }

        return result;
    }

    /**
     * @return the label or if not present the name
     */
    public String getLabel() {
        return label == null ? name : label;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" ).append( name );
        if ( label != null && !"".equals( label ) ) {
            sb.append( " {'" ).append( label ).append( "'}" );
        }
        sb.append( ": " );
        if ( intervals != null && !intervals.isEmpty() ) {
            Iterator<Interval<?, ?>> it = intervals.iterator();
            while ( it.hasNext() ) {
                Interval<?, ?> in = it.next();
                if ( in != null ) {
                    sb.append( "[" ).append( in.toString() ).append( "]" );
                }
                if ( it.hasNext() ) {
                    sb.append( "," );
                }
            }
        }
        if ( singleValues != null && !singleValues.isEmpty() ) {
            Iterator<SingleValue<?>> it = singleValues.iterator();
            while ( it.hasNext() ) {
                SingleValue<?> sv = it.next();
                if ( sv != null ) {
                    sb.append( "[" ).append( sv.toString() ).append( "]" );
                }
                if ( it.hasNext() ) {
                    sb.append( "," );
                }
            }
        }
        sb.append( "]" );
        return sb.toString();
    }
}
