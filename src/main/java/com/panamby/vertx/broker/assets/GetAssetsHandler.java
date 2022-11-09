package com.panamby.vertx.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetAssetsHandler implements Handler<RoutingContext> {

	@Override
	public void handle(final RoutingContext context) {
		final JsonArray response = new JsonArray();
		AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);
		log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
		context.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
				.putHeader("my-header", "my-value").end(response.toBuffer());

	}

}
