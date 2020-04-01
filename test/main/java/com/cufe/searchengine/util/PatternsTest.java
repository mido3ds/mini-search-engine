package com.cufe.searchengine.util;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PatternsTest {
	@Test
	public void extractHtmlTitle() {
		String html = "<!doctype html>\n" +
			"<html lang=\"en-US\">\n" +
			"<head>\n" +
			"\t<meta charset=\"UTF-8\">\n" +
			"\t<meta name=viewport content=\"width=device-width, initial-scale=1\">\n" +
			"\t<title>What is Search Engine Indexing &amp; How Does it Work? - DeepCrawl</title>\n" +
			"\n" +
			"<!-- This site is optimized with the Yoast SEO plugin v11.9 - https://yoast.com/wordpress/plugins/seo/ -->\n" +
			"<meta name=\"description\" content=\"Let’s take a look at the indexing process that search engines use to store information about web pages, enabling them to quickly return relevant, high quality results.\"/>";

		String nonTitle = "<!doctype html>\n" +
			"<html lang=\"en-US\">\n" +
			"<head>\n" +
			"\t<meta charset=\"UTF-8\">\n" +
			"\t<meta name=viewport content=\"width=device-width, initial-scale=1\">\n" +
			"\tWhat is Search Engine Indexing &amp; How Does it Work? - DeepCrawl\n" +
			"\n" +
			"<!-- This site is optimized with the Yoast SEO plugin v11.9 - https://yoast.com/wordpress/plugins/seo/ -->\n" +
			"<meta name=\"description\" content=\"Let’s take a look at the indexing process that search engines use to store information about web pages, enabling them to quickly return relevant, high quality results.\"/>";

		assertEquals("What is Search Engine Indexing &amp; How Does it Work? - DeepCrawl", Patterns.extractHtmlTitle(html));
		assertEquals("", Patterns.extractHtmlTitle(nonTitle));
	}

	@Test
	public void extractWebsite() {
		final String expected = "https://wikipedia.org";
		final String[] inputs = new String[]{
			"https://wikipedia.org/asdfa/dafsd/a?q=aa%A",
			"https://www.wikipedia.org/asdfa/dafsd/lll?q=aa%A",
			"https://ar.wikipedia.org/asdfa/dafsd",
			"https://en.wikipedia.org/fadfb/ads/"
		};

		for (String input : inputs) {
			assertEquals(expected, Patterns.extractWebsite(input));
		}
	}

	@Test
	public void couldBeHtml() {
		assertTrue(Patterns.couldBeHtml("http://www.git.com/adasd.html"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com/adasd.asp"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com/adasd"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com/"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com/adasd.$$$"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com/*"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com"));
		assertTrue(Patterns.couldBeHtml("http://www.git.com/https://www.github.com/"));

		assertFalse(Patterns.couldBeHtml("adasd.jpg"));
		assertFalse(Patterns.couldBeHtml("https://example.com/.gif"));
		assertFalse(Patterns.couldBeHtml("https://example.com/adasd.habal"));
		assertFalse(Patterns.couldBeHtml("https://example.com/adasd.mp4"));
		assertFalse(Patterns.couldBeHtml("https://example.com/om/dasd/adasd.css"));
	}

	@Test
	public void testExtractURLsSimple() {
		String test = "<li id=\"footer-places-privacy\">" +
			"<a href=\"https://foundation.wikimedia.org/wiki/Privacy_policy\" class=\"extiw\" " +
			"title=\"wmf:Privacy policy\">Privacy policy</a></li>";

		assertArrayEquals(new String[]{"https://foundation.wikimedia.org/wiki/Privacy_policy"},
			Patterns.extractUrls(test, ""));
	}

	@Test
	public void testExtractURLsMultiple() {
		String test = "<li id=\"footer-places-privacy\">" +
			"<a href=\"https://foundation.wikimedia.org/wiki/Privacy_policy\"> class=\"extiw\" " +
			"title=\"wmf:Privacy policy\">Privacy policy</a></li>" +
			"<li id=\"footer-places-developers\"><a href=\"https://www.mediawiki.org/wiki/Special:MyLanguage/How_to_contribute\">Developers</a></li>";

		assertArrayEquals(new String[]{"https://foundation.wikimedia.org/wiki/Privacy_policy",
				"https://www.mediawiki.org/wiki/Special:MyLanguage/How_to_contribute"},
			Patterns.extractUrls(test, ""));
	}

	@Test
	public void testExtractURLsRelative() {
		String test = "\t\t<li id=\"footer-places-about\">" +
			"<a href=\"wiki/Wikipedia:About\" title=\"Wikipedia:About\">About Wikipedia</a></li>";

		assertArrayEquals(new String[]{"https://wikipedia.org/wiki/Wikipedia:About"},
			Patterns.extractUrls(test, "https://wikipedia.org/"));
	}

	@Test
	// TODO: 1/4/20 just use a library
	public void testExtractURLsAbsNoProto() {
		String test = "\t\t<li id=\"footer-places-about\">" +
			"<a href=\"www.wikipedia.org/wiki/Wikipedia:About\" title=\"Wikipedia:About\">About Wikipedia</a></li>";

		assertArrayEquals(new String[]{"https://www.wikipedia.org/wiki/Wikipedia:About"},
			Patterns.extractUrls(test, "https://wikipedia.org/"));
	}
}
