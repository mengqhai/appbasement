package com.appbasement.persistence;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
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
			IDataSet setupDataSet = getDataSet(dataSetPath);
			ReplacementDataSet replacement = new ReplacementDataSet(
					setupDataSet);
			// always replace the [NULL]
			replacement.addReplacementObject("[NULL]", null);
			dbOperation.execute(dbUnitConn, replacement);
		} catch (Exception e) {
			throw new RuntimeException("Unable to clean insert " + dataSetPath,
					e);
		}
	}

	protected IDataSet getDataSet(String name) throws Exception {
		InputStream iStream = getClass().getResourceAsStream(name);
		assertNotNull("Unable to find file:" + name, iStream);
		Reader reader = new InputStreamReader(iStream);
		FlatXmlDataSet dataSet = new FlatXmlDataSet(reader);
		return dataSet;
	}

}