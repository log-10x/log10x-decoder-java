package com.log10x.decode.timestamp.epoch;

import com.log10x.decode.timestamp.Timestamp;

/**
 * 
 * {@link Timestamp} class for describing an epoch based timestamp in
 * nanoseconds.
 * 
 * @author Dor Levi
 *
 */
public class EpochNanoTimestamp extends EpochTimestamp {

	/**
	 * Singleton instance.
	 */
	public static final Timestamp Instance = new EpochNanoTimestamp();

	/**
	 * Unique identification string.
	 */
	public static final String EPOCH_NANO = "epochNano";

	private EpochNanoTimestamp() {
		super(EPOCH_NANO);
	}
}
