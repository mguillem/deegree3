/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2013 by:
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
package org.deegree.console.connection.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.deegree.console.AbstractResourceManagerBean;
import org.deegree.console.Config;
import org.deegree.db.ConnectionProviderManager;
import org.deegree.workspace.ResourceMetadata;

@Named
@ViewScoped
public class SqlConnectionManagerBean extends AbstractResourceManagerBean<ConnectionProviderManager>
		implements Serializable {

	private static final long serialVersionUID = 2946865645336970064L;

	public SqlConnectionManagerBean() {
		super(ConnectionProviderManager.class);
	}

	@Override
	public List<Config> getConfigs() {
		List<Config> configs = new ArrayList<Config>();
		for (ResourceMetadata<?> state : resourceManager.getResourceMetadata()) {
			if (!state.getIdentifier().getId().equals("LOCK_DB")) {
				configs.add(new Config(state, resourceManager, "/console/connection/sql/index", true));
			}
		}
		Collections.sort(configs);
		return configs;
	}

}
