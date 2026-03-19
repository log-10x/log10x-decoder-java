package com.log10x.decode.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.log10x.decode.lexer.BaseLexer;
import com.log10x.decode.lexer.TokenType;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.template.segment.PatternSegment;
import com.log10x.decode.template.segment.Segment;
import com.log10x.decode.template.segment.SegmentType;
import com.log10x.decode.template.segment.TimestampSegment;
import com.log10x.decode.template.segment.VarSegment;
import com.log10x.decode.timestamp.Timestamp;
import com.log10x.decode.util.DigitUtil;
import com.log10x.decode.util.StringUtil;
import com.log10x.decode.util.ToString;
import com.log10x.decode.util.chars.CharArraySequence;
import com.log10x.decode.util.chars.CharArraySubSequence;

/**
 * Main class for lexing and decoding templates.
 * 
 * @author Dor Levi
 *
 */
public class TemplateLexer extends BaseLexer {

	private static enum CharState {
		DELIM_CHAR,
		SYMBOL_CHAR,
		TIMESTAMP_CHAR,
		NON_INDEXED_VAR,
		NUMBER_CHAR,
		VAR_NOT_FOUND,
		NAMED_VAR
	}

	private static final Segment[] EMPTY_SEGMENT_ARRAY = new Segment[0];

	private static final int MINUS_ONE_LEN = String.valueOf(-1).length();

	protected char currChar;
	protected int currCharIndex;

	private int timestampEnd;

	protected final StringBuilder varNameBuilder;

	private final Map<Integer, Integer> varIndexes;

	private final List<Segment> segments;

	private final String templateHash;

	private final List<TimestampTemplate> timestamps;

	private int varSize;

	/**
	 * Constructs a new {@link TemplateLexer}
	 * 
	 * @param context      {@link TokenizeContext} used for tokenizing
	 * @param templateHash {@link String} of the unique hash id of this template.
	 */
	public TemplateLexer(TokenizeContext context, String templateHash) {

		super(context);

		this.templateHash = templateHash;

		this.varIndexes = new HashMap<>();

		this.segments = new ArrayList<>();
		this.varNameBuilder = new StringBuilder();

		this.timestamps = new ArrayList<>();
	}

	@Override
	protected boolean initialize(String s) {

		if (!super.initialize(s)) {
			return false;
		}

		this.currChar = 0;
		this.currCharIndex = this.start;

		this.timestamps.clear();
		this.timestampEnd = -1;

		this.varIndexes.clear();
		this.segments.clear();

		varSize = 0;

		return true;
	}

	private boolean escapeChar(int offset) {

		char c = chars[currCharIndex + offset];
		return c == TemplateEncodeOptions.VAR_ESCAPE;
	}

	private boolean currCharEscaped() {

		if ((currCharIndex > this.start + 1) &&
			(escapeChar(-1)) && (!escapeChar(-2))) {

			return true;

		} else if ((currCharIndex == this.start + 1) &&
				   (escapeChar(-1))) {

			return true;
		}

		return false;
	}

	private TimestampTemplate getTimestamp() {

		if (templateEncodeOptions().timestampPrefix() == null) {
			return null;
		}

		int prefixLen = templateEncodeOptions().timestampPrefix().length();
		int end = this.start + this.length;

		if (this.currCharIndex + prefixLen >= end) {
			return null;
		}

		if (currCharEscaped()) {
			return null;
		}

		if (!StringUtil.regionMatches(this.chars,
			this.currCharIndex, templateEncodeOptions().timestampPrefix())) {

			return null;
		}

		int tokenSize = 0;
		boolean isCurrVar = true;

		String timestampPostfix = templateEncodeOptions().timestampPostfix();

		int startFrom = this.currCharIndex + prefixLen;

		for (int i = startFrom; i < end; i++) {

			if (StringUtil.regionMatches(this.chars, i, timestampPostfix)) {

				int timestampStartIndex = this.tokenSize;

				int start = this.currCharIndex + prefixLen;
				int len = i - start;

				CharArraySequence pattern = new CharArraySubSequence(this.chars, start, len);

				Timestamp timestamp = timestampOptions().getPattern(pattern);

				if (timestamp == null) {
					return null;
				}

				int timestampSize = (isCurrVar) ? (tokenSize + 1) : tokenSize;

				return new TimestampTemplate(timestamp, timestampStartIndex, timestampStartIndex + timestampSize);
			}

			char c = this.chars[i];

			if (tokenOptions().isTokenDelim(c)) {

				if ((isCurrVar) && (i != startFrom)) {
					tokenSize++;
				}

				isCurrVar = false;
				tokenSize++;

			} else {
				isCurrVar = true;
			}
		}

		return null;
	}

