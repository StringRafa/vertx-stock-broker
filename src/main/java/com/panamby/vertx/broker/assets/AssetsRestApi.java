package com.panamby.vertx.broker.assets;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssetsRestApi {
	
	public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "NFLX", "TSLA", "FB", "MSFT", "GOOG");

	public static void attach(Router parent) {
		parent.get("/assets").handler(context -> {
			final JsonArray response = new JsonArray();
			ASSETS.stream().map(Asset::new).forEach(response::add);
			log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
			context.response().end(response.toBuffer());
		});
	}
}
