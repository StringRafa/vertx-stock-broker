package com.panamby.vertx.broker;

import org.junit.jupiter.api.BeforeEach;

import com.panamby.vertx.broker.config.ConfigLoader;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRestApiTest {

  protected	static final int TEST_SERVER_PORT = 9000;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
	System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
	System.setProperty(ConfigLoader.DB_HOST, "localhost");
	System.setProperty(ConfigLoader.DB_PORT, "5432");
	System.setProperty(ConfigLoader.DB_DATABASE, "vertx-stock-broker");
	System.setProperty(ConfigLoader.DB_USER, "postgres");
	System.setProperty(ConfigLoader.DB_PASSWORD, "secret");
	log.warn("!!!  Tests are using local database  !!!");
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }
  
}
