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
 * The template writes '/' as "//" and '$' as "/$". A '$' in the source text
 * right after a '/' is therefore "///$": an escaped '/' and an escaped '$',
 * never a value slot or the start of a timestamp. The PowerShell line is the
 * shape of a script block the engine encoded.
 */
class EscapedDollarTest {

	private static final String NL = System.lineSeparator();

	private static final TokenizeContext CONTEXT = TokenizeContext.create(new TokenOptions(),
		new TimestampOptions("UTC"), new TemplateEncodeOptions(), new EventEncodeOptions('~', ','));

	private static String decode(String line) {

		Map<String, String> templates = new HashMap<>();

		templates.put("ps", "$(yyyy-MM-dd'T'HH:mm:ss.SSS'Z') /$proxy = \"http://///$(/$json.connectionConfig.$):/$(/$json.connectionConfig.port)\" pid $");
		templates.put("paths", "copy ///$ to ////$ and ///$1");

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		assertTrue(StreamDecoder.decode(CONTEXT, new TemplateProvider(CONTEXT, templates),
			new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8)), out));

		String text = new String(out.toByteArray(), StandardCharsets.UTF_8);

		return text.substring(0, text.length() - NL.length());
	}

	@Test
	void dollarAfterSlashInAScriptIsText() {
		assertEquals("2023-06-16T12:10:30.521Z $proxy = \"http://$($json.connectionConfig.host):$($json.connectionConfig.port)\" pid 4592",
			decode("~ps,1686917430521,host,4592"));
	}

	@Test
	void slashRunsBeforeDollar() {
		assertEquals("copy /$ to //tmp and /$1", decode("~paths,tmp"));
	}
}
