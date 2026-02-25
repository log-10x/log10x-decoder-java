package com.log10x.decode.template.segment;

import java.io.IOException;
import java.io.Writer;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.util.StringUtil;
import com.log10x.decode.util.ToString;

/**
 * 
 * A {@link Segment} which is part of the concrete pattern of the events, i.e.
 * the part that's always the same
 * 
 * @author Dor Levi
 *
 */
public class PatternSegment extends Segment {

	private final char[] value;
	private final int start;
	private final short length;

	/**
	 * Creates a new instance wrapping a part of a {@code char[]}
	 * 
	 * @param value  The underlying {@code char[]}
	 * @param start  Start position of this segment (inclusive)
	 * @param length Length of this segment.
	 */
	public PatternSegment(char[] value, int start, int length) {
		this.value = value;
		this.start = start;
		this.length = (short) length;
	}

	@Override
	public SegmentType type() {
		return SegmentType.Pattern;
	}

	@Override
	public void write(Writer writer, EventLexer eventLexer) throws IOException {
		writer.write(value, start, length);
	}

	@Override
	public String toString() {

		return ToString.format(
			"type", type(),
			"value", "'" + StringUtil.print(value, start, length) + "'"
		);
	}
}
