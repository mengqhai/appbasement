package com.appbasement.persistence.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.hibernate.jdbc.Work;

public class DBUnitAssertionWork implements Work {

	protected IDataSet expectedDataSet;

	protected String tableName;

	protected String whereCondition;

	protected String[] ignoreCols;

	protected Map<String, Object> replaceTokenMap = new HashMap<String, Object>();

	public DBUnitAssertionWork(IDataSet expectedDataSet, String tableName,
			String whereCondition, String... ignoreCols) {
		super();
		this.expectedDataSet = expectedDataSet;
		this.tableName = tableName;
		this.whereCondition = whereCondition;
		this.ignoreCols = ignoreCols;
	}

	public DBUnitAssertionWork(IDataSet expectedDataSet, String tableName,
			String whereCondition) {
		super();
		this.expectedDataSet = expectedDataSet;
		this.tableName = tableName;
		this.whereCondition = whereCondition;
		this.ignoreCols = new String[] {};
	}

	public DBUnitAssertionWork(String dataSetPath, String tableName,
			String whereCondition, String... ignoreCols) {
		this(DBUnitHelper.getFlatXmlDataSet(dataSetPath), tableName,
				whereCondition, ignoreCols);
	}

	public DBUnitAssertionWork(Class<?> clazz, String shortName,
			String tableName, String whereCondition, String... ignoreCols) {
		this(DBUnitHelper.getFlatXmlDataSet(clazz, shortName), tableName,
				whereCondition, ignoreCols);
	}

	public DBUnitAssertionWork(String dataSetPath, String tableName,
			String whereCondition) {
		this(DBUnitHelper.getFlatXmlDataSet(dataSetPath), tableName,
				whereCondition);
	}

	public DBUnitAssertionWork(Class<?> clazz, String shortName,
			String tableName, String whereCondition) {
		this(DBUnitHelper.getFlatXmlDataSet(clazz, shortName), tableName,
				whereCondition);
	}

	public DBUnitAssertionWork(Class<?> clazz, String shortName,
			String tableName) {
		this(DBUnitHelper.getFlatXmlDataSet(clazz, shortName), tableName, null);
	}

	@Override
	public void execute(Connection connection) throws SQLException {
		try {
			DatabaseConnection dbUnitConn = new HsqldbConnection(connection,
					null);
			String whereClause = (this.whereCondition == null) ? "" : " where "
					+ this.whereCondition;
			String sql = "select * from " + tableName + whereClause;
			IDataSet expected = this.expectedDataSet;
			if (!this.replaceTokenMap.isEmpty()) {
				ReplacementDataSet replacement = new ReplacementDataSet(
						this.expectedDataSet);
				for (Map.Entry<String, Object> entry : this.replaceTokenMap
						.entrySet()) {
					replacement.addReplacementObject(entry.getKey(),
							entry.getValue());
				}
				expected = replacement;
			}

			Assertion.assertEqualsByQuery(expected, dbUnitConn, sql, tableName,
					ignoreCols);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Map<String, Object> getReplaceTokenMap() {
		return replaceTokenMap;
	}

	public DBUnitAssertionWork replace(String token, Object value) {
		this.getReplaceTokenMap().put(token, value);
		return this;
	}

	public DBUnitAssertionWork replaceId(Long id) {
		return replace("[ID]", id);
	}

	public DBUnitAssertionWork replaceCreatedAt(Date time) {
		return replace("[CREATED_AT]", time);
	}

	public DBUnitAssertionWork replaceLastUpdate(Date time) {
		return replace("[LAST_UPDATE]", time);
	}
	
	public DBUnitAssertionWork replaceNull() {
		return replace("[NULL]", null);
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public DBUnitAssertionWork setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
		return this;
	}

}
