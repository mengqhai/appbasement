package com.workstream.core.persistence;

import java.io.InputStream;

import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryObjType;

public interface IBinaryObjDAO extends IGenericDAO<BinaryObj, Long> {

	public abstract void persistOutputStreamToContent(InputStream is,
			BinaryObj toObj, long size);

	public abstract BinaryObj getBinaryObjByTarget(BinaryObjType type,
			String targetId);

}
