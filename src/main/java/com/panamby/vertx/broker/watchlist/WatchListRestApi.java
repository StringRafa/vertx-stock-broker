package com.panamby.vertx.broker.watchlist;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatchListRestApi {

	public static void attach(Router parent) {
		
		final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
		
		final String path = "/account/watchlist/:accountId";
		
		parent.get(path).handler(context -> {
			String accountId = context.pathParam("accountId");
			log.debug("{} for account {}", context.normalizedPath(), accountId);
			Optional<WatchList> watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
			
			if(watchList.isEmpty()) {
				context.response()
					.setStatusCode(HttpResponseStatus.NOT_FOUND.code())
					.end(new JsonObject()
							.put("message", "watchList for account " + accountId + " not available!")
							.put("path", context.normalizedPath())
							.toBuffer()
				);
			  return;
			}
			
			context.response().end(watchList.get().toJsonObject().toBuffer());
		});
		
		parent.put(path).handler(context -> {
			
			String accountId = context.pathParam("accountId");
			log.debug("{} for account {}", context.normalizedPath(), accountId);
			
			JsonObject json = context.getBodyAsJson();
			WatchList watchList = json.mapTo(WatchList.class);
			watchListPerAccount.put(UUID.fromString(accountId), watchList);
			context.response().end(json.toBuffer());
		});
		
		parent.delete(path).handler(context -> {
			
		});
		
	}

}
