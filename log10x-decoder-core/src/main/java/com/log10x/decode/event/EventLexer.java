package com.log10x.decode.event;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.log10x.decode.lexer.BaseLexer;
import com.log10x.decode.lexer.TokenType;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.template.Template;
import com.log10x.decode.template.segment.Segment;
import com.log10x.decode.util.LongUtil;
import com.log10x.decode.util.StringUtil;
import com.log10x.decode.util.chars.CharArraySequence;
import com.log10x.decode.util.chars.CharArraySubSequence;

/**
 * Main class for lexing and decoding events.
 * 
 * @author Dor Levi
 *
 */
public class EventLexer extends BaseLexer {

	private static final int NO_DELIM = -1;
	private static final long EMPTY_TIMESTAMP = -1;
	private static final char EPOCH_DIGIT = '.';
	
	private final Template template;
	private final List<Long> timestampEpochs;

	/**
	 * Constructs a new {@link EventLexer}
	 * 
	 * @param context  {@link TokenizeContext} used for tokenizing
	 * @param template {@link Template} of the events this {@link EventLexer} can
	 *                 decode
	 */
	public EventLexer(TokenizeContext context, Template template) {

		super(context);

		this.template = template;
		this.timestampEpochs = new ArrayList<>();
	}

	@Override
	protected boolean initialize(String s) {

		if (!super.initialize(s)) {
			return false;
		}

		timestampEpochs.clear();

		return true;
	}

	/**
	 * Decodes a Log10x encoded event and returns the result. The event needs to match
	 * the {@link Template} of this {@link EventLexer}, or decoding can return an
	 * unexpected result.
	 * 
	 * The input {@link String} to decode needs to be just the event to decode,
	 * without the template hash identifier.
	 * 
	 * @param s input {@link String} to decode.
	 * @return Log10x decoded event
	 * 
	 * @throws IOException in case of unexpected writer issues.
	 */
	public String decode(String s) throws IOException {

		if (this.shouldInitialize() && (!this.initialize(s))) {
			return null;
		}

		if (!this.tokenize()) {
			return null;
		}

		Writer writer = new StringWriter();

		for (int i = 0; i < template.segmentSize(); i++) {
			Segment segment = template.segment(i);

			segment.write(writer, this);
		}

		return writer.toString();
	}

	/**
	 * Returns a {@link CharArraySequence} for the event variable of the provided
	 * index.
	 * 
	 * @param index the index of the variable
	 * @return a new {@link CharArraySequence} matching this variable
	 */
	public CharArraySequence varValue(int index) {

		int start = super.tokenStart(index);
		int length = super.tokenLength(index);

		return new CharArraySubSequence(this.chars, start, length);
	}

	/**
	 * Returns a {@code long} for the timestamp of the provided index.
	 * 
	 * @param index the index of the variable
	 * @return a new {@code long} matching this timestamp
	 */
	public long timestampEpoch(int index) {
		if ((index < 0) || (index >= timestampEpochs.size())) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		return timestampEpochs.get(index);
	}

	private void addTimestamp(int beginIndex, int endIndex) {

		long epoch = LongUtil.parse(chars, beginIndex, endIndex, EMPTY_TIMESTAMP);

		if (epoch == EMPTY_TIMESTAMP) {

			int len = endIndex - beginIndex;

			for (int i = 0; i < len; i++) {

				char c = chars[beginIndex + i];

				if (c == EPOCH_DIGIT) {

					long epochMilli = LongUtil.parse(chars, beginIndex, beginIndex + i, EMPTY_TIMESTAMP);

					int epochNano = (int) LongUtil.parse(chars, beginIndex + i + 1, endIndex, EMPTY_TIMESTAMP);

					epoch = combineEpoch(epochMilli, epochNano);

					break;
				}
			}
		}

		timestampEpochs.add(epoch);
	}

	private long combineEpoch(long epochMilli, int nano) {

		int times = 1;

		while (times <= nano) {
			times *= 10;
		}

		return epochMilli * times + nano;
	}

	private void addVarToken(int beginIndex, int endIndex) {

		int timestampEpochsSize = timestampEpochs.size();

		if (timestampEpochsSize < template.timestampSize()) {
			addTimestamp(beginIndex, endIndex);
		}

		addToken(TokenType.Var, beginIndex, endIndex);
	}

	private boolean isDelim(char c) {
		return (c == eventEncodeOptions().encodeDelimiter());
	}

	@Override
	protected void processTokens() {

		boolean isNumber = true;

		int beginIndex = this.start;
		int endIndex = this.start;

		int end = this.start + this.length;

		int pendingDelim = NO_DELIM;

		for (int i = this.start; i < end; i++) {

			char c = this.chars[i];

			if (isDelim(c)) {

				if (endIndex - beginIndex > 0) {

					if (pendingDelim != NO_DELIM) {

						if ((isNumber) &&
							(this.chars[pendingDelim] == StringUtil.MINUS)) {

							if (endIndex - beginIndex > 1) {
								addVarToken(beginIndex, endIndex);
							}

						} else {

							if (endIndex > beginIndex + 1) {
								addVarToken(beginIndex + 1, endIndex);
							}
						}

					} else {

						addVarToken(beginIndex, endIndex);
					}

					isNumber = true;
				}

				beginIndex = endIndex;
				endIndex++;

				pendingDelim = i;

			} else {

				if (!StringUtil.isNumeral(c)) {
					isNumber = false;
				}

				endIndex++;
			}
		}

		int remaining = endIndex - beginIndex;

		if (remaining > 0) {

			if (pendingDelim != NO_DELIM) {

				if (remaining > 1) {

					if ((isNumber) &&
						(this.chars[pendingDelim] == StringUtil.MINUS)) {

						addVarToken(beginIndex, endIndex);

					} else {
						addVarToken(beginIndex + 1, endIndex);
					}
				}

			} else {
				addVarToken(beginIndex, endIndex);
			}
		}
	}
}
