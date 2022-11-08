package com.panamby.vertx.broker.quotes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.panamby.vertx.broker.assets.Asset;
import com.panamby.vertx.broker.assets.AssetsRestApi;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.ThreadLocalRandom;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuotesRestApi {

	public static void attach(Router parent) {
		
		final Map<String, Quote> cachedQuotes = new HashMap<>();
		AssetsRestApi.ASSETS.forEach(symbol ->
			cachedQuotes.put(symbol, initRandomQuote(symbol))
		);
		
		parent.get("/quotes/:asset").handler(context -> {
			
			final String assetParam = context.pathParam("asset");
			log.debug("Asset parameter: {}", assetParam);
			
			Optional<Quote> maybeQuote = Optional.ofNullable(cachedQuotes.get(assetParam));
			
			if(maybeQuote.isEmpty()) {
				context.response()
					.setStatusCode(HttpResponseStatus.NOT_FOUND.code())
					.end(new JsonObject()
							.put("message", "quote for asset " + assetParam + " not available!")
							.put("path", context.normalizedPath())
							.toBuffer()
				);
			  return;
			}
			
			final JsonObject response = maybeQuote.get().toJsonObject();
			
			log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
			context.response().end(response.toBuffer());
		});
	}

	private static Quote initRandomQuote(final String assetParam) {
		return Quote.builder()
			.asset(new Asset(assetParam))
			.ask(randomValue())
			.bid(randomValue())
			.lastPrice(randomValue())
			.volume(randomValue())
			.build();
	}

	private static BigDecimal randomValue() {
		return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
	}
}
