package com.log10x.decode.util.chars;

/**
 * Class representing a sub sequence of a character array
 * 
 * @author Dor Levi
 *
 */
public class CharArraySubSequence extends CharArraySequence {

	private int start;
	private int length;

	/**
	 * Constructs a new {@link CharArraySubSequence}
	 * 
	 * @param value  internal {@code char[]}
	 * @param start  start position in {@code value} for this subsequence
	 * @param length length of this subsequence
	 */
	public CharArraySubSequence(char[] value, int start, int length) {
		super(value, start, length);
	}

	@Override
	public void reset(char[] value, int start, int length) {

		super.reset(value, start, length);
		this.start = start;
		this.length = length;
	}

	@Override
	public int length() {
		return this.length;
	}

	@Override
	public int start() {
		return this.start;
	}
}