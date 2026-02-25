package com.log10x.decode.options;

import com.log10x.decode.util.ToString;

/**
 * 
 * Options used for various tokenization processes in the decoder.
 * 
 * For proper decoding, those options need to match the ones used for encoding
 * events.
 * 
 * @author Dor Levi
 *
 */
public class TokenOptions {

	/**
	 * Singleton default instance.
	 */
	public static final TokenOptions defaultOptions = new TokenOptions();

	private final String tokenDelims;

	private transient boolean[] tokenDelimsKeys;

	private transient String[] tokenDelimsValues;

	/**
	 * Creates an instance with default token delimiters.
	 */
	public TokenOptions() {

		this(TokenConsts.TOKEN_DELIMS);
	}

	/**
	 * Creates an instance with custom token delimiters.
	 * 
	 * @param tokenDelims a {@link String} containing all delimiter characters to
	 *                    use when tokenizing.
	 */
	public TokenOptions(String tokenDelims) {

		this.tokenDelims = tokenDelims;
	}

	private void initialize() {

		if (this.tokenDelimsKeys != null) {
			return;
		}

		synchronized (this) {

			if (this.tokenDelimsKeys != null) {
				return;
			}

			this.tokenDelimsKeys = new boolean[Byte.MAX_VALUE];
			this.tokenDelimsValues = new String[tokenDelims.length()];

			for (int i = 0; i < tokenDelims.length(); i++) {

				char delim = tokenDelims.charAt(i);

				if (delim > Byte.MAX_VALUE) {
					throw new IllegalArgumentException("delim: " + delim + " cannot exceed " + Byte.MAX_VALUE);
				}

				tokenDelimsKeys[(byte) delim] = true;
				tokenDelimsValues[i] = String.valueOf(delim);
			}
		}
	}

	/**
	 * @return The token delimiters this was created with.
	 */
	public String tokenDelims() {
		return this.tokenDelims;
	}

	/**
	 * Checks if a provided {@code char} is a delimiter character.
	 * 
	 * @param delim {@code char} to check.
	 * @return {@code true} if {@code char} is a delimiter character, otherwise
	 *         returns {@code false}
	 */
	public boolean isTokenDelim(char delim) {

		if (delim >= Byte.MAX_VALUE) {
			return false;
		}

		initialize();

		return tokenDelimsKeys[(byte) delim];
	}

	@Override
	public String toString() {

		return ToString.format("tokenDelims", tokenDelims.replace(System.lineSeparator(), "\\n").replace("\t", "\\t"));
	}
}
