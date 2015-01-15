//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.protocol.csw.client;

import static org.deegree.commons.ows.exception.OWSException.NO_APPLICABLE_CODE;
import static org.deegree.protocol.csw.CSWConstants.GMD_LOCAL_PART;
import static org.deegree.protocol.csw.CSWConstants.ISO_19115_NS;
import static org.deegree.protocol.csw.CSWConstants.VERSION_202;
import static org.deegree.protocol.csw.CSWConstants.CSWRequestType.GetRecords;
import static org.deegree.protocol.csw.CSWConstants.CSWRequestType.Transaction;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.ows.metadata.OperationsMetadata;
import org.deegree.commons.ows.metadata.domain.AllowedValues;
import org.deegree.commons.ows.metadata.domain.Domain;
import org.deegree.commons.ows.metadata.domain.PossibleValues;
import org.deegree.commons.ows.metadata.domain.Value;
import org.deegree.commons.ows.metadata.domain.Values;
import org.deegree.commons.ows.metadata.operation.DCP;
import org.deegree.commons.ows.metadata.operation.Operation;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.io.StreamBufferStore;
import org.deegree.commons.xml.CommonNamespaces;
import org.deegree.commons.xml.stax.XMLStreamUtils;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.filter.Filter;
import org.deegree.metadata.MetadataRecord;
import org.deegree.metadata.MetadataRecordFactory;
import org.deegree.protocol.csw.CSWConstants.ResultType;
import org.deegree.protocol.csw.CSWConstants.ReturnableElement;
import org.deegree.protocol.csw.client.getrecords.GetRecords;
import org.deegree.protocol.csw.client.getrecords.GetRecordsResponse;
import org.deegree.protocol.csw.client.getrecords.GetRecordsXMLEncoder;
import org.deegree.protocol.csw.client.transaction.TransactionResponse;
import org.deegree.protocol.csw.client.transaction.TransactionXMLEncoder;
import org.deegree.protocol.ows.client.AbstractOWSClient;
import org.deegree.protocol.ows.exception.OWSExceptionReport;
import org.deegree.protocol.ows.http.OwsHttpClientImpl;
import org.deegree.protocol.ows.http.OwsHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API-level client for accessing servers that implement the <a
 * href="http://www.opengeospatial.org/standards/specifications/catalog">OpenGIS Catalogue Services Specification (CSW)
 * 2.0.2</a> protocol.
 * 
 * <h4>Initialization</h4> In the initial step, one constructs a new {@link CSWClient} instance by invoking the
 * constructor with a URL to a CSW capabilities document. This usually is a <code>GetCapabilities</code> request
 * (including necessary parameters) to a CSW service.
 * 
 * <pre>
 * ...
 *   URL capabilitiesUrl = new URL( "http://...?service=CSW&version=2.0.2&request=GetCapabilities" );
 *   CSWClient cswClient = new CSWClient( capabilitiesUrl );
 * ...
 * </pre>
 * 
 * Afterwards, the initialized {@link CSWClient} instance is bound to the specified service and CSW protocol version.
 * Now, it's possible to access records and perform insert, update and delete requests.
 * 
 * <h4>Accessing records</h4> ...
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CSWClient extends AbstractOWSClient<CSWCapabilitiesAdapter> {

    private static final Logger LOG = LoggerFactory.getLogger( CSWClient.class );

    public enum GetRecordsRequestType {
        GET, POST, SOAP
    };

    public static class GetRecordsBuilder {
        private int startPosition = 1;

        private int numberOfRecords = 50;

        private Filter contraint;

        public GetRecordsBuilder() {
        }

        public GetRecordsBuilder startingAt( int startPosition ) {
            this.startPosition = startPosition;
            return this;
        }

        public GetRecordsBuilder withMax( int numberOfRecords ) {
            this.numberOfRecords = numberOfRecords;
            return this;
        }

        public GetRecordsBuilder withConstraint( Filter contraint ) {
            this.contraint = contraint;
            return this;
        }

        public GetRecords build() {
            return new GetRecords( VERSION_202, startPosition, numberOfRecords, "application/xml", ISO_19115_NS,
                                   Collections.singletonList( new QName( CommonNamespaces.ISOAP10GMDNS, GMD_LOCAL_PART,
                                                                         CommonNamespaces.ISOAP10GMD_PREFIX ) ),
                                   ResultType.results, ReturnableElement.full, contraint );
        }
    }

    /**
     * Creates a new {@link CSWClient} instance with infinite timeout.
     * 
     * @param capaUrl
     *            url of a CSW capabilities document, usually this is a <code>GetCapabilities</code> request to the
     *            service, must not be <code>null</code>
     * @throws OWSExceptionReport
     *             if the server replied with a service exception report
     * @throws XMLStreamException
     * @throws IOException
     *             if a communication/network problem occured
     */
    public CSWClient( URL capaUrl ) throws OWSExceptionReport, XMLStreamException, IOException {
        super( capaUrl, null );
    }

    /**
     * Creates a new {@link CSWClient} instance.
     * 
     * @param capaUrl
     *            url of a CSW capabilities document, usually this is a <code>GetCapabilities</code> request to the
     *            service, must not be <code>null</code>
     * @param connectionTimeout
     *            the timeout for get/post requests in milliseconds, 0 is interpreted as an infinite timeout (default)
     * @throws OWSExceptionReport
     *             if the server replied with a service exception report
     * @throws XMLStreamException
     * @throws IOException
     *             if a communication/network problem occured
     */
    public CSWClient( URL capaUrl, int connectionTimeout, int readTimeout ) throws OWSExceptionReport,
                            XMLStreamException, IOException {
        super( capaUrl, new OwsHttpClientImpl( connectionTimeout, readTimeout, null, null ) );
    }

    @Override
    protected CSWCapabilitiesAdapter getCapabilitiesAdapter( OMElement rootEl, String version )
                            throws IOException {
        CSWCapabilitiesAdapter cswCapAdapter = new CSWCapabilitiesAdapter();
        cswCapAdapter.setRootElement( rootEl );
        return cswCapAdapter;
    }

    public GetRecordsResponse getIsoRecords( ResultType resultType, ReturnableElement elementSetName, Filter constraint )
                            throws IOException, OWSExceptionReport, XMLStreamException, OWSException {
        final GetRecords getRecords = new GetRecordsBuilder().startingAt( 10 ).withMax( 15 ).build();
        return getRecords( getRecords );
    }

    public GetRecordsResponse getIsoRecords( int startPosition, int maxRecords, ResultType resultType,
                                             ReturnableElement elementSetName, Filter constraint )
                            throws IOException, OWSExceptionReport, XMLStreamException, OWSException {
        final GetRecords getRecords = new GetRecordsBuilder().startingAt( startPosition ).withMax( maxRecords ).build();
        return getRecords( getRecords );
    }

    public GetRecordsResponse getRecords( int startPosition, int maxRecords, String outputFormat, String outputSchema,
                                          List<QName> typeNames, ResultType resultType,
                                          ReturnableElement elementSetName, Filter constraint )
                            throws IOException, OWSExceptionReport, XMLStreamException, OWSException {
        GetRecords getRecords = new GetRecords( VERSION_202, startPosition, maxRecords, outputFormat, outputSchema,
                                                typeNames, resultType, elementSetName, constraint );
        return getRecords( getRecords );
    }

    public GetRecordsResponse getRecords( final GetRecords request )
                            throws XMLStreamException, IOException, OWSExceptionReport, OWSException {
        GetRecordsRequestType preferredRequestType = null;
        if ( getEndpointUrlByType( "soap" ) != null ) {
            preferredRequestType = GetRecordsRequestType.SOAP;
        }
        if ( getEndpointUrlByType( "xml" ) != null ) {
            preferredRequestType = GetRecordsRequestType.POST;
        }
        if ( getGetUrl( "GetRecords" ) != null ) {
            preferredRequestType = GetRecordsRequestType.GET;
        }
        LOG.debug( "Using " + preferredRequestType + " for GetRecords request [" + request + "]" );
        return performGetRecordsRequest( request, preferredRequestType );
    }

    final GetRecordsResponse performGetRecordsRequest( final GetRecords request, final GetRecordsRequestType requestType )
                            throws IOException, OWSExceptionReport, XMLStreamException, OWSException {
        GetRecordsResponse response = null;
        switch ( requestType ) {
        case GET:
            response = performGetRecordsRequestWithGet( request );
            break;
        case SOAP:
            response = performGetRecordsRequestWithSoap( request );
            break;
        default:
            response = performGetRecordsRequestWithPost( request );
            break;
        }
        return response;
    }

    private GetRecordsResponse performGetRecordsRequestWithSoap( final GetRecords request )
                            throws XMLStreamException, IOException, OWSExceptionReport {
        final URL endPoint = getEndpointUrlByType( "soap" );
        final StreamBufferStore requestOutputStream = new StreamBufferStore();
        XMLStreamWriter xmlWriter = null;
        try {
            xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( requestOutputStream );
            GetRecordsXMLEncoder.exportAsSoapMessage( request, xmlWriter );
        } catch ( Throwable t ) {
            throw new RuntimeException( "Error creating SOAP message: " + request );
        } finally {
            if ( xmlWriter != null )
                xmlWriter.close();
            requestOutputStream.close();
        }
        OwsHttpResponse response = httpClient.doPost( endPoint, "application/soap+xml", requestOutputStream, null );
        return new GetRecordsResponse( response );
    }

    private GetRecordsResponse performGetRecordsRequestWithGet( final GetRecords request )
                            throws IOException, XMLStreamException, OWSExceptionReport, OWSException {
        final URL endPoint = getGetUrl( "GetRecords" );
        final Map<String, String> params = new HashMap<String, String>();
        params.put( "SERVICE", "CSW" );
        params.put( "VERSION", "2.0.2" );
        params.put( "REQUEST", "GetRecords" );
        params.put( "resultType", request.getResultType().name() );
        params.put( "maxRecords", Integer.toString( request.getMaxRecords() ) );
        params.put( "startPosition", Integer.toString( request.getStartPosition() ) );
        params.put( "typeNames", "gmd:MD_Metadata" );
        params.put( "namespace",
                    "xmlns(csw=http://www.opengis.net/cat/csw/2.0.2),(gmd=http://www.isotc211.org/2005/gmd)" );
        params.put( "constraintlanguage", "filter" );
        if ( request.getConstraint() != null )
            params.put( "constraint", createConstraint( request ) );
        final OwsHttpResponse response = httpClient.doGet( endPoint, params, null );
        return new GetRecordsResponse( response );
    }

    private GetRecordsResponse performGetRecordsRequestWithPost( final GetRecords request )
                            throws IOException, OWSExceptionReport, XMLStreamException {
        final URL endPoint = getEndpointUrlByType( "xml" );

        final StreamBufferStore requestOutputStream = new StreamBufferStore();
        XMLStreamWriter xmlWriter = null;
        try {
            xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( requestOutputStream );
            GetRecordsXMLEncoder.export( request, xmlWriter );
        } catch ( Throwable t ) {
            throw new RuntimeException( "Error creating XML request: " + request );
        } finally {
            if ( xmlWriter != null )
                xmlWriter.close();
            requestOutputStream.close();
        }
        final OwsHttpResponse response = httpClient.doPost( endPoint, "text/xml", requestOutputStream, null );
        return new GetRecordsResponse( response );
    }

    public List<MetadataRecord> getRecordById( List<String> fileIdentifiers ) {
        throw new UnsupportedOperationException( "GetRecordById with multiple fileIdentifiers is not implemented yet!" );
    }

    public MetadataRecord getIsoRecordById( String fileIdentifier )

                            throws IOException, OWSExceptionReport, XMLStreamException {
        return getRecordById( fileIdentifier, "http://www.isotc211.org/2005/gmd" );
    }

    public MetadataRecord getRecordById( String fileIdentifier, String schema )

                            throws IOException, OWSExceptionReport, XMLStreamException {
        URL endPoint = getGetUrl( "GetRecordById" );

        Map<String, String> params = getGetRecordByIdKvpParams( fileIdentifier, schema );

        OwsHttpResponse response = httpClient.doGet( endPoint, params, null );

        XMLStreamReader xmlStream = response.getAsXMLStream();
        XMLStreamUtils.skipStartDocument( xmlStream );
        moveToNextStartElement( xmlStream );
        return MetadataRecordFactory.create( xmlStream );
    }

    private Map<String, String> getGetRecordByIdKvpParams( String fileIdentifier, String schema ) {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "REQUEST", "GetRecordById" );
        params.put( "VERSION", "2.0.2" );
        params.put( "SERVICE", "CSW" );
        params.put( "OUTPUTSCHEMA", schema );

        params.put( "ID", fileIdentifier );
        return params;
    }

    private void moveToNextStartElement( XMLStreamReader xmlStream )
                            throws XMLStreamException {
        xmlStream.next();
        while ( !xmlStream.isStartElement() && xmlStream.getEventType() != XMLStreamReader.END_DOCUMENT ) {
            xmlStream.next();
        }
    }

    public TransactionResponse insert( OMElement record )
                            throws IOException, OWSExceptionReport, XMLStreamException {
        return insert( Collections.singletonList( record ) );
    }

    public TransactionResponse insert( List<OMElement> records )
                            throws IOException, OWSExceptionReport, XMLStreamException {
        ckeckOperationSupported( Transaction.name() );
        URL endPoint = getPostUrl( Transaction.name() );

        StreamBufferStore request = new StreamBufferStore();
        try {
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( request );
            TransactionXMLEncoder.exportInsert( records, xmlWriter );
            xmlWriter.close();
            request.close();
        } catch ( Throwable t ) {
            throw new RuntimeException( "Error inserting " + records.size() + " records" );
        }
        OwsHttpResponse response = httpClient.doPost( endPoint, "text/xml", request, null );
        return new TransactionResponse( response );
    }

    public boolean update( String fileIdentifier, OMElement record ) {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    public boolean delete( String fileIdentifier ) {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    public boolean delete( Filter filter ) {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    public boolean deleteAll() {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    private void ckeckOperationSupported( String operationName )
                            throws UnsupportedOperationException {
        OperationsMetadata om = getOperations();
        if ( om.getOperation( operationName ) == null )
            throw new UnsupportedOperationException( "Operation " + operationName + " is not supported!" );
    }

    /**
     * Cope with <code>OperationMetadata</code> sections that specify separate SOAP and XML endpoints.
     * 
     * <pre>
     *  &lt;ows:Operation name="GetRecords"&gt;
     *    &lt;ows:DCP&gt;
     *      &lt;ows:HTTP&gt;
     *        &lt;ows:Post xlink:href="http://www..."&gt;
     *          &lt;ows:Constraint name="PostEncoding"&gt;
     *          &lt;ows:Value&gt;SOAP&lt;/ows:Value&gt;
     *        &lt;/ows:Constraint&gt;
     *      &lt;/ows:Post&gt;
     *    &lt;/ows:HTTP&gt;
     *  &lt;/ows:DCP&gt;
     *  &lt;ows:DCP&gt;
     *    &lt;ows:HTTP&gt;
     *      &lt;ows:Post xlink:href="http://www..."&gt;
     *        &lt;ows:Constraint name="PostEncoding"&gt;
     *        &lt;ows:Value&gt;XML&lt;/ows:Value&gt;
     *      &lt;/ows:Constraint&gt;
     *    &lt;/ows:HTTP&gt;
     *  &lt;/ows:DCP&gt;
     * </pre>
     * 
     * @return endpoint URL for post requests, never <code>null</code>
     */
    private URL getEndpointUrlByType( String type ) {
        Operation operation = getOperations().getOperation( GetRecords.name() );
        for ( DCP dcp : operation.getDCPs() ) {
            for ( Pair<URL, List<Domain>> pe : dcp.getPostEndpoints() ) {
                for ( Domain d : pe.second ) {
                    if ( "PostEncoding".equals( d.getName() ) ) {
                        PossibleValues pv = d.getPossibleValues();
                        if ( pv instanceof AllowedValues ) {
                            AllowedValues av = (AllowedValues) pv;
                            for ( Values value : av.getValues() ) {
                                if ( value instanceof Value && type.equalsIgnoreCase( ( (Value) value ).getValue() ) ) {
                                    return pe.first;
                                }
                            }
                        }
                    }
                }
            }
        }
        return getPostUrl( GetRecords.name() );
    }

    private String createConstraint( GetRecords getRecords )
                            throws IOException, XMLStreamException, OWSException {
        ByteArrayOutputStream constraintStream = new ByteArrayOutputStream();
        XMLStreamWriter constraintWriter = null;
        try {
            constraintWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( constraintStream );
            GetRecordsXMLEncoder.writeConstraintWithFilter( getRecords, constraintWriter );
        } catch ( FactoryConfigurationError e ) {
            throw new OWSException( e.getMessage(), NO_APPLICABLE_CODE );
        } catch ( UnknownCRSException e ) {
            throw new OWSException( e.getMessage(), NO_APPLICABLE_CODE );
        } catch ( TransformationException e ) {
            throw new OWSException( e.getMessage(), NO_APPLICABLE_CODE );
        } finally {
            if ( constraintWriter != null )
                constraintWriter.close();
            IOUtils.closeQuietly( constraintStream );
        }
        return constraintStream.toString();
    }

}