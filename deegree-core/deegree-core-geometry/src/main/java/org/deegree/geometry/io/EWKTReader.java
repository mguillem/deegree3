/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright by:
 (C) 2018 - 2022, grit GmbH
 (C) 2009 - 2016, Open Source Geospatial Foundation (OSGeo)
 (C) 2001, Vivid Solutions

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
 This is a port of the JTS WKTReader to handle SQL MM types such as Curve.
 We have subclassed so that our implementation can be used anywhere
 a WKTReader is needed. We would of tried for more code reuse  except
 the base class has reduced everything to private methods.

 This class also contains code written by Mark Leslie for PostGIS while working
 at Refractions Research with whom we have a code contribution agreement.

 Contact information:
  e-mail: info@deegree.org
 website: http://www.deegree.org/

 ----------------------------------------------------------------------------*/
package org.deegree.geometry.io;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.geometry.Geometry;
import org.deegree.geometry.GeometryFactory;
import org.deegree.geometry.multi.MultiCurve;
import org.deegree.geometry.multi.MultiGeometry;
import org.deegree.geometry.multi.MultiLineString;
import org.deegree.geometry.multi.MultiPoint;
import org.deegree.geometry.multi.MultiPolygon;
import org.deegree.geometry.multi.MultiSurface;
import org.deegree.geometry.primitive.Curve;
import org.deegree.geometry.primitive.LineString;
import org.deegree.geometry.primitive.LinearRing;
import org.deegree.geometry.primitive.Point;
import org.deegree.geometry.primitive.Polygon;
import org.deegree.geometry.primitive.Ring;
import org.deegree.geometry.primitive.Surface;
import org.deegree.geometry.primitive.segments.ArcString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.util.Assert;

/**
 * Create a geometry from SQL Multi-Media Extension Well-Known Text which allows curves.
 *
 * @version 1.7
 */
public class EWKTReader {
    private static final String EMPTY = "EMPTY";

    private static final String COMMA = ",";

    private static final String L_PAREN = "(";

    private static final String R_PAREN = ")";

    private static final String NAN_SYMBOL = "NaN";

    private GeometryFactory geometryFactory;

    // private PrecisionModel precisionModel;

    private StreamTokenizer tokenizer;

    /** Creates a reader that creates objects using the default {@link GeometryFactory}. */
    public EWKTReader() {
        this( new GeometryFactory() );
    }

    /**
     * Creates a reader that creates objects using the given {@link GeometryFactory}.
     *
     * @param geometryFactory
     *            the factory used to create <code>Geometry</code>s.
     */
    public EWKTReader( GeometryFactory geometryFactory ) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * Reads a Well-Known Text representation of a {@link Geometry} from a {@link String}.
     *
     * @param wellKnownText
     *            one or more <Geometry Tagged Text>strings (see the OpenGIS Simple Features Specification) separated by
     *            whitespace
     * @return a <code>Geometry</code> specified by <code>wellKnownText</code>
     * @throws ParseException
     *             if a parsing problem occurs
     */
    public Geometry read(String wellKnownText) throws ParseException {
        try (StringReader reader = new StringReader(wellKnownText)) {
            return read( reader );
        }
    }

    /**
     * Reads a Well-Known Text representation of a {@link Geometry} from a {@link Reader}.
     *
     * @param reader
     *            a Reader which will return a <Geometry Tagged Text> string (see the OpenGIS Simple Features
     *            Specification)
     * @return a <code>Geometry</code> read from <code>reader</code>
     * @throws ParseException
     *             if a parsing problem occurs
     */
    public Geometry read( Reader reader )
                            throws ParseException {
        tokenizer = new StreamTokenizer( reader );
        // set tokenizer to NOT parse numbers
        tokenizer.resetSyntax();
        tokenizer.wordChars( 'a', 'z' );
        tokenizer.wordChars( 'A', 'Z' );
        tokenizer.wordChars( 128 + 32, 255 );
        tokenizer.wordChars( '0', '9' );
        tokenizer.wordChars( '-', '-' );
        tokenizer.wordChars( '+', '+' );
        tokenizer.wordChars( '.', '.' );
        tokenizer.whitespaceChars( 0, ' ' );
        tokenizer.commentChar( '#' );

        try {
            return readGeometryTaggedText();
        } catch ( IOException e ) {
            throw new ParseException( e.toString() );
        }
    }

