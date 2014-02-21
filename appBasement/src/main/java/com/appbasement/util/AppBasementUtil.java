package com.appbasement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;

import com.appbasement.exception.AppBasementRTException;

public class AppBasementUtil {

	public static String readClob(Clob clob) throws AppBasementRTException {
		StringBuffer sb = new StringBuffer();
		try {
			Reader reader = clob.getCharacterStream();
			BufferedReader br = new BufferedReader(reader);
			String line;
			while (null != (line = br.readLine())) {
				sb.append(line);
			}
			br.close();
		} catch (SQLException | IOException e) {
			throw new AppBasementRTException("Failed to read String from clob",
					e);
		}
		return sb.toString();
	}

	public static Clob createWriteClob(String str, Connection conn)
			throws AppBasementRTException {
		try {
			// if using c3p0, make sure the NewProxyConnection implements
			// createClob()
			// earlier versions of c3p0 doesn't have this method
			Clob clob = conn.createClob();
			clob.setString(1, str);
			return clob;
		} catch (SQLException e) {
			throw new AppBasementRTException("Failed to write to Clob", e);
		}
	}

	public static Connection getConnection(EntityManager em) {
		// Hibernate 4 specific code
		Session session = em.unwrap(Session.class);
		SessionImplementor si = (SessionImplementor) session;
		try {
			return si.getJdbcConnectionAccess().obtainConnection();
		} catch (SQLException e) {
			throw new AppBasementRTException("Failed to obtain connection", e);
		}
	}

}
