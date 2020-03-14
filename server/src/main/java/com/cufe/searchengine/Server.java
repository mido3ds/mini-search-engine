package com.cufe.searchengine;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.SQLException;
import java.util.Objects;

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

	public static void main(String[] args) throws Exception {
		new SpringApplication(Server.class).run(args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		if (arg0.length > 0 && arg0[0].equals("exitcode")) {
			throw new ExitException();
		}

//		createDB(); TODO: uncomment when scqlite_schema.sql is complete

		if (Objects.equals(env.getProperty("POPULATE_DB"), "TRUE")) {
//			populateDB(); TODO: uncomment when populate_schema.sql is complete
		}
	}

	public void createDB() {
		log.info("Creating tables");

		try {
			ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), sqliteSchemaResource);
		} catch (SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public void populateDB() {
		log.info("Populating DB with test data");

		try {
			ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), populateDBResource);
		} catch (SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	@Bean
	public WebMvcConfigurer webConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOrigins("*")
					.allowedMethods("*")
					.allowedHeaders("Content-Type");
			}
		};
	}

	static class ExitException extends RuntimeException implements ExitCodeGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public int getExitCode() {
			return 10;
		}

	}
}
