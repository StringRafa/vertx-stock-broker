package com.panamby.vertx.broker.quotes;

import java.math.BigDecimal;

import com.panamby.vertx.broker.assets.Asset;

import io.netty.util.internal.ThreadLocalRandom;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuotesRestApi {

	public static void attach(Router parent) {
		parent.get("/quotes/:asset").handler(context -> {
			
			final String assetParam = context.pathParam("asset");
			log.debug("Asset parameter: {}", assetParam);
			
			Quote quote = initRandomQuote(assetParam);
			
			final JsonObject response = quote.toJsonObject();
			
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
