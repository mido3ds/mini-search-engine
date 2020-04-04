package com.cufe.searchengine.db.table;

import com.cufe.searchengine.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeywordsTable {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void insertOrIgnore(List<String> words, long docID) throws Exception {
		StringBuilder builder = new StringBuilder("INSERT OR IGNORE INTO keywords VALUES");
		for (int i = 0; i < words.size(); i++) {
			builder.append("(?)");

			if (i != words.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(";\n");

		builder.append("REPLACE INTO keywords_documents(docID, wordID) VALUES");
		for (int i = 0; i < words.size(); i++) {
			builder.append("(").append(docID).append(",last_insert_rowid()-").append(i).append(")");

			if (i != words.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(";");

		DBUtils.waitLock(100, () -> jdbcTemplate.update(builder.toString(), words.toArray()));
	}
}
