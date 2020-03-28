package com.cufe.searchengine.db;

import org.springframework.context.ApplicationEvent;

public class DBInitializedEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public DBInitializedEvent(Object source) {
		super(source);
	}
}
