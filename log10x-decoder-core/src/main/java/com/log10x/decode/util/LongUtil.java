package com.log10x.decode.util;

/**
 * 
 * @author Dor Levi
 *
 */
public class LongUtil {

	/**
	 * Parses a {@code long} from a provided {@code char[]}
	 * 
	 * @param value        the {@code char[]} to read the characters representing
	 *                     the {@code long} value
	 * @param start        index of first {@code char} to read (inclusive)
	 * @param end          index of last {@code char} to read (exclusive)
	 * @param defaultValue value to return in case of unexpected characters.
	 * 
	 * @return the {@code long} value represented by the characters, or
	 *         {@code defaultValue} if any character isn't a digit.
	 */
	public static long parse(char[] value, int start, int end, long defaultValue) {

		long result = 0;

		for (int i = start; i < end; i++) {

			char c = value[i];

			if (!Character.isDigit(c)) {
				return defaultValue;
			}

			result = result * 10 + DigitUtil.digitValue(c);
		}

		return result;
	}
}
