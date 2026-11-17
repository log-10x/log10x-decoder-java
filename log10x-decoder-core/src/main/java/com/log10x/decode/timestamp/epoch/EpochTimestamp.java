package com.log10x.decode.timestamp.epoch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import com.log10x.decode.timestamp.Timestamp;

abstract class EpochTimestamp extends Timestamp {

	EpochTimestamp(String pattern) {
		super(pattern);
	}

	/**
	 * An epoch pattern ({@code +%s}, {@code +%s.%N}, {@code epoch},
	 * {@code epochNano}) carries the epoch text the encoder read, which a decoder
	 * writes back unchanged.
	 */
	@Override
	public boolean writesValueText() {
		return true;
	}

	@Override
	public void format(long epoch, Writer writer) {
		try {
			writer.write(Long.toString(epoch));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
