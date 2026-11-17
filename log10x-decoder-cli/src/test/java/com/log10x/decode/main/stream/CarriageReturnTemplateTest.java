package com.log10x.decode.main.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;

/**
 * A multi-line event's template joins its lines with the break each line was
 * read with: LF, CR LF, or a CR alone. A CR is template text and a delimiter: a
 * value slot or a timestamp right before it ends there. The template is the shape the engine writes for a
 * Windows Security event with both kinds of CR.
 */
class CarriageReturnTemplateTest {

	private static final TokenizeContext CONTEXT = TokenizeContext.create(new TokenOptions(),
		new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

	@Test
	void slotsBeforeALoneCrAndBeforeCrLfAreValues() {

		String template = "<Data Name='AccessList'>%%$\r\t\t\t\t%%$\r\n\t\t\t\t<//Data>\n<Data Name='AccessMask'>$<//Data>";

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT, new TemplateProvider(CONTEXT, Collections.singletonMap("w", template)),
			new ByteArrayInputStream("~w,4432,4433,0x1\n".getBytes(StandardCharsets.UTF_8)), out));

		assertEquals("<Data Name='AccessList'>%%4432\r\t\t\t\t%%4433\r\n\t\t\t\t</Data>\n<Data Name='AccessMask'>0x1</Data>"
			+ System.lineSeparator(), new String(out.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	void timestampRightBeforeACr() {

		java.util.Map<String, String> templates = new java.util.HashMap<>();

		templates.put("crlf", "$ : $(yyyy-MM-dd'T'HH:mm:ss'Z')\r\nVersion : $");
		templates.put("cr", "$ : $(yyyy-MM-dd'T'HH:mm:ss'Z')\rVersion : $");

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT, new TemplateProvider(CONTEXT, templates),
			new ByteArrayInputStream("~crlf,1488916587000,Last,4\n~cr,1488916587000,Last,4\n".getBytes(StandardCharsets.UTF_8)), out));

		String nl = System.lineSeparator();

		assertEquals("Last : 2017-03-07T19:56:27Z\r\nVersion : 4" + nl + "Last : 2017-03-07T19:56:27Z\rVersion : 4" + nl,
			new String(out.toByteArray(), StandardCharsets.UTF_8));
	}
}
