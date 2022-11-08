package com.panamby.vertx.broker.assets.quotes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.panamby.vertx.broker.MainVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_quotes_for_asset(Vertx vertx, VertxTestContext context) throws Throwable {
	  WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
	  client.get("/quotes/AMZN")
	  	.send()
	  	.onComplete(context.succeeding(response -> {
	  	    JsonObject json = response.bodyAsJsonObject();
	  	    log.info("Response: {}", json);
	  	    assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
	  	    assertEquals(200, response.statusCode());
	  	    context.completeNow();
	  	}));
  }

  @Test
  void return_not_found_for_unknown_asset(Vertx vertx, VertxTestContext context) throws Throwable {
	  WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
	  client.get("/quotes/UNKNOWN")
	  	.send()
	  	.onComplete(context.succeeding(response -> {
	  	    JsonObject json = response.bodyAsJsonObject();
	  	    log.info("Response: {}", json);
	  	    assertEquals(404, response.statusCode());
	  	    assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}", json.encode());
	  	    context.completeNow();
	  	}));
  }
}
