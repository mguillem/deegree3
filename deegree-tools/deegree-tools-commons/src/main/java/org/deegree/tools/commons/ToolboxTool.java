package org.deegree.tools.commons;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public interface ToolboxTool {

    String getDescription();

    void execute( String[] args )
                    throws Exception;
}
