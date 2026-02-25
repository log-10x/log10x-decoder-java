package com.log10x.decode.template.segment;

import java.io.IOException;
import java.io.Writer;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.template.Template;

/**
 * 
 * Class representing part of an event {@link Template}
 * 
 * @author Dor Levi
 *
 */
public abstract class Segment {

	/**
	 * @return The type of this {@link Segment}
	 */
	public abstract SegmentType type();

	/**
	 * Writes this {@link Segment} to a destination {@link Writer}, within the
	 * context of a specific event.
	 * 
	 * While {@link SegmentType#Pattern} segments are part of the {@link Template}
	 * and will have the same output regardless of the event,
	 * {@link SegmentType#Var} and {@link SegmentType#Timestamp} values are part of
	 * the event itself.
	 * 
	 * @param writer     Destination {@link Writer}
	 * @param eventLexer Event context
	 * @throws IOException in case of writer errors.
	 */
	public abstract void write(Writer writer, EventLexer eventLexer) throws IOException;

	/**
	 * Util method to pretty print a {@code Segment[]}, used for debugging.
	 * 
	 * @param segments Segments to print
	 * @return A pretty representation of input segments.
	 */
	public static String printSegments(Segment[] segments) {

		if (segments == null) {
			return null;
		}

		int index = 0;
		StringBuilder result = new StringBuilder("\n\t");

		for (Segment segment : segments) {

			result.append(index).append(" = ").append(segment.toString());

			result.append((index < segments.length - 1) ? "\n\t" : "");

			index++;
		}

		return result.toString();
	}
}
