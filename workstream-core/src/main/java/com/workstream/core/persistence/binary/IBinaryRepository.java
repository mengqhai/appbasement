package com.workstream.core.persistence.binary;

import java.io.InputStream;
import java.io.OutputStream;

import com.workstream.core.model.BinaryObj;

public interface IBinaryRepository {

	public abstract void readBinaryContent(OutputStream os, BinaryObj bo);

	public abstract void writeBinaryObjectContent(InputStream is, BinaryObj bo);

	public abstract InputStream getBinaryContent(BinaryObj bo);

	public abstract void deleteBinaryContent(BinaryObj bo);

//	/**
//	 * Useful when need to work with other APIs that requires an OutputStream to
//	 * write to.
//	 * 
//	 * @param bo
//	 * @return
//	 */
//	public abstract OutputStream getOutputStreamToWrite(BinaryObj bo);

}
