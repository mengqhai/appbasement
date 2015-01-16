package com.workstream.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.coobird.thumbnailator.Thumbnailator;

import com.workstream.core.exception.DataPersistException;

public class ThumbnailCreator {

	private int maxHeight = 200;
	private int maxWidth = 200;

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public InputStream createThumbnail(InputStream from) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
			Thumbnailator.createThumbnail(from, os, maxWidth, maxHeight);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			throw new DataPersistException("Failed to create thumbnail", e);
		}
	}

}
