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
 * Encoded lines are separated by LF, and a value can hold a CR: the Receiver
 * keeps a forwarder record's line that is a CR alone (a Windows blank line) as
 * the value "\r". The command line reads a line up to LF, so the CR stays in the
 * line. The template is the shape the engine wrote for a Sysmon event.
 */
class CarriageReturnValueTest {

	private static final String NL = System.lineSeparator();

	private static final TokenizeContext CONTEXT = TokenizeContext.create(new TokenOptions(),
		new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

	private static String decode(String input) {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT,
			new TemplateProvider(CONTEXT, Collections.singletonMap("s", "Message=$ new process has been created.\n$\nCreator Subject:\n\tSecurity ID:\t\t$\\Administrator")),
			new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), out));

		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}

	@Test
	void valueThatIsACarriageReturn() {
		assertEquals("Message=A new process has been created.\n\r\nCreator Subject:\n\tSecurity ID:\t\tATTACKRANGE\\Administrator" + NL,
			decode("~s,A,\r,ATTACKRANGE\n"));
	}

	@Test
	void nextLineAfterTheCarriageReturnIsUnchanged() {
		assertEquals("Message=A new process has been created.\n\r\nCreator Subject:\n\tSecurity ID:\t\tX\\Administrator" + NL + "plain\rtext" + NL,
			decode("~s,A,\r,X\nplain\rtext\n"));
	}
}
