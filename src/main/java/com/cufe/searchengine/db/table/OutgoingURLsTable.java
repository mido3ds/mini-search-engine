package com.cufe.searchengine.db.table;

import com.cufe.searchengine.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OutgoingURLsTable {
	@Autowired
	private JdbcTemplate jdbcTemplate;

    public void insertLink(String srcURL, String outURL) throws Exception {
        String query = "INSERT OR IGNORE INTO outgoing_urls (srcURL,outURL) VALUES (?,?);";
        DBUtils.waitLock(100, () -> jdbcTemplate.update(query, srcURL, outURL));
    }

    public List<String> selectUrls() throws Exception {
		String query = "SELECT url FROM documents;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForList(query, String.class));
	}

	public List<String> selectIncomingURLs(String url) throws Exception {
        String query = "SELECT srcURL FROM outgoing_urls WHERE outURL = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForList(query, String.class, url));
	}

    public Hashtable<String, List<String>> selectAllIncomingURLs() throws Exception {
        Hashtable<String, List<String>> incomingURLs = new Hashtable<String, List<String>>();
        List<String> urls = selectUrls();
        for (String url : urls) {
            incomingURLs.put(url, selectIncomingURLs(url));
        }
        return incomingURLs;
	}

    public Integer getOutgoingCount(String url) throws Exception {
        String query = "SELECT COUNT(*) FROM outgoing_urls WHERE srcURL = ?;";
		return DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(query, Integer.class, url));
	}

    public Hashtable<String, Integer> getAllOutgoingCount() throws Exception {
        Hashtable<String, Integer> outgoingCount = new Hashtable<String, Integer>();
        List<String> urls = selectUrls();
        for (String url : urls) {
            outgoingCount.put(url, getOutgoingCount(url));
        }
        return outgoingCount;
    }
}
