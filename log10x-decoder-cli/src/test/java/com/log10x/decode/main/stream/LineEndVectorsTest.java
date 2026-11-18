package com.log10x.decode.main.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;

/**
 * The shared reader vectors (FORMAT_SPEC S6.4): templates and encoded lines the
 * engine wrote for events read with CR LF, inside and at the end of an event, a
 * CR alone inside an event, and the same lines read with LF, with the exact text
 * each decodes to. The same file is checked by the engine's decoder, the
 * JavaScript decoder and the format's reference implementation
 * (log10x-format fixtures/line-end-vectors.json). An event that ends in a CR
 * is written as the CR and then the line separator.
 */
class LineEndVectorsTest {

	private static final String NL = System.lineSeparator();

	private static String visible(String text) {
		return text.replace("\r", "\\r").replace("\n", "\\n");
	}

	@Test
	void everyEncodedLineDecodesToItsText() throws IOException {

		JsonNode data;

		try (InputStream in = LineEndVectorsTest.class.getResourceAsStream("/line-end-vectors.json")) {
			data = new ObjectMapper().readTree(in);
		}

		TokenizeContext context = TokenizeContext.create(new TokenOptions(),
			new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

		Map<String, String> templates = new HashMap<>();

		for (Iterator<Map.Entry<String, JsonNode>> it = data.get("templates").fields(); it.hasNext();) {

			Map.Entry<String, JsonNode> entry = it.next();
			templates.put(entry.getKey(), entry.getValue().asText());
		}

		TemplateProvider provider = new TemplateProvider(context, templates);

		int events = 0;

		for (JsonNode event : data.get("events")) {

			String encoded = event.get("encoded").asText();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			assertTrue(StreamDecoder.decode(context, provider,
				new ByteArrayInputStream((encoded + "\n").getBytes(StandardCharsets.UTF_8)), out), encoded);

			String text = new String(out.toByteArray(), StandardCharsets.UTF_8);

			assertTrue(text.endsWith(NL), visible(text));
			assertEquals(visible(event.get("text").asText()), visible(text.substring(0, text.length() - NL.length())), encoded);

			events++;
		}

		assertTrue(events > 0);
	}
}