	private CharState getVarState() {

		if (this.currChar != templateEncodeOptions().varPlaceholder()) {
			return CharState.VAR_NOT_FOUND;
		}

		if (currCharEscaped()) {
			return CharState.VAR_NOT_FOUND;
		}

		int end = this.start + this.length;

		if (this.currCharIndex == end - 1) {
			return CharState.NON_INDEXED_VAR;
		}

		CharState result = CharState.VAR_NOT_FOUND;

		varNameBuilder.setLength(0);

		for (int i = this.currCharIndex + 1; i < end; i++) {

			char nextChar = this.chars[i];

			if (Character.isDigit(nextChar)) {

				varNameBuilder.append(nextChar);
				result = CharState.NAMED_VAR;

			} else if (nextChar == templateEncodeOptions().varPlaceholder()) {

				if (varNameBuilder.length() == 0) {
					result = CharState.NON_INDEXED_VAR;
				}

				break;

			} else if (tokenOptions().isTokenDelim(nextChar)) {

				if (varNameBuilder.length() == 0) {
					result = CharState.NON_INDEXED_VAR;
				}

				break;

			} else {
				result = CharState.VAR_NOT_FOUND;
				break;
			}
		}

		return result;
	}

	private static TokenType tokenType(boolean isNumber) {

		if (isNumber) {
			return TokenType.MinusOne;
		} else {
			return TokenType.Symbol;
		}
	}

	@Override
	protected void addToken(TokenType tokenType, int start, int endOrVarIndex) {

		int tokenEnd;

		if (tokenType == TokenType.Var) {

			int templateIndex = tokenSize;
			varIndexes.put(templateIndex, endOrVarIndex);

			tokenEnd = this.start + endOrVarIndex;

		} else {
			tokenEnd = endOrVarIndex;
		}

		super.addToken(tokenType, start, tokenEnd);
	}

	@Override
	public int tokenLength(int tokenIndex) {

		if (this.tokenType(tokenIndex) == TokenType.Var) {
			return 1;
		}

		return super.tokenLength(tokenIndex);
	}

	private int timestampSize() {
		return timestamps.size();
	}

	private TimestampTemplate timestamp(int index) {
		return timestamps.get(index);
	}

	private void addTimestamp(TimestampTemplate timestamp) {
		timestamps.add(timestamp);

		int prefixLen = templateEncodeOptions().timestampPrefix().length();
		int patternLen = timestamp.pattern().length();

		this.timestampEnd = this.currCharIndex + prefixLen + patternLen;
	}

	private void addVar(CharState charState) {

		int varIndex;

		if (charState == CharState.NON_INDEXED_VAR) {

			varIndex = this.varSize;
			this.varSize++;

		} else if (charState == CharState.NAMED_VAR) {

			int varOffset = DigitUtil.parseInt(this.varNameBuilder, -1);

			if (varOffset == -1) {
				throw new IllegalStateException("Unsupported variable " + this.varNameBuilder);
			}

			if (varOffset != 0) {

				varIndex = this.varSize - varOffset;

			} else {

				varIndex = this.varSize;
				this.varSize++;
			}

		} else {
			throw new IllegalStateException(String.valueOf(charState));
		}

		addToken(TokenType.Var, currCharIndex, varIndex);
	}

