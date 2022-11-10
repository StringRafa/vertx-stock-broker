package com.panamby.vertx.broker.assets.quotes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.panamby.vertx.broker.AbstractRestApiTest;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi extends AbstractRestApiTest {

	@Test
	void return_quotes_for_asset(Vertx vertx, VertxTestContext context) throws Throwable {
		WebClient client = webClient(vertx);
		client.get("/quotes/AMZN").send().onComplete(context.succeeding(response -> {
			JsonObject json = response.bodyAsJsonObject();
			log.info("Response: {}", json);
			assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
			assertEquals(200, response.statusCode());
			context.completeNow();
		}));
	}

	@Test
	void return_not_found_for_unknown_asset(Vertx vertx, VertxTestContext context) throws Throwable {
		WebClient client = webClient(vertx);
		client.get("/quotes/UNKNOWN").send().onComplete(context.succeeding(response -> {
			JsonObject json = response.bodyAsJsonObject();
			log.info("Response: {}", json);
			assertEquals(404, response.statusCode());
			assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}",
					json.encode());
			context.completeNow();
		}));
	}

	private WebClient webClient(Vertx vertx) {
		return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
	}
}
