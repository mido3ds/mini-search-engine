package com.cufe.searchengine.db.table;

import com.cufe.searchengine.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PersonsTable {
	@Autowired
	private JdbcTemplate jdbcTemplate;

    public void updatePersonCount(String name, String country) throws Exception {
        String firstQuery = "SELECT COUNT(*) FROM persons WHERE name = ? AND countryCode = ?;";
        int count = DBUtils.waitLock(100, () -> jdbcTemplate.queryForObject(firstQuery, Integer.class, name, country));
        if (count == 0) {
            // insert
            String secondQuery = "INSERT OR IGNORE INTO persons (name,countryCode) VALUES (?,?);";
            DBUtils.waitLock(100, () -> jdbcTemplate.update(secondQuery, name, country));
        } else {
            // update
            String thirdQuery = "UPDATE persons SET count = count + 1 WHERE name = ? AND countryCode = ?;";
            DBUtils.waitLock(100, () -> jdbcTemplate.update(thirdQuery, name, country));
        }
    }

    public List<String> getTopPersons(String country) throws Exception {
        String query = "SELECT name FROM persons WHERE countryCode = (?) ORDER BY count DESC LIMIT 10";
        return DBUtils.waitLock(100, () -> jdbcTemplate.queryForList(query, String.class));
    }
}
