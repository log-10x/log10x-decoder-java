package com.log10x.decode.template;

import com.log10x.decode.timestamp.Timestamp;
import com.log10x.decode.util.ToString;

class TimestampTemplate {

	private final Timestamp timestamp;

	private final int startToken;
	private final int endToken;

	protected TimestampTemplate(Timestamp timestamp, int startIndex, int endIndex) {

		this.timestamp = timestamp;
		this.startToken = startIndex;
		this.endToken = endIndex;
	}

	protected Timestamp timestamp() {
		return this.timestamp;
	}

	protected int tokenLength() {
		return endToken - startToken;
	}

	protected int tokenIndex() {
		return this.startToken;
	}

	protected String pattern() {
		return timestamp.pattern();
	}

	@Override
	public String toString() {

		return ToString.format(
				"startToken", tokenIndex(),
				"tokenLength", tokenLength(),
				"timestamp", timestamp);
	}
}