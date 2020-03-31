package com.cufe.searchengine.util;

import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
	private static final Pattern HTML_PATTERN = Pattern.compile("<!DOCTYPE html>|<head>|<body>|</body>|</head>");

	public static String streamToString(InputStream stream) throws IOException {
		char[] buffer = new char[1024];

		Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		StringBuilder builder = new StringBuilder();

		int chars;
		while ((chars = reader.read(buffer, 0, buffer.length)) > 0) {
			builder.append(buffer, 0, chars);
		}

		return builder.toString();
	}

	public static List<String> resourceToLines(Resource resource) throws IOException {
		return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)).lines()
			.collect(Collectors
				.toList());
	}

	public static boolean isHtml(String document) {
		return HTML_PATTERN.matcher(document).find();
	}
}
