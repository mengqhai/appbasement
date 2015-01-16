package com.workstream.core.persistence.binary;

import java.io.IOException;
import java.io.InputStream;

import org.activiti.engine.identity.Picture;
import org.apache.commons.io.IOUtils;

import com.workstream.core.exception.DataPersistException;

public class BinaryPicture extends Picture {

	Picture picture;
	InputStream binaryContent;

	public BinaryPicture() {
		super(null, null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8357987169502750990L;

	public BinaryPicture(Picture pic, InputStream binaryContent) {
		this();
		picture = pic;
		this.binaryContent = binaryContent;
	}

	public byte[] getBytes() {
		try {
			if (this.bytes == null) {
				this.bytes = IOUtils.toByteArray(binaryContent);
			}
			return bytes;
		} catch (IOException e) {
			throw new DataPersistException("Failed to read binary stream", e);
		}
	}

	public InputStream getInputStream() {
		return binaryContent;
	}

	public String getMimeType() {
		return picture.getMimeType();
	}

}
