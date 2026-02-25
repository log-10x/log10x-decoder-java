package com.log10x.decode.template.segment;

import java.io.IOException;
import java.io.Writer;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.util.ToString;
import com.log10x.decode.util.chars.CharArraySequence;

/**
 * 
 * A {@link Segment} which is part of the variables the events, i.e. the part
 * that changes between different events sharing the same template.
 * 
 * @author Dor Levi
 *
 */
public class VarSegment extends Segment {

	private final short varIndex;

	/**
	 * Creates a new instance
	 * 
	 * @param varIndex The index within the event of this variable segment
	 */
	public VarSegment(int varIndex) {
		this.varIndex = (short) varIndex;
	}

	@Override
	public SegmentType type() {
		return SegmentType.Var;
	}

	@Override
	public void write(Writer writer, EventLexer eventLexer) throws IOException {
		CharArraySequence varValue = eventLexer.varValue(varIndex);

		writer.write(varValue.array(), varValue.start(), varValue.length());
	}

	@Override
	public String toString() {

		return ToString.format(
			"type", type(),
			"index", varIndex
		);
	}
}
