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
package org.deegree.services.controller.csw.exporthandling;

import static org.deegree.protocol.csw.CSWConstants.CSW_202_DISCOVERY_SCHEMA;
import static org.deegree.protocol.csw.CSWConstants.CSW_202_NS;
import static org.deegree.protocol.csw.CSWConstants.CSW_PREFIX;
import static org.deegree.protocol.csw.CSWConstants.VERSION_202;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.commons.tom.ows.Version;
import org.deegree.commons.utils.kvp.InvalidParameterValueException;
import org.deegree.commons.xml.stax.XMLStreamWriterWrapper;
import org.deegree.record.persistence.RecordStore;
import org.deegree.services.controller.csw.getrecordbyid.GetRecordById;
import org.deegree.services.controller.utils.HttpResponseBuffer;
import org.deegree.services.csw.CSWService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the export functionality for a {@link GetRecordById} request
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author: thomas $
 * 
 * @version $Revision: $, $Date: $
 */
public class GetRecordByIdHandler {

    private static final Logger LOG = LoggerFactory.getLogger( GetRecordByIdHandler.class );

    private CSWService service;

    /**
     * Creates a new {@link GetRecordByIdHandler} instance that uses the given service to lookup the {@link RecordStore}
     * s.
     * 
     * @param service
     */
    public GetRecordByIdHandler( CSWService service ) {
        this.service = service;

    }

    /**
     * Preprocessing for the export of a {@link GetRecordById} request
     * 
     * @param getRecBI
     *            the parsed getRecordById request
     * @param response
     *            for the servlet request to the client
     * @param isSoap
     * @throws IOException
     * @throws XMLStreamException
     * @throws InvalidParameterValueException
     */
    public void doGetRecordById( GetRecordById getRecBI, HttpResponseBuffer response, boolean isSoap )
                            throws XMLStreamException, IOException, InvalidParameterValueException {

        LOG.debug( "doGetRecords: " + getRecBI );

        Version version = getRecBI.getVersion();

        response.setContentType( getRecBI.getOutputFormat() );

        // to be sure of a valid response
        String schemaLocation = "";
        if ( getRecBI.getVersion() == VERSION_202 ) {
            schemaLocation = CSW_202_NS + " " + CSW_202_DISCOVERY_SCHEMA;
        }

        XMLStreamWriter xmlWriter = getXMLResponseWriter( response, schemaLocation );
        try {
            export( xmlWriter, getRecBI, version, isSoap );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        xmlWriter.flush();

    }

    /**
     * Exports the correct recognized request and determines to which version export it should delegate the request
     * 
     * @param xmlWriter
     * @param getRecBI
     * @param response
     * @param version
     * @throws XMLStreamException
     * @throws SQLException
     */
    private void export( XMLStreamWriter xmlWriter, GetRecordById getRecBI, Version version, boolean isSoap )
                            throws XMLStreamException, SQLException {
        if ( VERSION_202.equals( version ) ) {
            export202( xmlWriter, getRecBI, isSoap );
        } else {
            throw new IllegalArgumentException( "Version '" + version + "' is not supported." );
        }

    }

    /**
     * Exporthandling for the CSW version 2.0.2
     * 
     * @param xmlWriter
     * @param getRecBI
     * @throws XMLStreamException
     * @throws SQLException
     */
    private void export202( XMLStreamWriter writer, GetRecordById getRecBI, boolean isSoap )
                            throws XMLStreamException, SQLException {

        writer.setDefaultNamespace( CSW_202_NS );
        writer.setPrefix( CSW_PREFIX, CSW_202_NS );
        if ( !isSoap ) {
            writer.writeStartDocument();
        }
        writer.writeStartElement( CSW_202_NS, "GetRecordByIdResponse" );

        if ( service.getRecordStore() != null ) {
            try {
                for ( RecordStore rec : service.getRecordStore() ) {
                    rec.getRecordById( writer, getRecBI.getRequestedIds(), getRecBI.getOutputSchema(),
                                       getRecBI.getElementSetName() );
                }
            } catch ( InvalidParameterValueException e ) {
                throw new InvalidParameterValueException( "The requested identifier is no available in the dataset." );
            }
        }

        writer.writeEndDocument();

    }

    /**
     * Returns an <code>XMLStreamWriter</code> for writing an XML response document.
     * 
     * @param writer
     *            writer to write the XML to, must not be null
     * @param schemaLocation
     *            allows to specify a value for the 'xsi:schemaLocation' attribute in the root element, must not be null
     * @return {@link XMLStreamWriter}
     * @throws XMLStreamException
     * @throws IOException
     */
    static XMLStreamWriter getXMLResponseWriter( HttpResponseBuffer writer, String schemaLocation )
                            throws XMLStreamException, IOException {

        if ( schemaLocation == null ) {
            return writer.getXMLWriter();
        }
        return new XMLStreamWriterWrapper( writer.getXMLWriter(), schemaLocation );
    }

}
