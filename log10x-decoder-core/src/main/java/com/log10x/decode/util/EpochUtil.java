package com.log10x.decode.util;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Dor Levi
 *
 */
public class EpochUtil {

	private static final long JAN_FIRST_2100 = 4102444800000L;

	private static boolean isNanoMilli(long epoch) {
		return epoch > JAN_FIRST_2100;
	}

	/**
	 * Returns an {@link Instant} from a {@code long} epoch. {@code epoch} can be
	 * either in milliseconds or nanoseconds.
	 * 
	 * @param epoch milliseconds/nanoseconds since epoch (1/1/1970)
	 * 
	 * @return an {@link Instant} matching the input {@code epoch}
	 */
	public static Instant fromEpoch(long epoch) {

		if (!isNanoMilli(epoch)) {
			return Instant.ofEpochMilli(epoch);
		}

		long sec = TimeUnit.NANOSECONDS.toSeconds(epoch);
		long secNanos = TimeUnit.SECONDS.toNanos(sec);

		long nanoAdjust = epoch - secNanos;
		Instant result = Instant.ofEpochSecond(sec, nanoAdjust);

		return result;
	}
}
