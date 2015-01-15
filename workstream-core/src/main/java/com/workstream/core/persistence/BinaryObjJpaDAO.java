package com.workstream.core.persistence;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryObjType;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class BinaryObjJpaDAO extends GenericJpaDAO<BinaryObj, Long> implements
		IBinaryObjDAO {

	public static Connection getConnection(EntityManager em) {
		// Hibernate 4 specific code
		Session session = em.unwrap(Session.class);
		SessionImplementor si = (SessionImplementor) session;
		try {
			return si.getJdbcConnectionAccess().obtainConnection();
		} catch (SQLException e) {
			throw new DataPersistException("Failed to obtain connection", e);
		}
	}

	@Override
	public void persistOutputStreamToContent(InputStream is, BinaryObj toObj,
			long size) {
		try {
			// Unable to write to existing Clob, always need to create new one
			// Connection conn = getConnection(getEm());
			// Blob blob = conn.createBlob();
			// http://stackoverflow.com/questions/19908279/hibernate-4-2-2-create-blob-from-unknown-length-input-stream
			LobCreator creator = Hibernate.getLobCreator(getEm().unwrap(
					Session.class));
			Blob blob = creator.createBlob(is, size);

			// int size = IOUtils.copy(is, blob.setBinaryStream(1));
			toObj.setContent(blob);
			this.persist(toObj);
			// blob.length is always -1 :(
			toObj.setSize(size);
		} catch (Exception e) {
			throw new DataPersistException("Unable to write to blob", e);
		}
	}

	@Override
	public BinaryObj getBinaryObjByTarget(BinaryObjType type, String targetId) {
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		attributes.put("targetId", targetId);
		attributes.put("type", type);
		Collection<BinaryObj> binaries = filterFor(attributes, 0, 1);
		Iterator<BinaryObj> ite = binaries.iterator();
		if (ite.hasNext()) {
			return ite.next();
		} else {
			return null;
		}
	}

}
