package com.log10x.decode.timestamp.epoch;

import com.log10x.decode.timestamp.Timestamp;

/**
 * 
 * {@link Timestamp} class for describing an epoch based timestamp in
 * milliseconds.
 * 
 * @author Dor Levi
 *
 */
public class EpochMilliTimestamp extends EpochTimestamp {

	/**
	 * Singleton instance.
	 */
	public static final Timestamp Instance = new EpochMilliTimestamp();

	/**
	 * Unique identification string.
	 */
	public static final String EPOCH = "epoch";

	private EpochMilliTimestamp() {
		super(EPOCH);
	}

	public EpochMilliTimestamp(String pattern) {
		super(pattern);
	}
}
