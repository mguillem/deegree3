/*-
 * #%L
 * deegree-cli-utility
 * %%
 * Copyright (C) 2016 - 2021 lat/lon GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.tools.featurestoresql.loader;

import org.deegree.feature.persistence.FeatureStoreException;
import org.deegree.feature.persistence.sql.SQLFeatureStore;
import org.deegree.feature.persistence.sql.SQLFeatureStoreTransaction;
import org.slf4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.util.Assert;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.batch.core.ExitStatus.FAILED;

/**
 * Acquires a transaction before step and commit or rollback the transaction after.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class TransactionHandler extends ReferenceCheckListener implements StepExecutionListener {

	private static final Logger LOG = getLogger(TransactionHandler.class);

	private SQLFeatureStore sqlFeatureStore;

	private SQLFeatureStoreTransaction featureStoreTransaction;

	/**
	 * @param sqlFeatureStore used for transactions, never <code>null</code>
	 * @param summary writing the report, never <code>null</code>
	 */
	public TransactionHandler(SQLFeatureStore sqlFeatureStore, Summary summary) {
		super(summary);
		Assert.notNull(sqlFeatureStore, "sqlFeatureStore  must not be null");
		this.sqlFeatureStore = sqlFeatureStore;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		try {
			this.featureStoreTransaction = (SQLFeatureStoreTransaction) this.sqlFeatureStore.acquireTransaction();
			LOG.info("Acquired transaction.");
		}
		catch (Exception e) {
			LOG.error("Transaction could not acquired!", e);
			throw new RuntimeException("Transaction could not acquired!", e);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (featureStoreTransaction != null)
			return checkReferencesAndHandleResult(stepExecution);
		return FAILED;
	}

	@Override
	ExitStatus handleValidReferences(StepExecution stepExecution) {
		return commitOrRollback(stepExecution, featureStoreTransaction);
	}

	@Override
	void handleInvalidReferences() {
		rollback();
	}

	private ExitStatus commitOrRollback(StepExecution stepExecution, SQLFeatureStoreTransaction transaction) {
		try {
			ExitStatus exitStatus = stepExecution.getExitStatus();
			if (COMPLETED.equals(exitStatus)) {
				LOG.info("Commit transaction.");
				transaction.commit();
			}
			else {
				LOG.info("Rollback transaction.");
				transaction.rollback();
			}
			return exitStatus;
		}
		catch (FeatureStoreException e) {
			summary.setCommitFailed(e.getMessage());
			LOG.error("Could not commit/rollback the transaction.", e);
			return FAILED;
		}
	}

	private void rollback() {
		try {
			LOG.info("Rollback transaction.");
			featureStoreTransaction.rollback();
		}
		catch (FeatureStoreException e) {
			LOG.error("Could not rollback the transaction.", e);
		}
	}

}
