package com.log10x.decode.template.segment;

import com.log10x.decode.lexer.TokenType;

/**
 * 
 * An enum describing a type of {@link Segment}.
 * 
 * @author Dor Levi
 *
 */
public enum SegmentType {

	/**
	 * Pattern segments are the part of a Template describing an event, i.e. they
	 * are "fixed" in value across multiple events with the same template structure.
	 */
	Pattern,

	/**
	 * Var segments are the variable part of events, i.e. they can be different in
	 * value across multiple events with the same template structure.
	 */
	Var,

	/**
	 * Timestamp segments are special variables which represent a timestamp.
	 */
	Timestamp;

	/**
	 * Converts a {@link TokenType} to the matching {@link SegmentType}
	 * 
	 * @param tokenType {@link TokenType} to convert.
	 * @return Matching {@link SegmentType}
	 */
	public static SegmentType from(TokenType tokenType) {

		switch (tokenType) {
		case Delim:
		case MinusOne:
		case Symbol:
			return Pattern;
		case Var:
			return Var;
		}

		throw new IllegalStateException("Unknown token type " + tokenType);
	}
}
