package com.cufe.searchengine.crawler;

import org.springframework.context.ApplicationEvent;

public class CrawlingFinishedEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public CrawlingFinishedEvent(Object source) {
		super(source);
	}
}
