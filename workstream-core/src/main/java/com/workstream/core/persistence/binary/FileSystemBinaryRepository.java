package com.workstream.core.persistence.binary;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.workstream.core.exception.DataPersistException;
import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryReposType;

public class FileSystemBinaryRepository implements IBinaryRepository {

	protected String repositoryRootPath;

	protected File root;

	/**
	 * How many files a dir can hold
	 */
	protected int dirSize = 100;

	public String getRepositoryRootPath() {
		return repositoryRootPath;
	}

	public void setRepositoryRootPath(String repositoryRootPath) {
		this.repositoryRootPath = repositoryRootPath;
		root = new File(this.repositoryRootPath);
	}

	protected File initDir() {
		if (!root.exists() || !root.isDirectory()) {
			root.mkdirs();
		}
		final int groupCount = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File sub) {
				return sub.isDirectory();
			}
		}).length;
		String groupName = null;
		if (groupCount > 0) {
			groupName = String.valueOf(groupCount - 1);
		} else {
			groupName = "0";
		}
		File lastGroup = new File(root, groupName);
		if (!lastGroup.exists() || !lastGroup.isDirectory()) {
			lastGroup.mkdir();
		} else if (lastGroup.list().length >= dirSize) {
			groupName = String.valueOf(groupCount);
			lastGroup = new File(root, groupName);
			lastGroup.mkdir();
		}
		return lastGroup;
	}

	@Override
	public void writeBinaryObjectContent(InputStream is, BinaryObj bo) {
		File dir = initDir();
		File biFile = new File(dir, String.valueOf(bo.getId()));
		try {
			FileOutputStream os = FileUtils.openOutputStream(biFile);
			int size = IOUtils.copy(is, os);
			is.close();
			os.close();
			bo.setSize(size);
			bo.setReposType(BinaryReposType.FILE_SYSTEM_REPOSITORY);
			bo.setRepositoryKey(dir.getName() + "/" + biFile.getName());
		} catch (IOException e) {
			throw new DataPersistException("Failed to write to file");
		}
	}

	// @Override
	// public OutputStream getOutputStreamToWrite(BinaryObj bo) {
	// File dir = initDir();
	// File biFile = new File(dir, String.valueOf(bo.getId()));
	// try {
	// FileOutputStream os = FileUtils.openOutputStream(biFile);
	// return os;
	// } catch (IOException e) {
	// throw new DataPersistException("Failed to open file");
	// }
	// }

	/**
	 * Read the binary to the OutputStream.
	 * 
	 * @param os
	 * @param bo
	 */
	@Override
	public void readBinaryContent(OutputStream os, BinaryObj bo) {
		try {
			InputStream is = this.getBinaryContent(bo);
			IOUtils.copy(is, os);
			is.close();
			os.close();
		} catch (IOException e) {
			throw new DataPersistException("Failed to read to file");
		}
	}

	@Override
	public InputStream getBinaryContent(BinaryObj bo) {
		String path = bo.getRepositoryKey();
		File biFile = new File(root, path);
		try {
			FileInputStream is = FileUtils.openInputStream(biFile);
			return is;
		} catch (IOException e) {
			throw new DataPersistException("Failed to read to file: "
					+ biFile.getPath());
		}
	}

	@Override
	public void deleteBinaryContent(BinaryObj bo) {
		String path = bo.getRepositoryKey();
		File biFile = new File(root, path);
		if (biFile.exists() && !biFile.isDirectory()) {
			biFile.delete();
		}
	}

	public int getDirSize() {
		return dirSize;
	}

	public void setDirSize(int dirSize) {
		this.dirSize = dirSize;
	}

}
