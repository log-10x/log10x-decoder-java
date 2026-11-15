package com.log10x.decode.main.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;
import com.log10x.decode.template.Template;

/**
 * {@link StreamDecoder} with the encoded-line prefix set, as the command line
 * runs it with {@code -d , -p ~}.
 */
class StreamDecoderTest {

	private static final String NL = System.lineSeparator();

	/** Records each hash it is asked for. */
	private static final class RecordingProvider extends TemplateProvider {

		final List<String> lookups = new ArrayList<>();

		RecordingProvider(TokenizeContext context, Map<String, String> rawTemplates) {
			super(context, rawTemplates);
		}

		@Override
		public Template getTemplate(String hash) {
			lookups.add(hash);
			return super.getTemplate(hash);
		}
	}

	private static final TokenizeContext CONTEXT = TokenizeContext.create(new TokenOptions(),
		new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

	private static String decode(TemplateProvider provider, String input) {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT, provider,
			new ByteArrayInputStream(input.getBytes(Charset.defaultCharset())), out));

		return new String(out.toByteArray(), Charset.defaultCharset());
	}

	@Test
	void encodedLineIsDecoded() {

		RecordingProvider provider = new RecordingProvider(CONTEXT,
			Collections.singletonMap("h1", "user $ logged in"));

		assertEquals("user alice logged in" + NL, decode(provider, "~h1,alice\n"));
		assertEquals(Collections.singletonList("h1"), provider.lookups);
	}

	/**
	 * A line that does not begin with the prefix is not encoded (FORMAT_SPEC
	 * S4.1): it is written as read and no template is looked up for it, even
	 * when the text before its first delimiter is a known template hash.
	 */
	@Test
	void lineWithoutThePrefixPassesThroughWithoutALookup() {

		RecordingProvider provider = new RecordingProvider(CONTEXT,
			Collections.singletonMap("h1", "user $ logged in"));

		String input = "~h1,alice\n" + "h1,bob\n" + "\n" + "{\"eventVersion\":\"1.08\",\"a\":1}\n";

		assertEquals("user alice logged in" + NL + "h1,bob" + NL + NL + "{\"eventVersion\":\"1.08\",\"a\":1}" + NL,
			decode(provider, input));

		assertEquals(Collections.singletonList("h1"), provider.lookups);
	}

	@Test
	void encodedLineWithAMissingTemplateIsWrittenAsRead() {

		RecordingProvider provider = new RecordingProvider(CONTEXT,
			Collections.singletonMap("h1", "user $ logged in"));

		assertEquals("~h2,carol" + NL, decode(provider, "~h2,carol\n"));
		assertEquals(Collections.singletonList("h2"), provider.lookups);
	}
}