	private CharState processCharState() {

		TimestampTemplate timestamp = getTimestamp();

		if (timestamp != null) {
			addTimestamp(timestamp);
			return CharState.TIMESTAMP_CHAR;
		}

		CharState vatState = getVarState();

		if (vatState != CharState.VAR_NOT_FOUND) {
			return vatState;
		}

		if (tokenOptions().isTokenDelim(currChar)) {

			boolean isNegativeNum = StringUtil.isNegativeNum(chars,
				currChar, currCharIndex, start + length);

			if (isNegativeNum) {
				return CharState.NUMBER_CHAR;
			} else {
				return CharState.DELIM_CHAR;
			}
		}

		return CharState.SYMBOL_CHAR;
	}

	@Override
	protected void processTokens() {

		int beginToken = this.start;
		int endToken = this.start;

		boolean isNumber = true;

		int length = this.start + this.length;

		while (this.currCharIndex < length) {

			if (this.currCharIndex == timestampEnd) {

				this.timestampEnd = -1;
				this.currCharIndex++;

				continue;
			}

			int advance;

			this.currChar = this.chars[this.currCharIndex];
			CharState charState = processCharState();

			if (charState == CharState.SYMBOL_CHAR) {

				if (!StringUtil.isNumeral(this.currChar)) {
					isNumber = false;
				}

				endToken++;
				advance = 1;

			} else {

				if (endToken - beginToken > 0) {

					TokenType type = tokenType(isNumber);

					isNumber = true;
					addToken(type, beginToken, endToken);
				}

				if (charState == CharState.DELIM_CHAR) {

					addToken(TokenType.Delim, this.currCharIndex,
						this.currCharIndex + 1);

					advance = 1;

					beginToken = this.currCharIndex + advance;
					endToken = beginToken;

				} else if (charState == CharState.NUMBER_CHAR) {

					advance = MINUS_ONE_LEN;

					addToken(TokenType.MinusOne, this.currCharIndex,
						this.currCharIndex + advance);

					beginToken = this.currCharIndex + advance;
					endToken = beginToken;

				} else if (charState == CharState.TIMESTAMP_CHAR) {

					advance = templateEncodeOptions().timestampPrefix().length();

					beginToken = this.currCharIndex + advance;
					endToken = beginToken;

				} else if ((charState == CharState.NON_INDEXED_VAR) ||
						   (charState == CharState.NAMED_VAR)) {

					addVar(charState);
					advance = varNameBuilder.length() + 1;

					beginToken = this.currCharIndex + advance;
					endToken = beginToken;

				} else {

					throw new IllegalStateException("Unknown char state: " + charState);
				}

			}

			this.currCharIndex += advance;
		}

		if (beginToken < endToken) {
			addToken(tokenType(isNumber), beginToken, endToken);
		}

		tokenizeSegments();
	}

	private TimestampTemplate getTimestamp(int index, int timestampStart, int size) {

		for (int i = timestampStart; i < size; i++) {

			TimestampTemplate timestampTemplate = this.timestamp(i);

			if (timestampTemplate.tokenIndex() == index) {
				return timestampTemplate;
			}
		}

		return null;
	}

