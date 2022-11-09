package com.panamby.vertx.broker.quotes;

import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetQuoteHandler implements Handler<RoutingContext> {

	private final Map<String, Quote> cachedQuotes;

	public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
		this.cachedQuotes = cachedQuotes;
	}

	@Override
	public void handle(RoutingContext context) {
		final String assetParam = context.pathParam("asset");
		log.debug("Asset parameter: {}", assetParam);

		Optional<Quote> maybeQuote = Optional.ofNullable(cachedQuotes.get(assetParam));

		if (maybeQuote.isEmpty()) {
			context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
					.end(new JsonObject().put("message", "quote for asset " + assetParam + " not available!")
							.put("path", context.normalizedPath()).toBuffer());
			return;
		}

		final JsonObject response = maybeQuote.get().toJsonObject();

		log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
		context.response().end(response.toBuffer());

	}

}
