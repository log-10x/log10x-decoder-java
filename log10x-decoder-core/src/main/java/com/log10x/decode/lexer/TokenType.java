package com.log10x.decode.lexer;

/**
 * 
 * An enum describing a type of token.
 * 
 * @author Dor Levi
 *
 */
public enum TokenType {
	/**
	 * Symbol tokens are part of a Template describing an event, i.e. they are
	 * "fixed" in value across multiple events with the same template structure.
	 */
	Symbol,

	/**
	 * Var tokens are the variable part of events, i.e. they can be different in
	 * value across multiple events with the same template structure.
	 */
	Var,

	/**
	 * Delim tokens are part of a Template (similar to {@link #Symbol}), and are
	 * used to separate individual {@link #Symbol} type tokens.
	 */
	Delim,

	/**
	 * A special type of delimiter, useful as it's the default
	 * "missing"/"unavailable" value in many universal cases.
	 */
	MinusOne;

	/**
	 * Array containing all values, used for quick random access conversion between
	 * the ordinal value and the actual {@link TokenType}
	 */
	public static final TokenType[] values = TokenType.values();
}
