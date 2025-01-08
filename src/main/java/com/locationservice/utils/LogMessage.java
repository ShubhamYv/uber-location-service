package com.locationservice.utils;

import org.apache.logging.log4j.Logger;

public final class LogMessage {

	private static final ThreadLocal<String> LOG_MESSAGE = new ThreadLocal<>();

	private LogMessage() {
		// Prevent instantiation
	}

	public static void setLogMessagePrefix(final String logMessagePrefix) {
		if (logMessagePrefix != null && !logMessagePrefix.isEmpty()) {
			LOG_MESSAGE.set(logMessagePrefix + " : ");
		} else {
			LOG_MESSAGE.set("");
		}
	}

	public static void close() {
		LOG_MESSAGE.remove();
	}

	private static String getPrefix() {
		return LOG_MESSAGE.get() != null ? LOG_MESSAGE.get() : "";
	}

	public static void log(final Logger logger, final Object object) {
		if (logger != null) {
			logger.info(getPrefix() + object);
		}
	}

	public static void log(final Logger logger, final String... messages) {
		if (logger != null) {
			StringBuilder sb = new StringBuilder(getPrefix());
			for (String message : messages) {
				sb.append(message).append(" ");
			}
			logger.info(sb.toString().trim());
		}
	}

	public static void warn(final Logger logger, final Object object) {
		if (logger != null) {
			logger.warn(getPrefix() + object);
		}
	}

	public static void debug(final Logger logger, final Object object) {
		if (logger != null) {
			logger.debug(getPrefix() + object);
		}
	}

	public static void info(final Logger logger, final Object object) {
		if (logger != null) {
			logger.info(getPrefix() + object);
		}
	}

	public static void logException(final Logger logger, final Exception e) {
		if (logger != null) {
			logger.error(getPrefix() + "Exception occurred", e);
		}
	}

	public static void trace(final Logger logger, final Object object) {
		if (logger != null) {
			logger.trace(getPrefix() + object);
		}
	}

	public static void fatal(final Logger logger, final Object object) {
		if (logger != null) {
			logger.fatal(getPrefix() + object);
		}
	}
}
