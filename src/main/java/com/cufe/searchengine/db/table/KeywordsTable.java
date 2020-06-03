package com.cufe.searchengine.db.table;

import com.cufe.searchengine.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeywordsTable {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void insertOrIgnore(Map<String, Integer> wordFreq, long docID) throws Exception {
		List<String> words = new ArrayList<String>(wordFreq.keySet());
		final StringBuilder builder = new StringBuilder("INSERT OR IGNORE INTO keywords VALUES");
		for (int i = 0; i < words.size(); i++) {
			builder.append("(?)");

			if (i != words.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(";");

		Integer numRows = DBUtils.waitLock(100, () -> jdbcTemplate.update(builder.toString(), words.toArray()));

		final StringBuilder builder2 = new StringBuilder("SELECT ROWID FROM keywords WHERE word in (");
		for (int i = 0; i < words.size(); i++) {
			builder2.append("?");

			if (i != words.size() - 1) {
				builder2.append(",");
			}
		}
		builder2.append(");");

		List<Integer> rowids = DBUtils.waitLock(
				100, () -> jdbcTemplate.query(builder2.toString(),
						(row, i) -> row.getInt(1), words.toArray())
		);

		final StringBuilder builder3 = new StringBuilder();
		builder3.append("REPLACE INTO keywords_documents(docID, wordID, count) VALUES");
		for (int i = 0; i < rowids.size(); i++) {
			builder3.append("(").append(docID).append(",").append(rowids.get(i)).append(",").append(wordFreq.get(words.get(i))).append(")");

			if (i != rowids.size() - 1) {
				builder3.append(",");
			}
		}
		builder3.append(";");

		numRows = DBUtils.waitLock(100, () -> jdbcTemplate.update(builder3.toString()));
	}

	public Integer selectCountbyWord(String keyword) throws Exception {
		String query = "SELECT COUNT(*) FROM documents d INNER JOIN keywords_documents kd ON d.ROWID = kd.docID " + 
					"INNER JOIN keywords k ON k.ROWID = kd.wordID WHERE word = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, Integer.class, keyword));
	}

	public Integer selectWordCount(String url, String keyword) throws Exception {
		String query = "SELECT count FROM documents d INNER JOIN keywords_documents kd ON d.ROWID = kd.docID " + 
					"INNER JOIN keywords k ON k.ROWID = kd.wordID WHERE url = ? AND word = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, Integer.class, url, keyword));
	}

	public List<String> selectWordStartWith(String initString) throws Exception {
		String startString = initString + "%";
		String query = "SELECT word FROM keywords WHERE WORD LIKE ? LIMIT 10";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForList(query, String.class, startString));
	}

	public Integer size() throws Exception {
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject("SELECT COUNT(*) FROM keywords;", Integer.class));
	}
}
