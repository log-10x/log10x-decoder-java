package com.log10x.decode.main.picocli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import picocli.CommandLine;

/**
 * The command line as {@code DecoderMain} runs it, with standard output and
 * standard error captured: standard output carries only the decoded events,
 * diagnostics go to standard error, and the exit status is 0 when every line
 * was written (decoded, or as read when its template is missing), 1 when the
 * input or the templates file cannot be read and 2 when the arguments are wrong.
 */
class PicocliStreamDecoderTest {

	private static final String NL = System.lineSeparator();

	@TempDir
	Path dir;

	private String out;

	private String err;

	private int run(String... args) {

		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream errBytes = new ByteArrayOutputStream();

		PrintStream previousOut = System.out;
		PrintStream previousErr = System.err;

		int exitCode;

		try {

			System.setOut(new PrintStream(outBytes, true));
			System.setErr(new PrintStream(errBytes, true));

			exitCode = new CommandLine(new PicocliStreamDecoder()).execute(args);

		} finally {

			System.setOut(previousOut);
			System.setErr(previousErr);
		}

		this.out = new String(outBytes.toByteArray(), Charset.defaultCharset());
		this.err = new String(errBytes.toByteArray(), Charset.defaultCharset());

		return exitCode;
	}

	private Path write(String name, String text) throws IOException {
		return Files.write(this.dir.resolve(name), text.getBytes(Charset.defaultCharset()));
	}

	private String[] args(Path templates, Path input) {

		return new String[] { "-t", templates.toString(), "-f", input.toString(),
			"-d", ",", "-p", "~", "-z", "UTC" };
	}

	@Test
	void missingTemplateIsReportedOnStandardErrorOnly() throws IOException {

		Path templates = write("templates.json", "{\"templateHash\":\"h1\",\"template\":\"user $ logged in\"}\n");
		Path input = write("encoded.log", "~h1,alice\n~h2,carol\nnot encoded\n~h1,bob\n");

		assertEquals(0, run(args(templates, input)));

		assertEquals("user alice logged in" + NL + "~h2,carol" + NL + "not encoded" + NL +
			"user bob logged in" + NL, this.out);

		assertTrue(this.err.contains("Missing template for h2"), this.err);
	}

	@Test
	void everyTemplatePresentWritesNothingToStandardError() throws IOException {

		Path templates = write("templates.json", "{\"templateHash\":\"h1\",\"template\":\"user $ logged in\"}\n");
		Path input = write("encoded.log", "~h1,alice\n");

		assertEquals(0, run(args(templates, input)));
		assertEquals("user alice logged in" + NL, this.out);
		assertEquals("", this.err);
	}

	@Test
	void unreadableInputExitsWithOne() throws IOException {

		Path templates = write("templates.json", "{\"templateHash\":\"h1\",\"template\":\"user $ logged in\"}\n");

		assertEquals(1, run(args(templates, this.dir.resolve("absent.log"))));
		assertEquals("", this.out);
	}

	@Test
	void missingTemplatesArgumentExitsWithTwo() {

		assertEquals(2, run("-f", "encoded.log"));
		assertEquals("", this.out);
	}

	@Test
	void unreadableTemplatesFileExitsWithOne() throws IOException {

		Path input = write("encoded.log", "~h1,alice\n");

		assertEquals(1, run(args(this.dir.resolve("absent.json"), input)));
		assertEquals("", this.out);
		assertTrue(this.err.contains("Cannot read the templates file"), this.err);

		assertEquals(1, run(args(this.dir, input)));
		assertEquals("", this.out);
	}

	/**
	 * A line of the templates file that is not a template entry is skipped with
	 * a warning that names it, and the templates after it are still loaded.
	 */
	@Test
	void malformedTemplateLineIsSkippedAndTheRestIsRead() throws IOException {

		Path templates = write("templates.json",
			"{\"templateHash\":\"h1\",\"template\":\"user $ logged in\"}\n" +
			"not a template\n" +
			"\n" +
			"{\"templateHash\":\"h2\",\"template\":\"order $ shipped\"}\n" +
			"{\"templateHash\":\"h9\",\"templ\n" +
			"{\"templateHash\":\"h3\",\"template\":\"item $ sold\"}\n");
		Path input = write("encoded.log", "~h1,alice\n~h2,o-1\n~h3,i-7\n");

		assertEquals(0, run(args(templates, input)));

		assertEquals("user alice logged in" + NL + "order o-1 shipped" + NL + "item i-7 sold" + NL, this.out);

		assertTrue(this.err.contains("Skipped line 2 of"), this.err);
		assertTrue(this.err.contains("Skipped line 5 of"), this.err);
		assertTrue(!this.err.contains("Skipped line 3 of"), this.err);
	}
}
