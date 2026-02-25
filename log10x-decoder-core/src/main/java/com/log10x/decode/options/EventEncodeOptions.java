package com.log10x.decode.options;

import com.log10x.decode.util.ToString;

/**
 * 
 * Options used for defining overall structure of encoded events.
 * 
 * For proper decoding, those options need to match the ones used for encoding.
 * events.
 * 
 * @author Dor Levi
 *
 */
public class EventEncodeOptions {

	private static final char VALUE_SEPERATOR = ' ';

	private final char encodedLinePrefix;
	private final char encodeDelimiter;

	/**
	 * Creates an instance with default encoded line prefix (none) and value
	 * separator (space).
	 */
	public EventEncodeOptions() {
		this(VALUE_SEPERATOR);
	}

	/**
	 * Creates an instance with default encoded line prefix (none) and custom value
	 * separator.
	 * 
	 * @param encodeDelimiter Delimiter to use between encoded values.
	 */
	public EventEncodeOptions(char encodeDelimiter) {
		this((char) 0, encodeDelimiter);
	}

	/**
	 * Creates an instance with custom encoded line prefix and value separator.
	 * 
	 * @param encodedLinePrefix Prefix to append to each encoded line.
	 * @param encodeDelimiter   Delimiter to use between encoded values.
	 */
	public EventEncodeOptions(char encodedLinePrefix, char encodeDelimiter) {

		this.encodedLinePrefix = encodedLinePrefix;
		this.encodeDelimiter = encodeDelimiter;
	}

	/**
	 * @return Encoded line prefix defined in these options.
	 */
	public char encodedLinePrefix() {
		return this.encodedLinePrefix;
	}

	/**
	 * @return Encoded value separator defined in these options.
	 */
	public char encodeDelimiter() {
		return this.encodeDelimiter;
	}

	@Override
	public String toString() {

		return ToString.format(
			"encodedLinePrefix", encodedLinePrefix,
			"encodeDelimiter", encodeDelimiter
		);
	}
}
