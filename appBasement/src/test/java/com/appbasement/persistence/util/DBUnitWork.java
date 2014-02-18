package com.appbasement.persistence.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.jdbc.Work;

public class DBUnitWork implements Work {

	private String dataSetPath;

	private DatabaseOperation dbOperation;

	public DBUnitWork(DatabaseOperation dbOperation, String dataSetPath) {
		this.dbOperation = dbOperation;
		this.dataSetPath = dataSetPath;
	}

	@Override
	public void execute(Connection connection) throws SQLException {
		try {
			HsqldbConnection dbUnitConn = new HsqldbConnection(connection, null);
			IDataSet setupDataSet = new FilteredDataSet(
					new DatabaseSequenceFilter(dbUnitConn),
					DBUnitHelper.getFlatXmlDataSet(dataSetPath));
			dbOperation.execute(dbUnitConn, setupDataSet);
		} catch (Exception e) {
			throw new RuntimeException("Unable perform operation "
					+ this.dbOperation + " on " + dataSetPath, e);
		}
	}

}