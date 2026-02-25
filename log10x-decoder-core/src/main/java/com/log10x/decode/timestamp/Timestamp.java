package com.log10x.decode.timestamp;

import java.io.Writer;

/**
 * Class for formatting timestamps based on a pre-given date time format.
 * 
 * @author Dor Levi
 *
 */
public abstract class Timestamp {

	private final String pattern;

	protected Timestamp(String pattern) {

		this.pattern = pattern;
	}

	/**
	 * @return The internal date time format pattern
	 */
	public String pattern() {
		return this.pattern;
	}

	/**
	 * Writes a given timestamp in milliseconds since epoch to a {@link Writer}
	 * 
	 * @param epoch  timestamp in milliseconds
	 * @param writer destination {@link Writer}
	 */
	public abstract void format(long epoch, Writer writer);

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Timestamp)) {
			return false;
		}

		Timestamp other = (Timestamp) obj;

		if (!pattern.equals(other.pattern)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return pattern.hashCode();
	}

	@Override
	public String toString() {
		return this.pattern;
	}
}
