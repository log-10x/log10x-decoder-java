package com.log10x.decode.main.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;

/**
 * A timestamp slot with an epoch pattern ({@code +%s.%N}, {@code +%s}) is
 * written back as the epoch text the engine read. Templates and lines are the
 * engine's output for those source lines.
 */
class EpochTimestampTest {

	private static final String NL = System.lineSeparator();

	private static final TokenizeContext CONTEXT = TokenizeContext.create(new TokenOptions(),
		new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

	private static String decode(String line) {

		Map<String, String> templates = new HashMap<>();

		templates.put("ms", "$(+%s.%N) INFO epoch ms fraction $ digits");
		templates.put("json", "{\"ts\":$(+%s.%N),\"msg\":\"$\"}");
		templates.put("ns", "$(+%s) INFO epoch ns");
		templates.put("date", "$(yyyy-MM-dd HH:mm:ss.SSS) INFO date");

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT, new TemplateProvider(CONTEXT, templates),
			new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8)), out));

		String text = new String(out.toByteArray(), StandardCharsets.UTF_8);

		assertTrue(text.endsWith(NL), text);

		return text.substring(0, text.length() - NL.length());
	}

	@Test
	void millisecondsWithAFractionKeepTheDot() {
		assertEquals("1705313472123.456 INFO epoch ms fraction 3 digits",
			decode("~ms,1705313472123.456,3"));
	}

	@Test
	void leadingZerosOfTheFractionAreKept() {
		assertEquals("1705313472123.000456 INFO epoch ms fraction 6 digits",
			decode("~ms,1705313472123.000456,6"));
	}

	@Test
	void nineFractionDigitsDoNotOverflow() {
		assertEquals("1705313472123.456789123 INFO epoch ms fraction 9 digits",
			decode("~ms,1705313472123.456789123,9"));
	}

	@Test
	void jsonEpochWithAFraction() {
		assertEquals("{\"ts\":1705313472123.456789,\"msg\":\"started\"}",
			decode("~json,1705313472123.456789,started"));
	}

	@Test
	void nanosecondEpochIsWrittenAsRead() {
		assertEquals("1705313472123456789 INFO epoch ns", decode("~ns,1705313472123456789"));
	}

	@Test
	void datePatternStillFormatsTheEpoch() {
		assertEquals("2024-01-15 10:11:12.123 INFO date", decode("~date,1705313472123"));
	}
}
