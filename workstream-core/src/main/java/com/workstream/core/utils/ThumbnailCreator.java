package com.workstream.core.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;

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

	/**
	 * Get the middle square region of the original image. Only write to png
	 * format.
	 * 
	 * @param from
	 */
	public InputStream createSquareThumbnail(InputStream from) {
		try {

			BufferedImage image = ImageIO.read(from);
			int heigth = image.getHeight();
			int width = image.getWidth();
			int length = Math.min(heigth, width);
			int x = 0, y = 0, h = length, w = length;
			if (heigth > width) {
				y = (heigth - length) / 2;
			} else {
				x = (width - length) / 2;
			}
			int resultLength = Math.max(maxWidth, maxHeight);

			ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
			Thumbnails.of(image.getSubimage(x, y, w, h))
					.size(resultLength, resultLength).outputFormat("png")
					.toOutputStream(os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (Exception e) {
			throw new DataPersistException("Failed to create thumbnail", e);
		}
	}

}
