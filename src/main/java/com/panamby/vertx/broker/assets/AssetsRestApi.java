package com.panamby.vertx.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssetsRestApi {

	public static void attach(Router parent) {
		parent.get("/assets").handler(context -> {
			final JsonArray response = new JsonArray();
			response
				.add(new Assets("AAPL"))
				.add(new Assets("AMZN"))
				.add(new Assets("NFLX"))
				.add(new Assets("TSLA"));
			log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
			context.response().end(response.toBuffer());
		});
	}
}
