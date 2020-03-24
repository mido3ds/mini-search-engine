package com.cufe.searchengine.configuration;

import com.cufe.searchengine.QueryProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class QueryProcessorConfiguration {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Bean
	QueryProcessor queryProcessor() {
		return new QueryProcessor(jdbcTemplate);
	}
}
