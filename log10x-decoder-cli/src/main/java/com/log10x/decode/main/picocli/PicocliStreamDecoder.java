package com.log10x.decode.main.picocli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.main.stream.StreamDecoder;
import com.log10x.decode.main.template.TemplateProvider;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;
import picocli.CommandLine.Option;

/**
 * Wrapper class for invoking {@link StreamDecoder} nicely via Picocli
 * 
 * @author Dor Levi
 *
 */
public class PicocliStreamDecoder implements Callable<Integer> {

	@Option(names = { "-t", "--templates" }, required = true, description = "Log10x templates file used to decode with")
	private String templatesFileName;

	@Option(names = { "-f", "--file" }, description = "File to decode. If omitted, will read from stdin")
	private File inputFile;

	@Option(names = { "-o", "--output" }, description = "Output file. If omitted, will write to stdout")
	private File outputFile;

	@Override
	public Integer call() throws Exception {

		TokenizeContext context = TokenizeContext.create(
				new TokenOptions(), new TimestampOptions(),
				new TemplateEncodeOptions(), new EventEncodeOptions());

		TemplateProvider provider = TemplateProvider.fromFile(context, templatesFileName, true);

		InputStream input = (inputFile != null ? new FileInputStream(inputFile) : System.in);
		OutputStream output = (outputFile != null ? new FileOutputStream(outputFile) : System.out);

		if (!StreamDecoder.decode(context, provider, input, output)) {
			return 1;
		}

		return 0;
	}
}
