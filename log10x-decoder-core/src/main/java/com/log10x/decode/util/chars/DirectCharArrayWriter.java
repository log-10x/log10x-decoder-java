package com.log10x.decode.util.chars;

import java.io.CharArrayWriter;
import java.util.Arrays;

/**
 * 
 * Utility class used for exposing {@link CharArrayWriter}'s internal buffer for
 * quick read/write access.
 * 
 * @author Dor Levi
 *
 */
public class DirectCharArrayWriter extends CharArrayWriter {

	/**
	 * Creates a new DirectCharArrayWriter with the specified initial size.
	 *
	 * @param initialSize an int specifying the initial buffer size.
	 */
	public DirectCharArrayWriter(int initialSize) {
		super(initialSize);
	}

	/**
	 * Ensures the internal {@code char[]} buffer is of sufficient size, allocating
	 * a new buffer if needed.
	 * 
	 * @param newcount minimal size of internal buffer to validate.
	 */
	public void ensureCapacity(int newcount) {

		synchronized (lock) {
			if (newcount > buf.length) {
				buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
			}
		}
	}

	/**
	 * Writes the characters of a provided {@link StringBuilder} to the internal
	 * {@code char[]} buffer.
	 * 
	 * @param s {@link StringBuilder} to write
	 */
	public void write(StringBuilder s) {

		int currSize = this.size();

		ensureCapacity(currSize + s.length());

		s.getChars(0, s.length(), buf(), currSize);

		this.count += s.length();
	}

	/**
	 * @return the internal {@code char[]} buffer
	 */
	public char[] buf() {
		return this.buf;
	}
}
