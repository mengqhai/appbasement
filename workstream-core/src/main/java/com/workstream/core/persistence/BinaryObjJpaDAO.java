package com.workstream.core.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryObjType;
import com.workstream.core.persistence.binary.BinaryRepositoryManager;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class BinaryObjJpaDAO extends GenericJpaDAO<BinaryObj, Long> implements
		IBinaryObjDAO {

	@Autowired
	protected BinaryRepositoryManager rMgr;

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
	public void persistInputStreamToContent(InputStream is, BinaryObj toObj) {
		try {
			// if (is instanceof FileInputStream) {
			// is = new BufferedInputStream((FileInputStream) is);
			// is.mark(10000);
			// }

			// Unable to write to existing Blob, always need to create new one
			// Connection conn = getConnection(getEm());
			// Blob blob = conn.createBlob();
			// http://stackoverflow.com/questions/19908279/hibernate-4-2-2-create-blob-from-unknown-length-input-stream

			this.persist(toObj);
			rMgr.getRepository(toObj.getReposType()).writeBinaryObjectContent(
					is, toObj);
		} catch (Exception e) {
			throw new DataPersistException("Unable to write binary", e);
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

	@Override
	public void deleteBinaryObjByTarget(BinaryObjType type, String targetId) {
		BinaryObj bo = getBinaryObjByTarget(type, targetId);
		if (bo != null) {
			deleteBinaryObj(bo);
			remove(bo);
		}
	}

	@Override
	public void deleteBinaryObj(BinaryObj binary) {
		rMgr.getRepository(binary.getReposType()).deleteBinaryContent(binary);
	}

	@Override
	public long outputContent(OutputStream os, BinaryObj bo) {
		rMgr.getRepository(bo.getReposType()).readBinaryContent(os, bo);
		return bo.getSize();
	}

	@Override
	public InputStream getContentStream(BinaryObj bo) {
		return rMgr.getRepository(bo.getReposType()).getBinaryContent(bo);
	}

}
