package com.cufe.searchengine.util;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.util.concurrent.Callable;

public class DBUtils {
	public static <T> T waitLock(int timeMillis, Callable<T> callback) throws Exception {
		while (true) {
			try {
				return callback.call();
			} catch (CannotGetJdbcConnectionException e) {
				sleep(timeMillis);
			} catch (SQLiteException e) {
				if (e.getResultCode().equals(SQLiteErrorCode.SQLITE_BUSY)) {
					sleep(timeMillis);
				} else {
					throw e;
				}
			}
		}
	}

	private static void sleep(int timeMillis) {
		try {
			Thread.sleep(timeMillis);
		} catch (InterruptedException ignored) {
		}
	}
}
