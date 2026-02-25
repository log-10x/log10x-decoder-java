package com.log10x.decode.lexer;

import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;

/**
 * A context for all decoder operations, basically a central place for all the
 * different configuration options.
 * 
 * For proper decoding, those options need to match the ones used for encoding
 * events.
 * 
 * @author Dor Levi
 *
 */
public class TokenizeContext {

	/**
	 * Singleton default instance, setting all internal options to their own
	 * defaults.
	 */
	public static final TokenizeContext DEFAULT = create(
			new TokenOptions(),
			new TimestampOptions(),
			new TemplateEncodeOptions(),
			new EventEncodeOptions());

	private final TokenOptions tokenOptions;

	private final TimestampOptions timestampOptions;

	private final TemplateEncodeOptions templateOptions;

	private final EventEncodeOptions eventOptions;

	private TokenizeContext(TokenOptions tokenOptions, TimestampOptions timestampOptions,
			TemplateEncodeOptions templateOptions, EventEncodeOptions eventOptions) {

		this.tokenOptions = tokenOptions;
		this.timestampOptions = timestampOptions;
		this.templateOptions = templateOptions;
		this.eventOptions = eventOptions;
	}

	/**
	 * @return {@link TokenOptions} this context was initialized with.
	 */
	public TokenOptions tokenOptions() {
		return this.tokenOptions;
	}

	/**
	 * @return {@link TimestampOptions} this context was initialized with.
	 */
	public TimestampOptions timestampOptions() {
		return this.timestampOptions;
	}

	/**
	 * @return {@link TemplateEncodeOptions} this context was initialized with.
	 */
	public TemplateEncodeOptions templateOptions() {
		return this.templateOptions;
	}

	/**
	 * @return {@link EventEncodeOptions} this context was initialized with.
	 */
	public EventEncodeOptions eventOptions() {
		return this.eventOptions;
	}

	/**
	 * Creates a new {@link TokenizeContext} instance with the provided options.
	 * 
	 * @param tokenOptions     {@link TokenOptions} to use with the new instance.
	 * @param timestampOptions {@link TimestampOptions} to use with the new
	 *                         instance.
	 * @param templateOptions  {@link TemplateEncodeOptions} to use with the new
	 *                         instance.
	 * @param eventOptions     {@link EventEncodeOptions} to use with the new
	 *                         instance.
	 * @return A new {@link TokenizeContext} instance with the provided options.
	 */
	public static TokenizeContext create(TokenOptions tokenOptions, TimestampOptions timestampOptions,
			TemplateEncodeOptions templateOptions, EventEncodeOptions eventOptions) {

		return new TokenizeContext(tokenOptions, timestampOptions, templateOptions, eventOptions);
	}
}