	private void addSegment(int tokenIndex, int tokenSize, SegmentType type) {

		if (type == SegmentType.Pattern) {

			char[] value;
			int start;
			int length;

			int tokenStart = tokenStart(tokenIndex);
			int endToken = tokenIndex + tokenSize - 1;

			int endTokenStart = tokenStart(endToken);
			int endTokenLen = tokenLength(endToken);

			int tokenEnd = endTokenStart + endTokenLen;

			boolean escaped = false;

			for (int i = tokenStart; i < tokenEnd - 1; i++) {

				char c = chars[i];

				if (c != TemplateEncodeOptions.VAR_ESCAPE) {
					continue;
				}

				char n = chars[i + 1];

				if ((n == TemplateEncodeOptions.VAR_ESCAPE) ||
					(n == templateEncodeOptions().varPlaceholder())) {

					escaped = true;
					break;
				}
			}

			if (escaped) {

				String pattern = new String(chars, tokenStart, tokenEnd - tokenStart);
				start = 0;
				value = templateEncodeOptions().unescape(pattern).toCharArray();
				length = value.length;

			} else {
				length = tokenEnd - tokenStart;
				start = tokenStart;
				value = chars;
			}

			segments.add(new PatternSegment(value, start, length));

		} else if (type == SegmentType.Var) {

			Integer varIndex = varIndexes.get(tokenIndex);

			if (varIndex == null) {
				throw new IllegalStateException("No var index for token index " + tokenIndex);
			}

			int actualIndex = varIndex.intValue() + timestamps.size();

			segments.add(new VarSegment(actualIndex));

		} else if (type == SegmentType.Timestamp) {

			// TODO - consider a more efficient way to get this.
			//
			int timestampIndex = 0;

			for (int i = segments.size() - 1; i >= 0; i--) {
				Segment currentSegment = segments.get(i);

				if (currentSegment.type() == SegmentType.Timestamp) {
					timestampIndex = ((TimestampSegment) currentSegment).timestampIndex() + 1;
					break;
				}
			}

			TimestampTemplate timestampTemplate = timestamp(timestampIndex);
			segments.add(new TimestampSegment(timestampIndex, timestampTemplate.timestamp()));
		}
	}

	protected void tokenizeSegments() {

		int tokenSize = this.tokenSize;
		int timestampSize = this.timestampSize();

		int index = 0;
		int timestamps = 0;

		int currLen = 0;
		int currIndex = 0;

		SegmentType currType = null;

		while (index < tokenSize) {

			TimestampTemplate timestamp;

			if (timestamps < timestampSize) {
				timestamp = getTimestamp(index, timestamps, timestampSize);
			} else {
				timestamp = null;
			}

			if (timestamp != null) {

				if (currLen > 0) {
					addSegment(currIndex, currLen, currType);
				}

				currIndex += currLen;
				currType = SegmentType.Timestamp;

				int timestampLen = timestamp.tokenLength();
				addSegment(currIndex, timestampLen, currType);

				currLen = 0;
				timestamps++;

				index += timestampLen;
				currIndex += timestampLen;

			} else {

				TokenType tokenType = super.tokenType(index);
				SegmentType segmentType = SegmentType.from(tokenType);

				if (segmentType == SegmentType.Pattern) {

					if (segmentType != currType) {

						if (currLen > 0) {

							addSegment(currIndex, currLen, currType);
							currIndex += currLen;
							currLen = 0;

						} else {
							currLen++;
						}

						currType = segmentType;

					} else {
						currLen++;
					}

				} else if (segmentType == SegmentType.Var) {

					if (segmentType != currType) {

						if (currLen > 0) {
							addSegment(currIndex, currLen, currType);
							currIndex += currLen;
						}

					}

					addSegment(currIndex, 1, SegmentType.Var);

					currLen = 0;
					currIndex++;
					currType = segmentType;

				} else {

					currLen++;
					currType = segmentType;
				}

				index++;
			}
		}

		if (currLen > 0) {
			addSegment(currIndex, currLen, currType);
		}
	}

	/**
	 * Constructs a new {@link Template} from the provided {@link String}
	 * 
	 * @param s input {@link String} to tokenize.
	 * @return New {@link Template}
	 */
	public Template tokenize(String s) {

		if (this.shouldInitialize() && (!this.initialize(s))) {
			return null;
		}

		if (!this.tokenize()) {
			return null;
		}

		return new Template(templateHash, segments.toArray(EMPTY_SEGMENT_ARRAY));
	}

	@Override
	public String toString() {

		return ToString.formatPrefix(super.toString(),
			"currIndex", currCharIndex,
			"currChar", currChar,
			"timestampEnd", timestampEnd,
			"segments", Segment.printSegments(segments.toArray(EMPTY_SEGMENT_ARRAY)));
	}
}