    /**
     * Returns the next array of <code>Coordinate</code>s in the stream.
     *
     * @return the next array of <code>Coordinate</code>s in the stream, or an empty array if EMPTY is the next element
     *         returned by the stream.
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private List<Point> getCoordinates()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return Collections.emptyList();
        }
        ArrayList<Point> coordinates = new ArrayList<>();
        coordinates.add( getPreciseCoordinate() );
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            coordinates.add( getPreciseCoordinate() );
            nextToken = getNextCloserOrComma();
        }
        return coordinates;
    }

    private List<Point> getCoordinateList( boolean openExpected )
                            throws IOException,
                            ParseException {
        String nextToken;
        if ( openExpected ) {
            nextToken = getNextEmptyOrOpener();
            if ( nextToken.equals( EMPTY ) ) {
                return Collections.emptyList();
            }
        }
        ArrayList<Point> coordinates = new ArrayList<>();
        coordinates.add( getPreciseCoordinate() );
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            coordinates.add( getPreciseCoordinate() );
            nextToken = getNextCloserOrComma();
        }
        return coordinates;
    }

    private Point getPreciseCoordinate()
                            throws IOException,
                            ParseException {
        if (isEmptyNext()) {
            // eat the EMPTY
            tokenizer.nextToken();
            return null;
        }
        double x, y, z;
        x = getNextNumber();
        y = getNextNumber();
        if ( isNumberNext() ) {
            z = getNextNumber();
            return geometryFactory.createPoint( null, x, y, z, null );
        }

        return geometryFactory.createPoint( null, x, y, null );
    }

    private boolean isEmptyNext() throws IOException {
        tokenizer.nextToken();
        boolean ret = false;
        if (tokenizer.sval.equalsIgnoreCase("EMPTY")) {
            ret = true;
        }
        tokenizer.pushBack();
        return ret;
    }

    private boolean isNumberNext()
                            throws IOException {
        int type = tokenizer.nextToken();
        tokenizer.pushBack();
        return type == StreamTokenizer.TT_WORD;
    }

    /**
     * Parses the next number in the stream. Numbers with exponents are handled. <tt>NaN</tt> values are handled
     * correctly, and the case of the "NaN" token is not significant.
     *
     * @return the next number in the stream
     * @throws ParseException
     *             if the next token is not a valid number
     * @throws IOException
     *             if an I/O error occurs
     */
    private double getNextNumber()
                            throws IOException,
                            ParseException {
        int type = tokenizer.nextToken();
        switch ( type ) {
        case StreamTokenizer.TT_WORD: {
            if ( tokenizer.sval.equalsIgnoreCase( NAN_SYMBOL ) ) {
                return Double.NaN;
            } else {
                try {
                    return Double.parseDouble( tokenizer.sval );
                } catch ( NumberFormatException ex ) {
                    throw new ParseException( "Invalid number: " + tokenizer.sval );
                }
            }
        }
        }
        parseError( "number" );
        return 0.0;
    }

    /**
     * Returns the next EMPTY or L_PAREN in the stream as uppercase text.
     *
     * @return the next EMPTY or L_PAREN in the stream as uppercase text.
     * @throws ParseException
     *             if the next token is not EMPTY or L_PAREN
     * @throws IOException
     *             if an I/O error occurs
     */
    private String getNextEmptyOrOpener()
                            throws IOException,
                            ParseException {
        String nextWord = getNextWord();
        if ( nextWord.equals( EMPTY ) || nextWord.equals( L_PAREN ) ) {
            return nextWord;
        }
        parseError( EMPTY + " or " + L_PAREN );
        return null;
    }

    /**
     * Returns the next R_PAREN or COMMA in the stream.
     *
     * @return the next R_PAREN or COMMA in the stream
     * @throws ParseException
     *             if the next token is not R_PAREN or COMMA
     * @throws IOException
     *             if an I/O error occurs
     */
    private String getNextCloserOrComma()
                            throws IOException,
                            ParseException {
        String nextWord = getNextWord();
        if ( nextWord.equals( COMMA ) || nextWord.equals( R_PAREN ) ) {
            return nextWord;
        }
        parseError( COMMA + " or " + R_PAREN );
        return null;
    }

