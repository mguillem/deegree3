//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/deegree3/trunk/deegree-core/deegree-core-metadata/src/main/java/org/deegree/metadata/persistence/MetadataInspectorException.java $
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
package org.deegree.metadata.persistence;

/**
 * Indicates an exception that occured in the metadata inspectation.
 *
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author: sthomas $
 * @version $Revision: 27732 $, $Date: 2010-11-03 10:52:35 +0100 (Mi, 03. Nov 2010) $
 */
public class MetadataInspectorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -3833594286729370830L;

	public MetadataInspectorException() {
		super();
	}

	/**
	 * Creates a new {@link MetadataInspectorException} with detail message.
	 * @param message detail message
	 */
	public MetadataInspectorException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link MetadataInspectorException} which wraps the causing exception.
	 * @param cause
	 */
	public MetadataInspectorException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new {@link MetadataInspectorException} which wraps the causing exception
	 * and provides a detail message.
	 * @param message
	 * @param cause
	 */
	public MetadataInspectorException(String message, Throwable cause) {
		super(message, cause);
	}

}
