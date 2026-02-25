package com.log10x.decode.main;

import com.log10x.decode.main.picocli.PicocliStreamDecoder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * 
 * @author Dor Levi
 *
 */
@Command(name = "log10x-decode", mixinStandardHelpOptions = true, version = "log10x-decode 0.9.0", description = "Decodes Log10x encoded files")
public class DecoderMain {

	/**
	 * Main entry point
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		int exitCode = new CommandLine(new PicocliStreamDecoder()).execute(args);
		System.exit(exitCode);
	}
}
