package com.log10x.decode.util;

/**
 * 
 * @author Dor Levi
 *
 */
public class DigitUtil {

	/**
	 * @param c input character
	 * @return the value of the digit represented by this character
	 */
	public static int digitValue(char c) {
		return c - '0';
	}

	/**
	 * Parses an integer from a provided {@link CharSequence}
	 * 
	 * @param arr          input to parse
	 * @param defaultValue default value to return in case of errors, if {@code arr}
	 *                     contains any non-digit characters (except a leading minus
	 *                     sign, which is allowed)
	 * @return the int value represented by the given {@link CharSequence}
	 */
	public static int parseInt(final CharSequence arr, int defaultValue) {
		return parseInt(arr, 0, arr.length(), defaultValue);
	}

	private static int parseInt(final CharSequence arr, int start, int length, int defaultValue) {

		int result = 0;
		int sign = 1;

		for (int i = 0; i < length; i++) {

			char c = arr.charAt(start + i);

			if (!Character.isDigit(c)) {

				if (i == 0) {

					if (c == '-') {
						sign = -1;
						continue;
					} else {
						return defaultValue;
					}

				} else {
					return defaultValue;
				}
			}

			int digit = digitValue(c);

			result *= 10;
			result += digit;
		}

		return result * sign;
	}
}