    /**
     * Returns the next R_PAREN in the stream.
     *
     * @return the next R_PAREN in the stream
     * @throws ParseException
     *             if the next token is not R_PAREN
     * @throws IOException
     *             if an I/O error occurs
     */
    private String getNextCloser()
                            throws IOException,
                            ParseException {
        String nextWord = getNextWord();
        if ( nextWord.equals( R_PAREN ) ) {
            return nextWord;
        }
        parseError( R_PAREN );
        return null;
    }

    /**
     * Returns the next word in the stream.
     *
     * @return the next word in the stream as uppercase text
     * @throws ParseException
     *             if the next token is not a word
     * @throws IOException
     *             if an I/O error occurs
     */
    private String getNextWord()
                            throws IOException,
                            ParseException {
        int type = tokenizer.nextToken();
        switch ( type ) {
        case StreamTokenizer.TT_WORD:
            String word = tokenizer.sval;
            if ( word.equalsIgnoreCase( EMPTY ) )
                return EMPTY;
            return word;

        case '(':
            return L_PAREN;
        case ')':
            return R_PAREN;
        case ',':
            return COMMA;
        }
        parseError( "word" );
        return null;
    }

    /**
     * Throws a formatted ParseException for the current token.
     *
     * @param expected
     *            a description of what was expected
     * @throws ParseException
     */
    private void parseError( String expected )
                            throws ParseException {
        // throws Asserts for tokens that should never be seen
        if ( tokenizer.ttype == StreamTokenizer.TT_NUMBER )
            Assert.shouldNeverReachHere( "Unexpected NUMBER token" );
        if ( tokenizer.ttype == StreamTokenizer.TT_EOL )
            Assert.shouldNeverReachHere( "Unexpected EOL token" );

        String tokenStr = tokenString();
        throw new ParseException( "Expected " + expected + " but found " + tokenStr );
    }

    /**
     * Gets a description of the current token
     *
     * @return a description of the current token
     */
    private String tokenString() {
        switch ( tokenizer.ttype ) {
        case StreamTokenizer.TT_NUMBER:
            return "<NUMBER>";
        case StreamTokenizer.TT_EOL:
            return "End-of-Line";
        case StreamTokenizer.TT_EOF:
            return "End-of-Stream";
        case StreamTokenizer.TT_WORD:
            return "'" + tokenizer.sval + "'";
        }
        return "'" + (char) tokenizer.ttype + "'";
    }

    /**
     * Creates a <code>Geometry</code> using the next token in the stream.
     *
     * @return a <code>Geometry</code> specified by the next token in the stream
     * @throws ParseException
     *             if the coordinates used to create a <code>Polygon</code> shell and holes do not form closed
     *             linestrings, or if an unexpected token was encountered
     * @throws IOException
     *             if an I/O error occurs
     */
    private Geometry readGeometryTaggedText()
                            throws IOException,
                            ParseException {
        String type = null;

        try {
            type = getNextWord();
        } catch ( IOException e ) {
            return null;
        } catch ( ParseException e ) {
            return null;
        }

        if ( type.equalsIgnoreCase( "POINT" ) ) {
            return readPointText();
        } else if ( type.equalsIgnoreCase( "LINESTRING" ) ) {
            LineString line = readLineStringText();
            return line.getControlPoints().size() == 0 ? null : line ;
        } else if ( type.equalsIgnoreCase( "LINEARRING" ) ) {
            return readLinearRingText();
        } else if ( type.equalsIgnoreCase( "POLYGON" ) ) {
            return readPolygonText();
        } else if ( type.equalsIgnoreCase( "MULTIPOINT" ) ) {
            return readMultiPointText();
        } else if ( type.equalsIgnoreCase( "MULTILINESTRING" ) ) {
            return readMultiLineStringText();
        } else if ( type.equalsIgnoreCase( "MULTICURVE" ) ) {
            return readMultiCurveText();
        } else if ( type.equalsIgnoreCase( "MULTIPOLYGON" ) ) {
            return readMultiPolygonText();
        } else if ( type.equalsIgnoreCase( "GEOMETRYCOLLECTION" ) ) {
            return readGeometryCollectionText();
        } else if ( type.equalsIgnoreCase( "CIRCULARSTRING" ) ) {
            ArcString arcString = readCircularStringText();
            return arcString == null ? null : geometryFactory.createCurve( null, null, arcString );
        } else if ( type.equalsIgnoreCase( "COMPOUNDCURVE" ) ) {
            return readCompoundCurveText();
        } else if ( type.equalsIgnoreCase( "CURVEPOLYGON" ) ) {
            return readCurvePolygonText();
        } else if ( type.equalsIgnoreCase( "MULTISURFACE" ) ) {
            return readMultiSurfaceText();
        }
        throw new ParseException( "Unknown geometry type: " + type );
    }

