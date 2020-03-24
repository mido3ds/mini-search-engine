package com.cufe.searchengine;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cufe.searchengine", "com.cufe.searchengine.api", "com.cufe.searchengine.configuration"})
public class Server implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(Server.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	Environment env;
	@Value("classpath:sqlite_schema.sql")
	private Resource sqliteSchemaResource;
	@Value("classpath:populate_db.sql")
	private Resource populateDBResource;
	@Value("classpath:crawler_seed")
	private Resource crawlerSeedResource;

	public static void main(String[] args) {
		new SpringApplication(Server.class).run(args);
	}

	@Override
	public void run(String... arg0) {
		createDB();

		if (Objects.equals(env.getProperty("POPULATE_DB"), "TRUE")) {
			populateDB();
		}

		Crawler.createThreads(
			Integer.parseInt(Objects.requireNonNull(env.getProperty("crawler.numThreads"))),
			jdbcTemplate, getSeedSet()
		);
	}

	private List<String> getSeedSet() {
		List<String> seedSet;
		try (InputStream resource = crawlerSeedResource.getInputStream()) {
			seedSet =
				new BufferedReader(new InputStreamReader(resource,
					StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedExecutionException(e);
		}
		return seedSet;
	}

	public void createDB() {
		log.info("Creating tables");

		try {
			ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), sqliteSchemaResource);
		} catch (SQLException e) {
			throw new UncheckedExecutionException(e);
		} catch (UncategorizedScriptException _) {
			log.warn("empty sqlite_schema.sql, ignoring it");
		}
	}

	public void populateDB() {
		log.info("Populating DB with test data");

		try {
			ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), populateDBResource);
		} catch (SQLException e) {
			throw new UncheckedExecutionException(e);
		} catch (UncategorizedScriptException _) {
			log.warn("empty populate_db.sql, ignoring it");
		}
	}
}
