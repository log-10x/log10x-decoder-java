package com.log10x.decode;

import java.io.IOException;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.template.Template;
import com.log10x.decode.template.TemplateLexer;

/**
 * Sample class for showing how to utilize the Log10x decoder classes to decode
 * events.
 * 
 * @author Dor Levi
 */
public class SingleEventDecoder {

	/**
	 * Decodes a single Log10x encoded event.
	 * 
	 * @param context  {@link TokenizeContext} matching the one the event was
	 *                 encoded with. Can use {@link TokenizeContext#DEFAULT} if
	 *                 unsure, though results might not be accurate.
	 * @param hash     The hash of the template matching this event. Usually it's
	 *                 the first word in each encoded output line, containing
	 *                 between 9 and 12 characters.
	 * @param input    The actual encoded event, *without* the hash of the template.
	 * @param template The template matching the {@code hash} provided.
	 * 
	 * @return The decoded (original) event, before it was encoded by Log10x
	 * 
	 * @throws IOException in case of writer errors.
	 */
	public static String decode(TokenizeContext context, String hash, String input, String template)
			throws IOException {
		TemplateLexer templateLexer = new TemplateLexer(context, hash);

		Template eventVarTemplate = templateLexer.tokenize(template);

		EventLexer eventLexer = new EventLexer(context, eventVarTemplate);

		String res = eventLexer.decode(input);

		return res;
	}
}
