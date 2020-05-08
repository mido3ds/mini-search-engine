package com.cufe.searchengine.db.table;

import com.cufe.searchengine.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UrlStoreQueueTable {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void insert(String[] store) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO urlstore_queue VALUES");
		for (int i = 0; i < store.length - 1; i++) {
			sql.append("(?),");
		}
		sql.append("(?);");
		DBUtils.waitLock(100, () -> jdbcTemplate.update(sql.toString(), store));
	}

	public void clean() throws Exception {
		DBUtils.waitLock(100, () -> {
			jdbcTemplate.execute("DELETE FROM urlstore_queue;");
			return null;
		});
	}

	public List<String> selectUrls() throws Exception {
		return DBUtils.waitLock(100,
				() -> jdbcTemplate.queryForList("SELECT url FROM urlstore_queue;", String.class)
		);
	}
}
