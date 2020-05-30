package com.cufe.searchengine.db.table;

import com.cufe.searchengine.crawler.Document;
import com.cufe.searchengine.server.model.QueryResult;
import com.cufe.searchengine.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Hashtable;

@Component
public class DocumentsTable {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Integer size() throws Exception {
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents;", Integer.class));
	}

	public void replace(String url, String content, long timeMillis, int counter, String pubDate, String countryCode, boolean isImage) throws Exception {
		Integer rows = DBUtils.waitLock(100,
			() -> jdbcTemplate.update("REPLACE INTO documents(url, content, timeMillis, counter, pubDate, countryCode, isImage) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?);", url, content, timeMillis, counter, pubDate, countryCode, isImage? 1:0)
		);

		if (rows == null || rows != 1) {
			throw new SQLException("couldn't replace into documents url=" + url);
		}
	}

	public void selectUrlTime(RowMapper<Void> mapper) throws Exception {
		DBUtils.waitLock(100,
			() -> jdbcTemplate.query(
				"SELECT url, timeMillis FROM documents;", mapper
			)
		);
	}

	public int getPragmaUserVersion() throws Exception {
		return DBUtils.waitLock(100,
			() -> jdbcTemplate.queryForObject("PRAGMA user_version;", Integer.class));
	}

	public void setPragmaUserVersion(int counter) throws Exception {
		DBUtils.waitLock(100, () -> jdbcTemplate.update("PRAGMA user_version = ?;", counter));
	}

	public List<String> selectUrlsNotThisCounter(int counter) throws Exception {
		return DBUtils.waitLock(100, () -> jdbcTemplate.query("SELECT url FROM documents WHERE counter != ?;",
			(row, i) -> row.getString(1), counter));
	}

	public List<String> selectUrlsThisCounter(int counter) throws Exception {
		return DBUtils.waitLock(100, () -> jdbcTemplate.query("SELECT url FROM documents WHERE counter = ?;",
			(row, i) -> row.getString(1), counter));
	}

	public int updateIndexTime(List<Document> documents) throws Exception {
		StringBuilder builder = new StringBuilder("UPDATE documents SET indexTimeMillis = ? WHERE ROWID in (");
		for (int i = 0; i < documents.size() - 1; i++) {
			builder.append(documents.get(i).getRowID()).append(",");
		}
		builder.append(documents.get(documents.size() - 1).getRowID()).append(");");

		int rows = DBUtils.waitLock(100, () -> jdbcTemplate.update(builder.toString(), System.currentTimeMillis()));
		if (rows != documents.size()) {
			throw new RuntimeException("should have updated " + documents.size());
		}
		return rows;
	}

	public List<Document> selectAll(int maxDocumentsPerIteration) throws Exception {
		return DBUtils.waitLock(100, () -> jdbcTemplate.query(String.format("SELECT content, url, timeMillis, rank, wordCount, indexTimeMillis, ROWID, " +
				"pubDate, countryCode FROM documents WHERE indexTimeMillis < timeMillis LIMIT %d;", maxDocumentsPerIteration),
			(row, i) -> new Document(row.getString(1), row
				.getString(2), row.getLong(3), row.getFloat(4), row.getInt(5), row.getString(8), row.getString(9)).indexTimeMillis(row.getLong(6))
				.rowID(row.getLong(7))));
	}

	public List<QueryResult> selectContentUrlLikePhrases(List<String> phrases) throws Exception {
		Optional<String> reducedLikes = phrases.stream()
			.map(String::toLowerCase)
			.distinct()
			.map(p -> MessageFormat.format("content LIKE \"%{0}%\"", p))
			.reduce((s, s2) -> MessageFormat.format("{0} OR {1}", s, s2));

		if (!reducedLikes.isPresent()) {
			return new ArrayList<>();
		}

		String query = "SELECT content, url rank, wordCount, pubDate, countryCode FROM documents WHERE " + reducedLikes.get() + ";";

		return DBUtils.waitLock(100, () -> jdbcTemplate.query(query, (row, i) -> new Document(row.getString(1),
			row.getString(2), 0, row.getFloat(3), row.getInt(4), row.getString(5), row.getString(6)))
			.stream()
			.map(d -> new QueryResult().title(d.getTitle())
				.snippet(d.getSnippet(phrases))
				.link(d.getUrl()))
			.collect(Collectors.toList()));
	}

	public List<Document> selectContentUrlSorted(List<String> keywords) throws Exception {
		StringBuilder builder = new StringBuilder("SELECT content, url, rank, wordCount, pubDate, countryCode FROM documents d " + "INNER JOIN " +
			"keywords_documents kd ON d.ROWID = kd.docID " + "INNER JOIN " + "keywords k ON k.ROWID = kd.wordID AND k.word in (");
		for (int i = 0; i < keywords.size(); i++) {
			builder.append("?");
			if (i != keywords.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(");");

		return DBUtils.waitLock(100, () -> jdbcTemplate.query(builder.toString(),
			(row, i) -> new Document(row.getString(1), row
				.getString(2), 0, row.getFloat(3), row.getInt(4), row.getString(5), row.getString(6)), keywords.toArray()));
	}

	public Float selectURLRank(String url) throws Exception {
		String query = "SELECT rank FROM documents WHERE url = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, Float.class, url));
	}

	public List<String> selectUrls() throws Exception {
		String query = "SELECT url FROM documents;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForList(query, String.class));
	}

	public Hashtable<String, Float> selectAllURLRanks() throws Exception {
		Hashtable<String, Float> urlRanks = new Hashtable<String, Float>();
		List<String> urls = selectUrls();
		for (String url : urls) {
			urlRanks.put(url, selectURLRank(url));
		}
		return urlRanks;
	}

	public void updateAllURLRanks(Hashtable<String, Float> urlRanks) throws Exception {
		urlRanks.forEach((k, v) -> {
			try {
				DBUtils.waitLock(100, () -> jdbcTemplate.update("UPDATE documents SET rank = (?) WHERE url = (?);", v, k));
			} catch (Exception e) {
				throw new RuntimeException("URL rank update failed");
			}
		});
	}

	public void updateURLWordCount(String url, Integer count) throws Exception {
		DBUtils.waitLock(100, () -> jdbcTemplate.update("UPDATE documents SET wordCount = (?) WHERE url = (?);", count, url));
	}

	public Integer selectWordCount(String url) throws Exception {
		String query = "SELECT wordCount FROM documents WHERE url = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, Integer.class, url));
	}

	public String selectURLPubDate(String url) throws Exception {
		String query = "SELECT pubDate FROM documents WHERE url = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, String.class, url));
	}

	public String selectURLCountryCode(String url) throws Exception {
		String query = "SELECT countryCode FROM documents WHERE url = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, String.class, url));
	}
}
