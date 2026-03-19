package com.log10x.decode.main.stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.template.Template;

/**
 * 
 * @author Dor Levi
 *
 */
public class StreamDecoder {

	private static final Logger logger = LoggerFactory.getLogger(StreamDecoder.class);

	/**
	 * Reads Log10x encoded lines from an {@link InputStream}, decodes each line, and
	 * write them to an {@link OutputStream}.
	 * 
	 * If decoding a given line fails, for example if the matching {@link Template}
	 * is missing from {@code templateProvider}, the original encoded line will be
	 * written to {@code out}
	 * 
	 * @param context          {@link TokenizeContext} for the decoding process.
	 * @param templateProvider {@link TemplateProvider} containing all expected
	 *                         templates.
	 * @param in               Source of encoded lines.
	 * @param out              Destination of decoded lines.
	 * 
	 * @return {@code true} if decoded successfully, {@code false} if any exceptions
	 *         were thrown.
	 */
	public static boolean decode(TokenizeContext context, TemplateProvider templateProvider, InputStream in,
			OutputStream out) {

		char delimiter = context.eventOptions().encodeDelimiter();
		char prefix = context.eventOptions().encodedLinePrefix();

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.defaultCharset()));
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

			for (String line; (line = br.readLine()) != null;) {

				// Strip prefix if configured
				String working = line;
				if (prefix != 0 && working.length() > 0 && working.charAt(0) == prefix) {
					working = working.substring(1);
				}

				int index = working.indexOf(delimiter);

				String hash;
				String encodedLine;

				if (index > -1) {
					hash = working.substring(0, index);
					encodedLine = working.substring(index + 1);
				} else {
					hash = working;
					encodedLine = "";
				}

				String decodedLine = decodeLine(context, templateProvider, hash, encodedLine);

				if (decodedLine != null) {
					writer.write(decodedLine);
				} else {
					writer.write(line);
				}

				writer.newLine();
			}

			return true;

		} catch (Exception e) {
			logger.error("Error while decoding.", e);
			return false;
		}
	}

	private static String decodeLine(TokenizeContext context, TemplateProvider templateProvider, String hash,
			String encodedLine) {

		Template template = templateProvider.getTemplate(hash);

		if (template == null) {
			logger.warn("Missing template for {}.", hash);
			return null;
		}

		try {
			EventLexer lexer = new EventLexer(context, template);
			return lexer.decode(encodedLine);

		} catch (Exception e) {
			logger.error("Failed decoding line {}", encodedLine, e);
			return null;
		}
	}
}
