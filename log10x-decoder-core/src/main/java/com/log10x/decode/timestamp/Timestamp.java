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

	/**
	 * @return {@code true} when a decoder writes the timestamp value back as the
	 *         text it was encoded with instead of formatting its epoch: the
	 *         value of an epoch pattern is the epoch text read from the source,
	 *         fraction and leading zeros included.
	 */
	public boolean writesValueText() {
		return false;
	}

	/**
	 * Returned by {@link #readEpoch} for a value it cannot read a time from.
	 */
	public static final long NO_EPOCH = -1;

	/**
	 * @return {@code true} when the event time of a slot of this pattern is read
	 *         from the value text by {@link #readEpoch} instead of the lexer's
	 *         default reading of an integer epoch.
	 */
	public boolean readsEpochFromText() {
		return false;
	}

	/**
	 * Reads the event time from the value text of a slot of this pattern. Only
	 * called when {@link #readsEpochFromText()} is {@code true}.
	 * 
	 * @param chars the event's characters
	 * @param start index of the value's first character
	 * @param end   index after the value's last character
	 * @return the time in milliseconds or nanoseconds since the epoch, or
	 *         {@link #NO_EPOCH}
	 */
	public long readEpoch(char[] chars, int start, int end) {
		return NO_EPOCH;
	}

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
