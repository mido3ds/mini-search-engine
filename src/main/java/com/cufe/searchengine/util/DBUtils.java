package com.cufe.searchengine.util;

import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.util.concurrent.Callable;

public class DBUtils {
	public static <T> T waitLock(int timeMillis, Callable<T> callback) throws Exception {
		while (true) {
			try {
				return callback.call();
			} catch (CannotGetJdbcConnectionException e) {
				try {
					Thread.sleep(timeMillis);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	public static void waitLock(int timeMillis, Runnable callback) {
		while (true) {
			try {
				callback.run();
			} catch (CannotGetJdbcConnectionException e) {
				try {
					Thread.sleep(timeMillis);
				} catch (InterruptedException ignored) {
				}

				continue;
			}

			break;
		}
	}
}
