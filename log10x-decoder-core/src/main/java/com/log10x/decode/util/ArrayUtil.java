package com.log10x.decode.util;

/**
 * 
 * @author Dor Levi
 *
 */
public class ArrayUtil {

	/**
	 * Checks if two ranges of two {@code char[]} are equal.
	 * 
	 * @param a          first array
	 * @param aFromIndex start index of first array (inclusive)
	 * @param aToIndex   end index of first array (exclusive)
	 * @param b          second array
	 * @param bFromIndex start index of second array (inclusive)
	 * @param bToIndex   end index of second array (exclusive)
	 * 
	 * @return {@code true} if the ranges are the same size and equal, {@code false}
	 *         otherwise.
	 */
	public static boolean equals(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {

		rangeCheck(a.length, aFromIndex, aToIndex);
		rangeCheck(b.length, bFromIndex, bToIndex);

		int aLength = aToIndex - aFromIndex;
		int bLength = bToIndex - bFromIndex;

		if (aLength != bLength) {
			return false;
		}

		for (int i = 0; i < aLength; i++) {
			if (a[aFromIndex + i] != b[bFromIndex + i]) {
				return false;
			}
		}

		return true;

	}

	private static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
		if (fromIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		}
		if (toIndex > arrayLength) {
			throw new ArrayIndexOutOfBoundsException(toIndex);
		}
	}
}
