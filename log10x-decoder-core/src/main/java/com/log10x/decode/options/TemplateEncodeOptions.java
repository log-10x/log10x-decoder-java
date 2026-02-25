package com.log10x.decode.options;

import com.log10x.decode.util.ToString;

/**
 * 
 * Options used for defining overall structure of encoded templates.
 * 
 * For proper decoding, those options need to match the ones used for encoding.
 * events.
 * 
 * @author Dor Levi
 *
 */
public class TemplateEncodeOptions {

	/**
	 * Variable escape character
	 */
	public static final char VAR_ESCAPE = '/';

	/**
	 * Default variable prefix marker
	 */
	public static final char VAR_PREFIX = '$';

	/**
	 * Default timestamp prefix marker
	 */
	public static final String TIMESTAMP_PREFIX = "$(";

	/**
	 * Default timestamp postfix marker
	 */
	public static final String TIMESTAMP_POSTFIX = ")";

	private static final String SINGLE_ESCAPE_STR = String.valueOf(VAR_ESCAPE);
	private static final String DOUBLE_ESCAPE_STR = SINGLE_ESCAPE_STR + SINGLE_ESCAPE_STR;

	private final char varPlaceholder;

	private final String timestampPrefix;

	private final String timestampPostfix;

	private transient String varPlaceholderStr;
	private transient String varPlaceholderEsc;

	/**
	 * Creates an instance with default markers.
	 */
	public TemplateEncodeOptions() {
		this(VAR_PREFIX, TIMESTAMP_PREFIX, TIMESTAMP_POSTFIX);
	}

	/**
	 * Creates an instance with custom markers.
	 * 
	 * @param varPlaceholder   Marker for variables in a template
	 * @param timestampPrefix  Prefix marker for timestamps
	 * @param timestampPostfix Postfix marker for timestamps
	 */
	public TemplateEncodeOptions(char varPlaceholder, String timestampPrefix, String timestampPostfix) {

		this.varPlaceholder = varPlaceholder;

		this.timestampPrefix = timestampPrefix;
		this.timestampPostfix = timestampPostfix;
	}

	/**
	 * @return Variable marker defined in these options.
	 */
	public char varPlaceholder() {
		return this.varPlaceholder;
	}

	/**
	 * @return Timestamp prefix marker defined in these options.
	 */
	public String timestampPrefix() {
		return this.timestampPrefix;
	}

	/**
	 * @return Timestamp postfix marker defined in these options.
	 */
	public String timestampPostfix() {
		return this.timestampPostfix;
	}

	private String varPlaceholderStr() {

		if (this.varPlaceholderStr == null) {
			this.varPlaceholderStr = String.valueOf(varPlaceholder);
		}

		return this.varPlaceholderStr;
	}

	private String varPlaceholderEsc() {

		if (this.varPlaceholderEsc == null) {
			this.varPlaceholderEsc = VAR_ESCAPE + String.valueOf(varPlaceholder);
		}

		return this.varPlaceholderEsc;
	}

	/**
	 * Unescapes a given pattern based on the escape character {@link #VAR_ESCAPE}
	 * 
	 * @param pattern Input pattern to unescape
	 * @return Unescaped pattern
	 */
	public String unescape(String pattern) {

		return pattern
				.replace(varPlaceholderEsc(), varPlaceholderStr())
				.replace(DOUBLE_ESCAPE_STR, SINGLE_ESCAPE_STR);
	}

	@Override
	public String toString() {
		return ToString.formatPrefix(super.toString(),
			"varPlaceholder", varPlaceholder,
			"timestampPrefix", timestampPrefix,
			"timestampPostfix", timestampPostfix
		);
	}
}
