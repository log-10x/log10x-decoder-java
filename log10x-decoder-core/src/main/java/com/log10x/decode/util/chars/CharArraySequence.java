package com.log10x.decode.util.chars;

import com.log10x.decode.util.ArrayUtil;
import com.log10x.decode.util.StringUtil;

/**
 * A {@link CharSequence} implementation over an internal {@code char[]}
 * 
 * @author Dor Levi
 *
 */
public class CharArraySequence implements CharSequence, Cloneable {

	protected char[] value;
	private int hashcode;

	/**
	 * Creates a new {@link CharArraySequence} from provided {@code char[]}
	 * 
	 * @param value the {@code char[]} to wrap
	 */
	public CharArraySequence(char[] value) {
		reset(value, 0, value.length);
	}

	/**
	 * Creates a new {@link CharArraySequence} from provided {@link String}
	 * 
	 * @param value the {@link String} to wrap
	 */
	public CharArraySequence(String value) {
		reset(value);
	}

	protected CharArraySequence(char[] value, int start, int length) {
		reset(value, start, length);
	}

	/**
	 * Returns the start position in the internal {@code char[]}
	 * 
	 * Base implementation is to return 0, but sub-classes can change that.
	 * 
	 * @return index of first {@code char} available
	 */
	public int start() {
		return 0;
	}

	@Override
	public int length() {
		return (this.array() != null) ? this.array().length : 0;
	}

	private void reset(String s) {
		reset(s.toCharArray(), 0, s.length());
	}

	/**
	 * @param start
	 * @param length
	 */
	protected void reset(char[] value, int start, int length) {

		this.value = value;
		this.hashcode = 0;
	}

	/**
	 * Returns the internal {@code char[]}
	 * 
	 * @return the internal {@code char[]}
	 */
	public char[] array() {
		return this.value;
	}

	@Override
	public char charAt(int index) {

		return this.array()[this.start() + index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {

		return new CharArraySubSequence(this.array(), this.start() + start, (end - start));
	}

	private boolean equals(CharSequence other) {

		if (this.length() != other.length()) {
			return false;
		}

		int length = this.length();
		int start = this.start();

		for (int i = 0; i < length; i++) {

			if (this.array()[start + i] != other.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return contentEquals(obj);
	}

	/**
	 * Checks if the content of this {@link CharArraySequence} is equal to the
	 * content of another provided {@link Object}
	 * 
	 * Only compares to the content of {@link CharArraySequence}, {@link String},
	 * {@link CharSequence} or a {@code char[]} All other types always return
	 * {@code false}
	 * 
	 * @param obj object to compare to
	 * @return {@code true} if the content of {@code this} is equal to the content
	 *         of {@code obj}, otherwise returns {@code false}
	 */
	public boolean contentEquals(Object obj) {

		if (obj instanceof CharArraySequence) {

			CharArraySequence other = (CharArraySequence) obj;

			return ArrayUtil.equals(this.array(), this.start(), this.start() + this.length(), other.array(),
					other.start(), other.start() + other.length());
		}

		if (obj instanceof char[]) {

			char[] array = (char[]) obj;

			int len = this.length();

			if ((len < array.length) || (len > array.length)) {

				return false;
			}

			int start = this.start();

			return (ArrayUtil.equals(this.array(), start, start + len, array, 0, array.length));
		}

		if ((obj instanceof String) && ((String) obj).contentEquals(this)) {

			return true;
		}

		if (obj instanceof CharSequence) {
			return equals((CharSequence) obj);
		}

		return false;
	}

	@Override
	public int hashCode() {

		if ((this.hashcode == 0) && (this.length() > 0)) {
			this.hashcode = StringUtil.stringHash(this.array(), start(), length());
		}

		return this.hashcode;
	}

	@Override
	public CharArraySequence clone() {

		int l = this.length();

		char[] a = new char[l];
		System.arraycopy(this.array(), this.start(), a, 0, l);

		return new CharArraySequence(a);
	}

	/**
	 * @return a new {@link String} from the contents of this.
	 */
	public String asString() {
		return new String(this.array(), start(), length());
	}

	@Override
	public String toString() {
		return StringUtil.print(this.array(), start(), length());
	}
}