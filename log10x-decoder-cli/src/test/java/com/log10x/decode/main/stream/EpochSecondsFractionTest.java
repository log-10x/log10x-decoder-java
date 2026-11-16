package com.log10x.decode.main.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.log10x.decode.event.EventLexer;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;
import com.log10x.decode.template.Template;
import com.log10x.decode.template.TemplateLexer;

/**
 * A timestamp slot {@code $(+%s.%f)} (seconds since the epoch, a dot, 1 to 9
 * fraction digits) writes the value text back as the engine read it, and its
 * event time is read digit by digit. Templates and lines are the engine's
 * output for those source lines.
 */
class EpochSecondsFractionTest {

	private static final String NL = System.lineSeparator();

	private static final TokenizeContext CONTEXT = TokenizeContext.create(new TokenOptions(),
		new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

	private static final String AUDIT = "type=SYSCALL msg=audit($(+%s.%f):$): arch=$ syscall=$";
	private static final String LOGFMT = "ts=$(+%s.%f) msg=hello";
	private static final String JSON = "{\"ts\":$(+%s.%f),\"msg\":\"$\"}";
	private static final String MILLIS = "type=SYSCALL msg=audit($(+%s.%N):$): arch=$ syscall=$";

	private static String decode(String line) {

		Map<String, String> templates = new HashMap<>();

		templates.put("Q<C9E6L.6o", AUDIT);
		templates.put("IE{DdzvP[H", LOGFMT);
		templates.put("json", JSON);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT, new TemplateProvider(CONTEXT, templates),
			new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8)), out));

		String text = new String(out.toByteArray(), StandardCharsets.UTF_8);

		assertTrue(text.endsWith(NL), text);

		return text.substring(0, text.length() - NL.length());
	}

	/** The event time of the first timestamp of a compact line's values. */
	private static long time(String template, String values) throws IOException {

		Template parsed = new TemplateLexer(CONTEXT, "h").tokenize(template);
		EventLexer lexer = new EventLexer(CONTEXT, parsed);

		lexer.decode(values);

		return lexer.timestampEpoch(0);
	}

	@Test
	void auditRecordIsWrittenBack() {
		assertEquals("type=SYSCALL msg=audit(1723042099.699:13369): arch=c000003e syscall=59",
			decode("~Q<C9E6L.6o,1723042099.699,13369,c000003e,59"));
	}

	@Test
	void logfmtLineIsWrittenBack() {
		assertEquals("ts=1705313472.123 msg=hello", decode("~IE{DdzvP[H,1705313472.123"));
	}

	@Test
	void fractionDigitsAndZerosAreKept() {
		assertEquals("ts=1723042099.600 msg=hello", decode("~IE{DdzvP[H,1723042099.600"));
		assertEquals("ts=1723042099.6 msg=hello", decode("~IE{DdzvP[H,1723042099.6"));
		assertEquals("{\"ts\":1705313472.022663733,\"msg\":\"x\"}", decode("~json,1705313472.022663733,x"));
	}

	@Test
	void eventTimeIsSecondsAndFraction() throws IOException {
		assertEquals(Instant.parse("2024-08-07T14:48:19.699Z").toEpochMilli(),
			time(AUDIT, "1723042099.699,13369,c000003e,59"));
		assertEquals(Instant.parse("2024-01-15T10:11:12.123Z").toEpochMilli(), time(LOGFMT, "1705313472.123"));
		assertEquals(Instant.parse("2024-08-07T14:48:19.600Z").toEpochMilli(), time(LOGFMT, "1723042099.6"));
		assertEquals(1705313472022663733L, time(JSON, "1705313472.022663733,x"));
		assertEquals(1705313472022600000L, time(JSON, "1705313472.0226,x"));
	}

	@Test
	void damagedValueGivesNoTimeAndKeepsItsText() throws IOException {
		assertEquals(-1L, time(LOGFMT, "17230420.99"));
		assertEquals(-1L, time(LOGFMT, "1723042099"));
		assertEquals("ts=17230420.99 msg=hello", decode("~IE{DdzvP[H,17230420.99"));
	}

	@Test
	void millisecondsWithAFractionGiveTheirTimeInNanoseconds() throws IOException {
		assertEquals(1705313472123456000L, time(MILLIS, "1705313472123.456,268,c000003e,59"));
		assertEquals(1705313472123456789L, time(MILLIS, "1705313472123.456789123,268,c000003e,59"));
	}
}
