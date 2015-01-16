package com.workstream.core.persistence;

import java.io.InputStream;
import java.io.OutputStream;

import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryObjType;

public interface IBinaryObjDAO extends IGenericDAO<BinaryObj, Long> {

	public abstract void persistInputStreamToContent(InputStream is,
			BinaryObj toObj);

	public abstract BinaryObj getBinaryObjByTarget(BinaryObjType type,
			String targetId);

	public abstract long outputContent(OutputStream os, BinaryObj bo);

	public abstract InputStream getContentStream(BinaryObj bo);

	public abstract void deleteBinaryObj(BinaryObj binary);

	public abstract void deleteBinaryObjByTarget(BinaryObjType type, String targetId);

}
