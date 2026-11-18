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

	/**
	 * A compact stream from a Receiver that sends an event as its own text
	 * while the event's shape has no template yet (templateCreateAfter): the
	 * engine writes every event that starts with {@code ~} encoded, so a line
	 * the reader sees without the prefix is that event's text, and passes
	 * through untouched whatever it holds; every {@code ~} line is decoded,
	 * including those whose original text itself began with {@code ~}.
	 */
	@Test
	void eventsWrittenAsTextPassThroughBesideEncodedOnes() {

		Map<String, String> templates = new java.util.HashMap<>();
		templates.put("h1", "user $ logged in");
		templates.put("t", "~");
		templates.put("tx", "~,x");
		templates.put("vv", "$,$");

		RecordingProvider provider = new RecordingProvider(CONTEXT, templates);

		String input =
			"~h1,alice\n" +
			"user bob logged in from a new shape\n" +
			"h1,1794909600000,91,vol1\n" +
			"t,whole\n" +
			"~t\n" +
			"~tx\n" +
			"~vv,~~double,1\n" +
			"   \n";

		assertEquals(
			"user alice logged in" + NL +
			"user bob logged in from a new shape" + NL +
			"h1,1794909600000,91,vol1" + NL +
			"t,whole" + NL +
			"~" + NL +
			"~,x" + NL +
			"~~double,1" + NL +
			"   " + NL,
			decode(provider, input));

		assertEquals(java.util.Arrays.asList("h1", "t", "tx", "vv"), provider.lookups,
			"only the ~ lines are looked up");
	}

	/**
	 * An empty event's template is empty, and the engine writes its template
	 * record with a null template ({"templateHash":"-J}UrlyaKD8","template":null}).
	 * Its encoded line, the hash alone, decodes to an empty line, as the engine
	 * decodes it; a hash with no record at all is still kept as read.
	 */
	@Test
	void anEmptyEventsTemplateRecordHoldsNoTextAndDecodesToAnEmptyLine() {

		Map<String, String> templates = new java.util.HashMap<>();
		templates.put("h1", "user $ logged in");
		templates.put("-J}UrlyaKD8", null);

		RecordingProvider provider = new RecordingProvider(CONTEXT, templates);

		assertEquals(
			"user alice logged in" + NL + NL + "user bob logged in" + NL + "~nohash" + NL,
			decode(provider, "~h1,alice\n~-J}UrlyaKD8\n~h1,bob\n~nohash\n"));
	}
}
