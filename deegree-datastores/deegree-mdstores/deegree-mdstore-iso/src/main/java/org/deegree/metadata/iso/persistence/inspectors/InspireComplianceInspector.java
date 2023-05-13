//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/deegree3/trunk/deegree-core/deegree-core-metadata/src/main/java/org/deegree/metadata/iso/persistence/inspectors/InspireComplianceInspector.java $
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
package org.deegree.metadata.iso.persistence.inspectors;

import java.sql.Connection;

import org.deegree.metadata.i18n.Messages;
import org.deegree.metadata.iso.ISORecord;
import org.deegree.metadata.persistence.MetadataInspectorException;
import org.deegree.metadata.persistence.inspectors.RecordInspector;
import org.deegree.metadata.persistence.iso19115.jaxb.InspireInspector;
import org.deegree.sqldialect.SQLDialect;

/**
 * {@link RecordInspector} for ensuring INSPIRE compliance.
 * <p>
 * This inspector performs the following checks (currently nothing).
 * </p>
 *
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author: mschneider $
 * @version $Revision: 30651 $, $Date: 2011-04-04 17:43:08 +0200 (Mo, 04. Apr 2011) $
 */
public class InspireComplianceInspector implements RecordInspector<ISORecord> {

	public InspireComplianceInspector(InspireInspector config) {
	}

	@Override
	public ISORecord inspect(ISORecord record, Connection conn, SQLDialect dialect) throws MetadataInspectorException {
		// 2.2.5 Unique resource identifier for dataset and dataset series
		String type = record.getType();
		if ((type == null || "dataset".equals(type) || "series".equals(type))
				&& record.getParsedElement().getQueryableProperties().getResourceIdentifier() == null) {
			throw new MetadataInspectorException(Messages.get("INSPIRE_COMPLIANCE_MISSING_RI", record.getIdentifier()));
		}
		return record;
	}

}
