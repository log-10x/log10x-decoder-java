package com.log10x.decode.options;

class TokenConsts {

	protected static final char LT_DELIM = '<';
	protected static final char GT_DELIM = '>';
	protected static final char PIPE_DELIM = '|';
	protected static final char DOT_DELIM = '.';
	protected static final char SPACE_DELIM = ' ';
	protected static final char CLOSE_RECT_BRACKET_DELIM = ']';
	protected static final char OPEN_RECT_BRACKET_DELIM = '[';
	protected static final char COLON_DELIM = ':';
	protected static final char DASH_DELIM = '-';
	protected static final char PLUS_DELIM = '+';
	protected static final char SLASH_DELIM = '/';
	protected static final char ASTERISK_DELIM = '*';
	protected static final char EQUAL_DELIM = '=';
	protected static final char BACKSLASH_DELIM = '\\';
	protected static final char COMMA_DELIM = ',';
	protected static final char CURL_BRACKET_OPEN_DELIM = '{';
	protected static final char CURL_BRACKET_CLOSE_DELIM = '}';
	protected static final char UNDERSCORE_DELIM = '_';
	protected static final char BRACKET_OPEN_DELIM = '(';
	protected static final char BRACKET_CLOSE_DELIM = ')';
	protected static final char SEMICOLON_DELIM = ';';
	protected static final char SINGLE_QUOTE_DELIM = '\'';
	protected static final char DOUBLE_QUOTE_DELIM = '"';
	protected static final char DOLLAR_SIGN_DELIM = '$';

	protected static final char TEMPLATE_VAR = '$';
	protected static final String TEMPLATE_VAR_STR = String.valueOf(TEMPLATE_VAR);

	protected static final String MINUS_ONE = "-1";

	protected static final char TAB = '\t';
	protected static final char BREAK = '\n';

	protected static final String TOKEN_DELIMS = new String(new char[] {
		LT_DELIM,
		GT_DELIM,
		PIPE_DELIM,
		DOT_DELIM,
		SPACE_DELIM,
		CLOSE_RECT_BRACKET_DELIM,
		OPEN_RECT_BRACKET_DELIM,
		COLON_DELIM,
		DASH_DELIM,
		PLUS_DELIM,
		SLASH_DELIM,
		ASTERISK_DELIM,
		EQUAL_DELIM,
		BACKSLASH_DELIM,
		COMMA_DELIM,
		CURL_BRACKET_OPEN_DELIM,
		CURL_BRACKET_CLOSE_DELIM,
		UNDERSCORE_DELIM,
		BRACKET_OPEN_DELIM,
		BRACKET_CLOSE_DELIM,
		SEMICOLON_DELIM,
		SINGLE_QUOTE_DELIM,
		DOUBLE_QUOTE_DELIM,
		DOLLAR_SIGN_DELIM,
		TAB,
		BREAK
	});
}
