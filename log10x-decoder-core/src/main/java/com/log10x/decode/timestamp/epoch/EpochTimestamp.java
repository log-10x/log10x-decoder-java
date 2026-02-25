package com.log10x.decode.timestamp.epoch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import com.log10x.decode.timestamp.Timestamp;

abstract class EpochTimestamp extends Timestamp {

	EpochTimestamp(String pattern) {
		super(pattern);
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
