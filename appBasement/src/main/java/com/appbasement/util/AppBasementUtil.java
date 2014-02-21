package com.appbasement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;

import com.appbasement.exception.AppBasementRTException;

public class AppBasementUtil {

	public static String readClob(Clob clob) throws AppBasementRTException {
		StringBuffer sb = new StringBuffer();
		try {
			Reader reader = clob.getCharacterStream();
			BufferedReader br = new BufferedReader(reader);
			@SuppressWarnings("unused")
			String line;
			while (null != (line = br.readLine())) {
				br.close();
			}
		} catch (SQLException | IOException e) {
			throw new AppBasementRTException("Failed to read String from clob",
					e);
		}
		return sb.toString();
	}

	public static Clob createWriteClob(String str, Connection conn)
			throws AppBasementRTException {
		try {
			Clob clob = conn.createClob();
			updateClob(str, clob);
			return clob;
		} catch (SQLException e) {
			throw new AppBasementRTException("Failed to write to Clob", e);
		}
	}

	public static void updateClob(String str, Clob clob) {
		try {
			clob.setString(1, str);
		} catch (SQLException e) {
			throw new AppBasementRTException("Failed to update Clob", e);
		}
	}

}
