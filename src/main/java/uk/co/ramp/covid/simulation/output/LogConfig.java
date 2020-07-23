package uk.co.ramp.covid.simulation.output;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.file.Path;

public class LogConfig {

    public static void configureLoggerRedirects(Path outPath) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        final Appender appender = FileAppender.newBuilder()
                .setName("FileLogger")
                .setLayout(PatternLayout.newBuilder().withPattern("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n").build())
                .withFileName(outPath.resolve("log").toString()).build();
        appender.start();

        // All loggers also append to the file
        for (final org.apache.logging.log4j.core.config.LoggerConfig loggerConfig : config.getLoggers().values()) {
            loggerConfig.addAppender(appender, null, null);
        }
    }
}