    /**
     * Creates a <code>Point</code> using the next token in the stream.
     *
     * @return a <code>Point</code> specified by the next token in the stream
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private Point readPointText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return null;
            // return geometryFactory.createPoint( (Coordinate) null );
        }
        Point point = getPreciseCoordinate();
        getNextCloser();
        return point;
    }

    /**
     * Creates a <code>LineString</code> using the next token in the stream.
     *
     * @return a <code>LineString</code> specified by the next token in the stream
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private LineString readLineStringText()
                            throws IOException,
                            ParseException {
        return geometryFactory.createLineString( null, null, geometryFactory.createPoints( getCoordinates() ) );
    }

    /**
     * Creates a <code>LineString</code> using the next token in the stream.
     *
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private ArcString readCircularStringText()
                            throws IOException,
                            ParseException {
        List<Point> coordinates = getCoordinateList( true );
        if ( coordinates.size() == 0 ) {
            return null;
            // return geometryFactory.createCurvedGeometry( new LiteCoordinateSequence( new Coordinate[0] ) );
        } else if ( coordinates.size() < 3 ) {
            throw new ParseException( "A CIRCULARSTRING must contain at least 3 control points" );
        } else {
            return geometryFactory.createArcString( geometryFactory.createPoints( coordinates ) );
        }
    }

    // private double[] toControlPoints( List<Coordinate> coordinates ) {
    // double[] result = new double[coordinates.size() * 2];
    // for ( int i = 0; i < coordinates.size(); i++ ) {
    // Coordinate c = coordinates.get( i );
    // result[i * 2] = c.x;
    // result[i * 2 + 1] = c.y;
    // }
    //
    // return result;
    // }

    private Curve readCompoundCurveText()
                            throws IOException,
                            ParseException {
        List<Curve> lineStrings = getLineStrings();
        return geometryFactory.createCompositeCurve( null, null, lineStrings );
    }

    /**
     * Handles mixed line string notation - either LineString (the default) or CircularCurve. Isolated as a seperate
     * method as I think we will need to call this from the polygon code.
     *
     * @return List of LineString (defined in a mixed format)
     * @throws IOException
     * @throws ParseException
     */
    List<Curve> getLineStrings()
                            throws IOException,
                            ParseException {
        ArrayList<Curve> lineStrings = new ArrayList<>();
        String nextWord = getNextEmptyOrOpener();
        if ( nextWord.equals( EMPTY ) ) {
            return null;
            // return lineStrings;
        }
        // must be an opener!
        nextWord = COMMA;
        while ( nextWord.equals( COMMA ) ) {
            nextWord = getNextWord();
            if ( nextWord.equals( L_PAREN ) ) {
                List<Point> coords = getCoordinateList( false );
                LineString lineString = geometryFactory.createLineString( null, null,
                                                                          geometryFactory.createPoints( coords ) );
                lineStrings.add( lineString );
            } else if ( nextWord.equalsIgnoreCase( "CIRCULARSTRING" ) ) {
                ArcString circularString = readCircularStringText();
                Curve curve = geometryFactory.createCurve( null, null, circularString );
                lineStrings.add( curve );
            } else if ( nextWord.equalsIgnoreCase( "COMPOUNDCURVE" ) ) {
                Curve compound = readCompoundCurveText();
                lineStrings.add( compound );
            }

            nextWord = getNextCloserOrComma();
        }
        return lineStrings;
    }

    /**
     * This method will read a LineString, CircularString or CompoundCurve and return the result as a LinearRing.
     *
     * @return LinearRing
     *         <p>
     *         This method expects either "EMPTY", "(", "CIRCULARSTRING", or "COMPOIUNDCURVE" to start out with.
     * @throws IOException
     * @throws ParseException
     */
    private Curve readCurvedLinearRingText()
                            throws IOException,
                            ParseException {
        String nextWord = getNextWord();
        if ( nextWord.equals( L_PAREN ) ) {
            List<Point> coords = getCoordinateList( false );
            return geometryFactory.createLinearRing( null, null, geometryFactory.createPoints( coords ) );
        } else if ( nextWord.equalsIgnoreCase( "CIRCULARSTRING" ) ) {
            return geometryFactory.createCurve( null, null, readCircularStringText() );
        } else if ( nextWord.equalsIgnoreCase( "COMPOUNDCURVE" ) ) {
            return readCompoundCurveText();
        } else {
            parseError( L_PAREN + ", CIRCULARSTRING or COMPOUNDCURVE" );
            return null;
        }
    }

