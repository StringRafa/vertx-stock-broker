package com.panamby.vertx.broker.assets.watchlist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.panamby.vertx.broker.MainVerticle;
import com.panamby.vertx.broker.assets.Asset;
import com.panamby.vertx.broker.watchlist.WatchList;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void add_and_return_watchlist_for_account(Vertx vertx, VertxTestContext context) throws Throwable {
	  WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
	  UUID accountId = UUID.randomUUID();
	  
	  client.put("/account/watchlist/" + accountId.toString())
	  	.sendJsonObject(body())
	  	.onComplete(context.succeeding(response -> {
	  	    JsonObject json = response.bodyAsJsonObject();
	  	    log.info("Response PUT: {}", json);
	  	    assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
	  	    assertEquals(200, response.statusCode());
	  	    context.completeNow();
	  	}))
	  	.compose(next -> {
	  		client.get("/account/watchlist/" + accountId.toString())
	  			.send()
	  			.onComplete(context.succeeding(response -> {
	  				JsonObject json = response.bodyAsJsonObject();
	  		  	    log.info("Response GET: {}", json);
	  		  	    assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
	  		  	    assertEquals(200, response.statusCode());
	  		  	    context.completeNow();
	  			}));
	  		return Future.succeededFuture();
	  	});
  }

  @Test
  void adds_and_deletes_watchlist_for_account(Vertx vertx, VertxTestContext context) {
	  WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
	  UUID accountId = UUID.randomUUID();
	  
	  client.put("/account/watchlist/" + accountId.toString())
	  	.sendJsonObject(body())
	  	.onComplete(context.succeeding(response -> {
	  	    JsonObject json = response.bodyAsJsonObject();
	  	    log.info("Response PUT: {}", json);
	  	    assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
	  	    assertEquals(200, response.statusCode());
	  	    context.completeNow();
	  	}))
	  	.compose(next -> {
	  		client.delete("/account/watchlist/" + accountId.toString())
	  			.send()
	  			.onComplete(context.succeeding(response -> {
	  				JsonObject json = response.bodyAsJsonObject();
	  		  	    log.info("Response DELETE: {}", json);
	  		  	    assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
	  		  	    assertEquals(200, response.statusCode());
	  		  	    context.completeNow();
	  			}));
	  		return Future.succeededFuture();
	  	});
  }
  
	private JsonObject body() {
		return new WatchList(Arrays.asList(
			new Asset("AMZN"),
			new Asset("TSLA")))
		.toJsonObject();
	}
}
