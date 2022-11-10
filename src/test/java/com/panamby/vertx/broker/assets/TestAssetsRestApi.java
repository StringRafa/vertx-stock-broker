package com.panamby.vertx.broker.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.panamby.vertx.broker.MainVerticle;
import com.panamby.vertx.broker.config.ConfigLoader;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
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
	System.setProperty(ConfigLoader.SERVER_PORT, "8888");
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
	  	    assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"},{\"name\":\"FB\"},{\"name\":\"MSFT\"},{\"name\":\"GOOG\"}]", json.encode());
	  	    assertEquals(200, response.statusCode());
	  	    assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
	  	    assertEquals("my-value", response.getHeader("my-header"));
	  	    context.completeNow();
	  	}));
  }
}
