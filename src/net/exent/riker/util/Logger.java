/*
 *  The MIT License
 *
 *  Copyright 2010 Vidar Wahlberg <canidae@exent.net>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package net.exent.riker.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A wrapper and convenience class for logging.
 * This class simplifies logging by checking if log level is enabled for you.
 * It also optimize logging by concatenating text using a StringBuilder.
 * The downside is that when logging exceptions you'll have to specify the exception before the text.
 */
public class Logger {

	/**
	 * A reference to the Logger library.
	 */
	private java.util.logging.Logger logger;

	static {
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new LoggerFormatter());
		consoleHandler.setLevel(LoggerLevel.NOTICE);
		for (Handler h : java.util.logging.Logger.getLogger("").getHandlers())
			java.util.logging.Logger.getLogger("").removeHandler(h);
		java.util.logging.Logger.getLogger("").addHandler(consoleHandler);
		java.util.logging.Logger.getLogger("").setLevel(Level.ALL);
	}

	/**
	 * Default constructor with parameter for the class to log for.
	 * @param clazz The class we're logging for.
	 */
	public Logger(Class clazz) {
		logger = java.util.logging.Logger.getLogger(clazz.getName());
	}

	/**
	 * Log at trace level.
	 * @param data data to be logged.
	 */
	public void trace(Object... data) {
		log(LoggerLevel.TRACE, null, data);
	}

	/**
	 * Log at trace level.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	public void trace(Throwable t, Object... data) {
		log(LoggerLevel.TRACE, t, data);
	}

	/**
	 * Log at debug level.
	 * @param data data to be logged.
	 */
	public void debug(Object... data) {
		log(LoggerLevel.DEBUG, null, data);
	}

	/**
	 * Log at debug level.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	public void debug(Throwable t, Object... data) {
		log(LoggerLevel.DEBUG, t, data);
	}

	/**
	 * Log at info level.
	 * @param data data to be logged.
	 */
	public void info(Object... data) {
		log(LoggerLevel.INFO, null, data);
	}

	/**
	 * Log at info level.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	public void info(Throwable t, Object... data) {
		log(LoggerLevel.INFO, t, data);
	}

	/**
	 * Log at notice level.
	 * @param data data to be logged.
	 */
	public void notice(Object... data) {
		log(LoggerLevel.NOTICE, null, data);
	}

	/**
	 * Log at notice level.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	public void notice(Throwable t, Object... data) {
		log(LoggerLevel.NOTICE, t, data);
	}

	/**
	 * Log at warning level.
	 * @param data data to be logged.
	 */
	public void warning(Object... data) {
		log(LoggerLevel.WARNING, null, data);
	}

	/**
	 * Log at warning level.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	public void warning(Throwable t, Object... data) {
		log(LoggerLevel.WARNING, t, data);
	}

	/**
	 * Log at error level.
	 * @param data data to be logged.
	 */
	public void error(Object... data) {
		log(LoggerLevel.ERROR, null, data);
	}

	/**
	 * Log at error level.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	public void error(Throwable t, Object... data) {
		log(LoggerLevel.ERROR, t, data);
	}

	/**
	 * Helper method, calls logging library.
	 * @param level The level to log at.
	 * @param t Throwable to be logged.
	 * @param data data to be logged.
	 */
	private void log(Level level, Throwable t, Object... data) {
		if (!logger.isLoggable(level))
			return;
		logger.log(level, unwrap(data), t);
	}

	/**
	 * Helper method, concatenates an array of objects efficiently.
	 * @param data Array to be concatenated as a String.
	 * @return A concatenated String containing the data from the supplied array.
	 */
	private String unwrap(Object... data) {
		StringBuilder sb = new StringBuilder(256);
		for (Object d : data)
			sb.append(d);
		return sb.toString();
	}

	/**
	 * Custom log levels for Java logging as the standard log levels are really weird.
	 */
	private static final class LoggerLevel extends Level {

		/**
		 * Error log level.
		 */
		public static final LoggerLevel ERROR = new LoggerLevel("ERROR", 1050);
		/**
		 * Notice log level.
		 */
		public static final LoggerLevel NOTICE = new LoggerLevel("NOTICE", 850);
		/**
		 * Debug log level.
		 */
		public static final LoggerLevel DEBUG = new LoggerLevel("DEBUG", 650);
		/**
		 * Trace log level.
		 */
		public static final LoggerLevel TRACE = new LoggerLevel("TRACE", 550);

		/**
		 * Private constructor to prevent instantiation.
		 * @param name name of the log level
		 * @param value value of the log level
		 */
		private LoggerLevel(String name, int value) {
			super(name, value);
		}
	}

	/**
	 * A Formatter for our logging.
	 */
	private static class LoggerFormatter extends Formatter {

		/**
		 * String format when logging a message without a throwable.
		 */
		private static final String WITHOUT_THROWABLE = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL [%2$-7.7s] [Thread-%3$-3.3s] [%4$-50.50s] %5$s%n";
		/**
		 * String format when logging a message with a throwable.
		 */
		private static final String WITH_THROWABLE = WITHOUT_THROWABLE + "%6$s";
		/**
		 * The newline character for this system.
		 */
		private static final String NEWLINE = System.getProperty("line.separator");

		/**
		 * Format a LogRecord.
		 * @param record The LogRecord to format.
		 * @return The formatted LogRecord.
		 */
		@Override
		public String format(LogRecord record) {
			if (record.getThrown() != null) {
				StringBuilder sb = new StringBuilder(1024);
				sb.append(record.getThrown()).append(NEWLINE);
				for (StackTraceElement ste : record.getThrown().getStackTrace())
					sb.append("        at ").append(ste).append(NEWLINE);
				return String.format(WITH_THROWABLE, System.currentTimeMillis(), record.getLevel().getName(), record.getThreadID(), record.getLoggerName(), record.getMessage(), sb);
			} else {
				return String.format(WITHOUT_THROWABLE, System.currentTimeMillis(), record.getLevel().getName(), record.getThreadID(), record.getLoggerName(), record.getMessage());
			}
		}
	}
}
