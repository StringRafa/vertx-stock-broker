package com.panamby.vertx.broker.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.panamby.vertx.broker.MainVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext context) throws Throwable {
	  WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
	  client.get("/assets")
	  	.send()
	  	.onComplete(context.succeeding(response -> {
	  	    JsonArray json = response.bodyAsJsonArray();
	  	    log.info("Response: {}", json);
	  	    assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"}]", json.encode());
	  	    assertEquals(200, response.statusCode());
	  	}));
  }
}
