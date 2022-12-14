package com.panamby.vertx.broker.quotes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.panamby.vertx.broker.assets.Asset;
import com.panamby.vertx.broker.assets.AssetsRestApi;

import io.netty.util.internal.ThreadLocalRandom;
import io.vertx.ext.web.Router;

public class QuotesRestApi {

	public static void attach(Router parent) {
		
		final Map<String, Quote> cachedQuotes = new HashMap<>();
		AssetsRestApi.ASSETS.forEach(symbol ->
			cachedQuotes.put(symbol, initRandomQuote(symbol))
		);
		
		parent.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
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
