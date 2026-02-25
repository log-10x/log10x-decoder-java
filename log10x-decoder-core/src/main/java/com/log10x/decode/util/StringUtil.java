package com.log10x.decode.util;

/**
 * 
 * @author Dor Levi
 *
 */
public class StringUtil {

	/**
	 * 
	 */
	public static final char MINUS = '-';

	private static final int MAX_PRINT_LEN = 1024;

	/**
	 * Checks if an input {@link String} is {@code null} or empty
	 * 
	 * @param val {@link String} to check
	 * @return {@code true} if {@code val} is {@code null} or empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNullOrEmpty(String val) {
		return ((val == null) || (val.isEmpty()));
	}

	/**
	 * Constructs a new {@link String} object for a range inside a {@code char[]}
	 * 
	 * @param chars input array
	 * @param start start index
	 * @param len   amount of characters
	 * 
	 * @return The resulting {@link String}. If {@code chars} is {@code null},
	 *         returns {@code null}. If {@code start} and {@code len} are both 0,
	 *         returns an empty {@link String}. If {@code start} or {@code length}
	 *         exceed the range of {@code chars}, returns a new {@link String}
	 *         explaining the problem.
	 */
	public static String print(char[] chars, int start, int len) {

		if (chars == null) {
			return null;
		}

		if ((start == 0) && (len == 0)) {
			return "";
		}

		if ((len < 0) || (len > chars.length)) {
			return "len " + len + " invalid for " + chars.length;
		}

		if ((start < 0) || (start >= chars.length)) {
			return "start " + start + " invalid for " + chars.length;
		}

		if (start + len > chars.length) {
			return "start " + start + " + len " + len + " invalid for " + chars.length;
		}

		return new String(chars, start, Math.min(len, MAX_PRINT_LEN));
	}

	/**
	 * Checks if a {@link String} appears in a {@code char[]} at a given position.
	 * 
	 * @param chars  the character array
	 * @param index  position in the array to start checking from
	 * @param target target {@link String}
	 * 
	 * @return {@code true} if the target {@link String} appears at {@code index} of
	 *         {@code chars[]}, otherwise returns {@code false}
	 */
	public static boolean regionMatches(char[] chars, int index, String target) {

		int len = target.length();

		for (int i = 0; i < len; i++) {

			char regionChar = chars[index + i];
			char otherChar = target.charAt(i);

			if (regionChar != otherChar) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if a character is a digit (0-9)
	 * 
	 * @param c {@code char} to check
	 * @return {@code true} if {@code c} is a digit, otherwise returns {@code false}
	 */
	public static boolean isNumeral(char c) {

		return (c >= '0') && (c <= '9');
	}

	/**
	 * Checks if a {@code char[]} contains a representation of a negative number at
	 * a provided position.
	 * 
	 * @param content target {@code char[]}
	 * @param c       last character read from {@code content}
	 * @param index   position in {@code content} to check
	 * @param end     last position of {@code content} with readable chars. Can be
	 *                shorter than the length of {@code content}
	 * @return {@code true} if {@code content} contains a negative number at
	 *         position {@code index}, otherwise returns {@code false}
	 */
	public static boolean isNegativeNum(char[] content, char c, int index, int end) {

		if (c != MINUS) {
			return false;
		}

		if (index >= end - 1) {
			return false;
		}

		char n = content[index + 1];

		if (!isNumeral(n)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns a copy of the internal internal {@code char[]} of the provided
	 * {@link StringBuilder}
	 * 
	 * @param builder input {@link StringBuilder}
	 * 
	 * @return a new {@code char[]} which is a copy of {@code builder}'s value.
	 */
	public static char[] getChars(StringBuilder builder) {

		char[] result = new char[builder.length()];
		builder.getChars(0, builder.length(), result, 0);

		return result;
	}

	/**
	 * Computes a hash code value from a given range of a {@code char[]}
	 * 
	 * @param value input {@code char[]}
	 * @param start start position to hash
	 * @param len   amount to characters to hash
	 * @return resulting hash code value.
	 */
	public static int stringHash(char[] value, int start, int len) {
		return stringHash(value, start, len, 0);
	}

	private static int stringHash(char[] value, int start, int len, int initial) {

		int result = initial;

		for (int i = 0; i < len; i++) {
			result = 31 * result + value[start + i];
		}

		return result;
	}
}
