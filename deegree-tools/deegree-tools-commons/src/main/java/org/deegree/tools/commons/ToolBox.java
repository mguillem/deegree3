//$HeadURL$
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

package org.deegree.tools.commons;

import org.deegree.commons.utils.DeegreeAALogoUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Allows for convenient starting and listing of available deegree command line tools.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
public class ToolBox {

    private final int NAME_PAD = 3;

    private final int DESCRIPTION_PAD = 5;

    private final int TEXT_WIDTH = 80;

    private List<ToolInfo> tools;

    ToolBox( Map<String, ToolboxTool> tools ) {
        this.tools = tools.values().stream().map( tool -> new ToolInfo( tool ) ).collect( Collectors.toList() );
    }

    /**
     * The list of tools is printed in a readable format
     */
    private void printList() {
        DeegreeAALogoUtils.print( System.out );
        System.out.println(
                        "\nAvailable tools, start with 'd3toolbox <tool> <args>' or omit the args to see all available parameters of the tool:\n" );

        // determine the maximum length of a tool name
        int maxLength = -1;
        for ( ToolInfo tool : tools ) {
            if ( tool.getName().length() > maxLength ) {
                maxLength = tool.getName().length();
            }
        }

        for ( ToolInfo tool : tools ) {
            if ( tool != null ) {
                System.out.print( createPadding( NAME_PAD ) );
                System.out.print( tool.getName() );

                // pad line up to maxLength
                System.out.print( createPadding( maxLength - tool.getName().length() ) );

                System.out.print( createPadding( DESCRIPTION_PAD ) );

                printWrappedText( tool.getDescription(), maxLength );
            }
        }
    }

    private void printWrappedText( String description, int maxLength ) {
        // local string that will be chopped off from the left side
        String text = description;

        // maximum description width
        int widthLeft = TEXT_WIDTH - NAME_PAD - maxLength - DESCRIPTION_PAD;

        while ( true ) {
            int nMark = text.indexOf( "\n" );
            if ( nMark != -1 && nMark < widthLeft ) {
                System.out.print( text.substring( 0, nMark ) );
                System.out.print( createPadding( NAME_PAD + maxLength + DESCRIPTION_PAD ) );
                text = text.substring( nMark + 1 );

            } else {
                if ( text.length() - widthLeft > 0 ) {

                    // find last whitespace that occurs before TEXT_WIDH
                    int index = -1;
                    int newIndex;
                    while ( ( newIndex = text.indexOf( " ", index + 1 ) ) != -1 ) {
                        if ( newIndex <= widthLeft ) {
                            index = newIndex;

                        } else {
                            break;
                        }
                    }

                    if ( index == -1 ) {
                        System.out.println();
                        return;
                    }
                    System.out.println( text.substring( 0, index ) );
                    System.out.print( createPadding( NAME_PAD + maxLength + DESCRIPTION_PAD ) );
                    text = text.substring( index + 1 );

                } else {
                    System.out.println( text );
                    break;
                }
            }
        }
    }

    private String createPadding( int padLength ) {
        StringBuffer s = new StringBuffer();
        for ( int i = 0; i < padLength; i++ ) {
            s.append( " " );
        }
        return s.toString();
    }

    private ToolInfo findTool( String toolName ) {
        ToolInfo tool = null;
        for ( ToolInfo toolInfo : tools ) {
            if ( toolInfo.getName().equals( toolName ) ) {
                tool = toolInfo;
                break;
            }
        }
        return tool;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main( String[] args )
                    throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan( "org.deegree.tools" );
        context.refresh();

        Map<String, ToolboxTool> beansOfType = context.getBeansOfType( ToolboxTool.class );

        ToolBox toolbox = new ToolBox( beansOfType );

        if ( args.length == 0 ) {
            toolbox.printList();
        } else {
            ToolInfo tool = toolbox.findTool( args[0] );
            if ( tool != null ) {
                if ( args.length > 1 ) {
                    tool.invoke( Arrays.copyOfRange( args, 1, args.length ) );
                } else {
                    tool.invoke( new String[0] );
                }
            } else {
                System.out.println( "\nNo tool with name '" + args[0] + "' available." );
                toolbox.printList();
            }
        }
    }

    private class ToolInfo {

        private ToolboxTool toolboxTool;

        private String description;

        ToolInfo( ToolboxTool toolboxTool ) {
            this.toolboxTool = toolboxTool;
            try {
                this.description = getDescription();
            } catch ( NullPointerException e ) {
                description = "[FAILURE] Does not implement the Tool annotation, a description is therefore not available.";
            }
        }

        /**
         * Invoke the main method of the given class.
         *
         * @param args
         * @throws Exception
         */
        public void invoke( String[] args )
                        throws Exception {
            toolboxTool.execute( args );
        }

        /**
         * @return the name of the tool
         */
        public String getName() {
            return toolboxTool.getClass().getSimpleName();
        }

        /**
         * @return the description of the tool
         */
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getName() + " - " + getDescription();
        }
    }
}
