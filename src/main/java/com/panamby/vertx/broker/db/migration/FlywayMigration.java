package com.panamby.vertx.broker.db.migration;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

import com.panamby.vertx.broker.config.DbConfig;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlywayMigration {

	public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {
		log.debug("DB Config: {}", dbConfig);
		return  vertx.<Void>executeBlocking(promise -> {
			// Flyway migration is bloking => uses JDBC
			execute(dbConfig);
			promise.complete();
		}).onFailure(err -> log.error("Failed to migrate db schema with error: ", err));
	}

	private static void execute(DbConfig dbConfig) {
		final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
				dbConfig.getHost(),
				dbConfig.getPort(),
				dbConfig.getDatabase()
				);
		
		log.debug("Migrating DB schema using jdbc url: {}", jdbcUrl);
		
		final Flyway flyway = Flyway.configure()
			.dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
			.schemas("broker")
			.defaultSchema("broker")
			.load();
		
		var current = Optional.ofNullable(flyway.info().current());
		current.ifPresent(info -> log.info("db schema is at version: {}", info.getVersion()));
		
		var pendingMigrations = flyway.info().pending();
		log.debug("Pending migrations are: {}", printMigrations(pendingMigrations));
		
		flyway.migrate();
	}

	private static String printMigrations(MigrationInfo[] pendingMigrations) {
		if(Objects.isNull(pendingMigrations)) {
			return "[]";
		}
		return Arrays.stream(pendingMigrations)
				.map(each -> each.getVersion() + " - " + each.getDescription())
				.collect(Collectors.joining(",", "[", "]"));
	}

}
