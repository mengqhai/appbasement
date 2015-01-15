package com.workstream.core.persistence.binary;

import java.io.InputStream;
import java.io.OutputStream;

import com.workstream.core.model.BinaryObj;

public interface IBinaryRepository {

	public abstract void readBinaryContent(OutputStream os, BinaryObj bo);

	public abstract void writeBinaryObjectContent(InputStream is, BinaryObj bo);

}