    /**
     * Creates a <code>LinearRing</code> using the next token in the stream.
     *
     * @return a <code>LinearRing</code> specified by the next token in the stream
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if the coordinates used to create the <code>LinearRing</code> do not form a closed linestring, or if
     *             an unexpected token was encountered
     */
    private LinearRing readLinearRingText()
                            throws IOException,
                            ParseException {
        return geometryFactory.createLinearRing( null, null, geometryFactory.createPoints( getCoordinates() ) );
    }

    /**
     * Creates a <code>MultiPoint</code> using the next token in the stream.
     *
     * @return a <code>MultiPoint</code> specified by the next token in the stream
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private MultiPoint readMultiPointText()
                            throws IOException,
                            ParseException {
        return geometryFactory.createMultiPoint( null, null, getCoordinatesForMultiPoint() );
        }

    /**
     * Get a Coordinate array for a MultiPoint. Specifically handle both WKT styles: MULTIPOINT (111 -47, 110 -46.5) and
     * MULTIPOINT ((111 -47), (110 -46.5)).
     *
     * @return An Array of Coordinates
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private List<Point> getCoordinatesForMultiPoint()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return emptyList();
        }

        // Check for inner parens
        boolean innerParens = false;
        try {
            String peek = getNextWord();
            innerParens = peek.equals( L_PAREN );
        } catch ( ParseException ex ) {
            // Do nothing
        } finally {
            tokenizer.pushBack();
        }

        ArrayList<Point> coordinates = new ArrayList<>();
        if ( innerParens ) {
            List<Point> coords = getCoordinates();
            coordinates.add( coords.get( 0 ) );
            nextToken = getNextCloserOrComma();
            while ( nextToken.equals( COMMA ) ) {
                coords = getCoordinates();
                coordinates.add( coords.get( 0 ) );
                nextToken = getNextCloserOrComma();
            }
        } else {
            coordinates.add( getPreciseCoordinate() );
            nextToken = getNextCloserOrComma();
            while ( nextToken.equals( COMMA ) ) {
                coordinates.add( getPreciseCoordinate() );
                nextToken = getNextCloserOrComma();
            }
        }
        return coordinates;
    }

    /**
     * Creates an array of <code>Point</code>s having the given <code>Coordinate</code> s.
     *
     * @param coordinates
     *            the <code>Coordinate</code>s with which to create the <code>Point</code>s
     * @return <code>Point</code>s created using this <code>WKTReader</code> s <code>GeometryFactory
     *     </code>
     */
    // private Point[] toPoints( Coordinate[] coordinates ) {
    // ArrayList points = new ArrayList();
    // for ( int i = 0; i < coordinates.length; i++ ) {
    // points.add( geometryFactory.createPoint( coordinates[i] ) );
    // }
    // return (Point[]) points.toArray( new Point[] {} );
    // }

