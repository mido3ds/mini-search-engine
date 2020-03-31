package com.cufe.searchengine.indexer;

import com.cufe.searchengine.util.Stemmer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeywordsExtractor {
	private static final Pattern NON_WORD = Pattern.compile("([^\\w\\s]|\\d)+");
	private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
	private static final Pattern BIG_WORDS = Pattern.compile("([\\w]{15,})");
	private static final Pattern HTML_TAGS = Pattern.compile("</?(!doctype html|a|abbr|address|area|article|aside" +
		"|audio|b|base|basefont|bdi|bdo|big|blockquote|body" + "|br" + "|button|canvas|caption|center|cite" + "|code" + "|col|colgroup|data|datalist|dd|del|details|dfn" + "|dialog|dir" + "|div" + "|dl|dt|em|embed|fieldset" + "|figcaption|figure|font|footer|form|frame|frameset|h1|head|header" + "|hr|html|i" + "|iframe|img" + "|input|ins|kbd|label|legend|li|link|main|map|mark|meta|meter|nav|noframes" + "|noscript" + "|object|ol|optgroup|option|output|p|param|picture|pre|progress|q|rp|rt|ruby|s|samp|" + "script|section" + "|select|small|source|span|strike|strong|sub|summary|sup|svg|table|tbody|td|template|" + "textarea|tfoot" + "|th|thead|time|title|tr|track|tt|u|ul|var|video|wbr) ?[^>]*>");
	private static final Pattern CSS = Pattern.compile("<style ?[\\w\\W]*</style>");
	private static final Pattern STOP_WORDS = Pattern.compile("\\b[^\\w -]*(i|me|my|myself|we|our|ours|ourselves" +
		"|you|you're|you've|you'll|you'd|your|yours|yourself"
		+ "|yourselves|he|him|his|himself|she" + "|she's|her"
		+ "|hers|herself|it|it's|its|itself|they|them|their" + "|theirs" + "|themselves|what" + "|which|who|whom" + "|this|that|that'll|these|those|am|is|are|was|were" + "|be|been" + "|being" + "|have|has|had|having|do" + "|does|did|doing|a|an|the|and|but|if|or|because|as" + "|until|while|of|at|by|for" + "|with|about|against|between|into|through|during|before|after" + "|above|below|to|from|up|down|in|out|on" + "|off|over|under|again|further|then|once|here" + "|there|when|where|why|how|all|any|both|each|few|more" + "|most|other|some|such|no|nor" + "|not|only|own|same|so|than|too|very|s|t|can|will|just|don|don't|should" + "|should've" + "|now|d|ll|m|o|re|ve|y|ain|aren|aren't|couldn|couldn't|didn|didn't|doesn|doesn't" + "|hadn" + "|hadn't|hasn|hasn't|haven|haven't|isn|isn't|ma|mightn|mightn't|mustn|mustn't" + "|needn|needn't|shan|shan" + "'t|shouldn|shouldn't|wasn|wasn't|weren" + "|weren't|won|won't|wouldn|wouldn't)[^\\w -]*\\b");

	private String text;

	public KeywordsExtractor(String text) {
		this.text = text;
	}

	/**
	 * extract keywords from html document
	 *
	 * @param html to extract keywords from
	 * @return list of keywords with size <= {@code maxKeywords}
	 */
	public static List<String> extractFromHtml(String html) {
		return new KeywordsExtractor(html).toLower()
			.filterCSS()
			.filterHtmlTags()
			.filterStopWords()
			.filterNonText()
			.filterExcessiveWhitespace()
			.filterBigWords()
			.split()
			.map(Stemmer::stem)
			.distinct()
			.filter((s) -> !s.equals("") && !s.equals(" "))
			.collect(Collectors.toList());
	}

	/**
	 * extract keywords from search query
	 *
	 * @param query to extract keywords from
	 * @return list of keywords with size <= {@code maxKeywords}
	 */
	public static List<String> extractFromQuery(String query) {
		return new KeywordsExtractor(query).toLower()
			.filterNonText()
			.filterExcessiveWhitespace()
			.split()
			.map(Stemmer::stem)
			.distinct()
			.filter((s) -> !s.equals("") && !s.equals(" "))
			.collect(Collectors.toList());
	}

	private KeywordsExtractor toLower() {
		text = text.toLowerCase();
		return this;
	}

	private KeywordsExtractor filterHtmlTags() {
		text = HTML_TAGS.matcher(text).replaceAll("");
		return this;
	}

	private KeywordsExtractor filterCSS() {
		text = CSS.matcher(text).replaceAll("");
		return this;
	}

	private KeywordsExtractor filterNonText() {
		text = NON_WORD.matcher(text).replaceAll("");
		return this;
	}

	private KeywordsExtractor filterExcessiveWhitespace() {
		text = WHITE_SPACE.matcher(text).replaceAll(" ");
		return this;
	}

	private Stream<String> split() {
		return Arrays.stream(text.split("\\s"));
	}

	private KeywordsExtractor filterStopWords() {
		text = STOP_WORDS.matcher(text).replaceAll("");
		return this;
	}

	private KeywordsExtractor filterBigWords() {
		text = BIG_WORDS.matcher(text).replaceAll("");
		return this;
	}
}
