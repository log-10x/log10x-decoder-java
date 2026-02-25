package com.log10x.decode.template.segment;

import java.io.IOException;
import java.io.Writer;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.timestamp.Timestamp;
import com.log10x.decode.util.ToString;

/**
 * 
 * A {@link Segment} which is part of the timestamps the events.
 * 
 * @author Dor Levi
 *
 */
public class TimestampSegment extends Segment {

	private final short timestampIndex;
	private final Timestamp timestamp;

	/**
	 * Creates a new instance
	 * 
	 * @param timestampIndex The index within the event of this timestamp segment
	 * @param timestamp      The {@link Timestamp} matching this index, useful for
	 *                       formatting values when decoding.
	 */
	public TimestampSegment(int timestampIndex, Timestamp timestamp) {
		this.timestampIndex = (short) timestampIndex;
		this.timestamp = timestamp;
	}

	@Override
	public SegmentType type() {
		return SegmentType.Timestamp;
	}

	/**
	 * @return The timestamp index of this segment.
	 */
	public int timestampIndex() {
		return this.timestampIndex;
	}

	@Override
	public void write(Writer writer, EventLexer eventLexer) throws IOException {
		long epoch = eventLexer.timestampEpoch(timestampIndex);

		this.timestamp.format(epoch, writer);
	}

	@Override
	public String toString() {

		return ToString.format(
			"type", type(),
			"pattern", "'" + timestamp.pattern() + "'"
		);
	}
}
