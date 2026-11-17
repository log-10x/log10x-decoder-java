package com.log10x.decode.main.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * Writes the command-line tool's log to standard error.
 *
 * The decoded events go to standard output. With no configuration of its own,
 * logback writes to standard output as well, so a warning such as a missing
 * template landed inside the decoded stream. This is logback's default
 * configuration (one console appender, the same layout, the root logger at
 * DEBUG) with standard error as its target.
 *
 * logback finds this class through
 * {@code META-INF/services/ch.qos.logback.classic.spi.Configurator}. A
 * {@code logback.xml} on the class path, or {@code -Dlogback.configurationFile},
 * still takes precedence over it.
 */
public class StandardErrorLogConfigurator extends ContextAwareBase implements Configurator {

	@Override
	public void configure(LoggerContext loggerContext) {

		ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
		appender.setContext(loggerContext);
		appender.setName("console");
		appender.setTarget("System.err");

		TTLLLayout layout = new TTLLLayout();
		layout.setContext(loggerContext);
		layout.start();

		LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
		encoder.setContext(loggerContext);
		encoder.setLayout(layout);

		appender.setEncoder(encoder);
		appender.start();

		loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
	}
}
