package com.workstream.core.persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResetableInputStream extends InputStream {

	protected FileInputStream in;

	public ResetableInputStream(FileInputStream in) {
		this.in = in;
	}

	public int hashCode() {
		return in.hashCode();
	}

	public boolean equals(Object obj) {
		return in.equals(obj);
	}

	public int read() throws IOException {
		return in.read();
	}

	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public String toString() {
		return in.toString();
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public int available() throws IOException {
		return in.available();
	}

	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	public void close() throws IOException {
		in.close();
	}

	public void reset() throws IOException {
		// in.reset();
	}

	public boolean markSupported() {
		return in.markSupported();
	}

}