    /**
     * Creates a <code>Polygon</code> using the next token in the stream.
     *
     * @return a <code>Polygon</code> specified by the next token in the stream
     * @throws ParseException
     *             if the coordinates used to create the <code>Polygon</code> shell and holes do not form closed
     *             linestrings, or if an unexpected token was encountered.
     * @throws IOException
     *             if an I/O error occurs
     */
    private Polygon readPolygonText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return null;
        }
        ArrayList<Ring> holes = new ArrayList<>();
        LinearRing shell = readLinearRingText();
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            LinearRing hole = readLinearRingText();
            holes.add( hole );
            nextToken = getNextCloserOrComma();
        }
        return geometryFactory.createPolygon( null, null, shell, holes );
    }

    private MultiCurve<?> readMultiCurveText()
                            throws IOException,
                            ParseException {
        List<Curve> members = getLineStrings();
        return geometryFactory.createMultiCurve( null, null, members );
    }

    private Polygon readCurvePolygonText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            throw null;
        }
        if ( !nextToken.equals( L_PAREN ) ) {
            parseError( "Ring expected" );
        }
        Ring shell = geometryFactory.createRing( null, null, singletonList( readCurvedLinearRingText() ) );
        ArrayList<Ring> holes = new ArrayList<>();
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            Curve hole = readCurvedLinearRingText();
            holes.add( geometryFactory.createRing( null, null, singletonList( hole ) ) );
            nextToken = getNextCloserOrComma();
        }
        return geometryFactory.createPolygon( null, null, shell, holes );
    }

    /**
     * Creates a <code>MultiLineString</code> using the next token in the stream.
     *
     * @return a <code>MultiLineString</code> specified by the next token in the stream
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private MultiLineString readMultiLineStringText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return geometryFactory.createMultiLineString( null, null, emptyList() );
        }
        ArrayList<LineString> lineStrings = new ArrayList<>();
        LineString lineString = readLineStringText();
        lineStrings.add( lineString );
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            lineString = readLineStringText();
            lineStrings.add( lineString );
            nextToken = getNextCloserOrComma();
        }
        return geometryFactory.createMultiLineString( null, null, lineStrings );
    }

    /**
     * Creates a <code>MultiPolygon</code> using the next token in the stream.
     *
     * @return a <code>MultiPolygon</code> specified by the next token in the stream, or if if the coordinates used to
     *         create the <code>Polygon</code> shells and holes do not form closed linestrings.
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private MultiPolygon readMultiPolygonText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return geometryFactory.createMultiPolygon( null, null, emptyList() );
        }
        ArrayList<Polygon> polygons = new ArrayList<>();
        Polygon polygon = readPolygonText();
        polygons.add( polygon );
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            polygon = readPolygonText();
            polygons.add( polygon );
            nextToken = getNextCloserOrComma();
        }
        return geometryFactory.createMultiPolygon( null, null, polygons );
    }

    /**
     * Creates a <code>MultiSurface</code> using the next token in the stream.
     *
     * @return a <code>MultiSurface</code> specified by the next token in the stream, or if if the coordinates used to
     *         create the <code>Polygon</code> shells and holes do not form closed linestrings.
     * @throws IOException
     *             if an I/O error occurs
     * @throws ParseException
     *             if an unexpected token was encountered
     */
    private MultiSurface<?> readMultiSurfaceText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return geometryFactory.createMultiSurface( null, null, emptyList() );
        }
        ArrayList<Surface> polygons = new ArrayList<>();
        // must be an opener!
        String nextWord = COMMA;
        while ( nextWord.equals( COMMA ) ) {
            nextWord = getNextWord();
            if ( nextWord.equals( L_PAREN ) || nextWord.equals( EMPTY ) ) {
                tokenizer.pushBack();
                Polygon polygon = readPolygonText();
                polygons.add( polygon );
            } else if ( nextWord.equalsIgnoreCase( "CURVEPOLYGON" ) ) {
                Polygon polygon = readCurvePolygonText();
                polygons.add( polygon );
            }

            nextWord = getNextCloserOrComma();
        }

        return geometryFactory.createMultiSurface( null, null, polygons );
    }

    /**
     * Creates a <code>GeometryCollection</code> using the next token in the stream.
     *
     * @return a <code>GeometryCollection</code> specified by the next token in the stream
     * @throws ParseException
     *             if the coordinates used to create a <code>Polygon</code> shell and holes do not form closed
     *             linestrings, or if an unexpected token was encountered
     * @throws IOException
     *             if an I/O error occurs
     */
    private MultiGeometry<Geometry> readGeometryCollectionText()
                            throws IOException,
                            ParseException {
        String nextToken = getNextEmptyOrOpener();
        if ( nextToken.equals( EMPTY ) ) {
            return geometryFactory.createMultiGeometry( null, null, emptyList() );
        }
        ArrayList<Geometry> geometries = new ArrayList<>();
        Geometry geometry = readGeometryTaggedText();
        geometries.add( geometry );
        nextToken = getNextCloserOrComma();
        while ( nextToken.equals( COMMA ) ) {
            geometry = readGeometryTaggedText();
            geometries.add( geometry );
            nextToken = getNextCloserOrComma();
        }
        return geometryFactory.createMultiGeometry( null, null, geometries );
    }
}