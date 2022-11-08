package com.panamby.vertx.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssetsRestApi {

	public static void attach(Router parent) {
		parent.get("/assets").handler(context -> {
			  final JsonArray response = new JsonArray();
			  response
			  	.add(new JsonObject().put("symbol", "AAPL"))
			  	.add(new JsonObject().put("symbol", "AMZN"))
			  	.add(new JsonObject().put("symbol", "NFLX"))
			  	.add(new JsonObject().put("symbol", "TSLA"));
			  log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
			  context.response().end(response.toBuffer());
		  });
	}
}
