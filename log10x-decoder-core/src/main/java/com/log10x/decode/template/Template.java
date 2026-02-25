package com.log10x.decode.template;

import com.log10x.decode.template.segment.Segment;
import com.log10x.decode.template.segment.SegmentType;
import com.log10x.decode.util.ToString;

/**
 * 
 * Class representing an encoded event template, i.e. the part of a log event
 * that's the same between events sharing the same overall structure.
 * 
 * @author Dor Levi
 *
 */
public class Template {

	private final String encodedTokenHash;
	private final Segment[] segments;
	private final int timestampSize;

	/**
	 * Creates a new instance.
	 * 
	 * @param encodedTokenHash Unique hash identifier for this {@link Template}
	 * @param segments         The {@code Segment[]} defining this {@link Template}
	 *                         structure.
	 */
	public Template(String encodedTokenHash, Segment[] segments) {

		this.encodedTokenHash = encodedTokenHash;
		this.segments = segments;

		this.timestampSize = calcTimestampSize();
	}

	private int calcTimestampSize() {
		int result = 0;

		for (int i = 0; i < segmentSize(); i++) {
			if (segment(i).type() == SegmentType.Timestamp) {
				result++;
			}
		}

		return result;
	}

	/**
	 * @return The amount of segments in this {@link Template}, can be 0 if empty.
	 */
	public int segmentSize() {
		return (segments == null ? 0 : segments.length);
	}

	/**
	 * @param i Index of {@link Segment} in this template to get.
	 * @return The {@link Segment} in the {@code i} index.
	 * 
	 * @throws ArrayIndexOutOfBoundsException if the internal {@code Segment[]} is
	 *                                        {@code null}, or the index is out of
	 *                                        range, i.e index &lt; 0 || index &gt;=
	 *                                        segmentSize()
	 */
	public Segment segment(int i) {
		if ((segments == null) ||
			(i < 0) ||
			(i >= segmentSize())) {

			throw new ArrayIndexOutOfBoundsException(i);
		}

		return segments[i];
	}

	/**
	 * @return The amount of {@link Segment} with a type of
	 *         {@link SegmentType#Timestamp}
	 */
	public int timestampSize() {
		return timestampSize;
	}

	@Override
	public String toString() {

		return ToString.formatList(
			"encodedTokenHash", encodedTokenHash,
			"segmentsSize", (segments != null) ? segments.length : null,
			"segments", Segment.printSegments(segments));
	}
}
