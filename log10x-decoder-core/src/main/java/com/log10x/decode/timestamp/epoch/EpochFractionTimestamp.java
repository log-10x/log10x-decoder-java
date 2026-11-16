package com.log10x.decode.timestamp.epoch;

import com.log10x.decode.timestamp.Timestamp;

/**
 *
 * {@link Timestamp} class for an epoch read with a fraction, whose value is the
 * text the encoder read: {@code +%s.%f} (seconds, a dot, 1 to 9 fraction
 * digits, such as {@code 1723042099.699}) and {@code +%s.%N} (milliseconds, a
 * dot, fraction digits of a millisecond, such as {@code 1705313472123.456}).
 * A decoder writes the text back unchanged; the event time is read from it
 * digit by digit, never through the magnitude rule of an integer epoch.
 *
 */
public class EpochFractionTimestamp extends EpochTimestamp {

	/**
	 * Seconds since the epoch, a dot and 1 to 9 fraction digits.
	 */
	public static final String SECONDS_FRACTION = "+%s.%f";

	/**
	 * Milliseconds since the epoch, a dot and fraction digits of a millisecond.
	 */
	public static final String MILLIS_FRACTION = "+%s.%N";

	private static final int SECOND_DIGITS = 10;
	private static final int MAX_FRACTION_DIGITS = 9;
	private static final long MAX_MILLIS = Long.MAX_VALUE / 1_000_000L;

	private static final long[] POW10 = {
		1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L, 1_000_000_000L
	};

	private final boolean seconds;

	/**
	 * Creates a new instance.
	 *
	 * @param pattern {@link #SECONDS_FRACTION} or {@link #MILLIS_FRACTION}
	 */
	public EpochFractionTimestamp(String pattern) {

		super(pattern);

		if (SECONDS_FRACTION.equals(pattern)) {
			this.seconds = true;
		} else if (MILLIS_FRACTION.equals(pattern)) {
			this.seconds = false;
		} else {
			throw new IllegalArgumentException(pattern);
		}
	}

	@Override
	public boolean readsEpochFromText() {
		return true;
	}

	/**
	 * Reads the event time from a value of this pattern.
	 * <ul>
	 * <li>{@code +%s.%f}: the value must be 10 digits, a dot and 1 to 9 digits.
	 * The time is in milliseconds for 1 to 3 fraction digits and in nanoseconds
	 * for 4 to 9 ({@code 1723042099.6} is 1723042099600 ms).</li>
	 * <li>{@code +%s.%N}: milliseconds, then an optional dot and 1 to 9 digits
	 * of a millisecond. The time is in nanoseconds when a fraction is present
	 * (digits past the nanosecond are dropped) and in milliseconds without
	 * one.</li>
	 * </ul>
	 * Both units are told apart by magnitude as for an integer epoch: a time
	 * from 2000 on in nanoseconds is far above any time in milliseconds.
	 *
	 * @return the time, or {@link Timestamp#NO_EPOCH} for a value of any other
	 *         shape (its text is still written back)
	 */
	@Override
	public long readEpoch(char[] chars, int start, int end) {

		int dot = -1;

		for (int i = start; i < end; i++) {

			char c = chars[i];

			if (c == '.') {

				if (dot != -1) {
					return NO_EPOCH;
				}

				dot = i;

			} else if ((c < '0') || (c > '9')) {
				return NO_EPOCH;
			}
		}

		int whole = ((dot == -1) ? end : dot) - start;
		int fractionDigits = (dot == -1) ? 0 : end - dot - 1;

		if ((whole == 0) || ((dot != -1) && (fractionDigits == 0)) || (fractionDigits > MAX_FRACTION_DIGITS)) {
			return NO_EPOCH;
		}

		if (seconds) {

			if ((whole != SECOND_DIGITS) || (dot == -1)) {
				return NO_EPOCH;
			}

			long sec = digits(chars, start, dot);
			long fraction = digits(chars, dot + 1, end);

			if (fractionDigits <= 3) {
				return sec * 1_000L + fraction * POW10[3 - fractionDigits];
			}

			return sec * 1_000_000_000L + fraction * POW10[9 - fractionDigits];
		}

		if (whole > 18) {
			return NO_EPOCH;
		}

		long millis = digits(chars, start, start + whole);

		if (dot == -1) {
			return millis;
		}

		if (millis > MAX_MILLIS) {
			return NO_EPOCH;
		}

		long fraction = digits(chars, dot + 1, end);

		long nanos = (fractionDigits <= 6)
			? fraction * POW10[6 - fractionDigits]
			: fraction / POW10[fractionDigits - 6];

		return millis * 1_000_000L + nanos;
	}

	private static long digits(char[] chars, int start, int end) {

		long result = 0;

		for (int i = start; i < end; i++) {
			result = result * 10 + (chars[i] - '0');
		}

		return result;
	}
}
