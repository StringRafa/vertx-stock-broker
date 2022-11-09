package com.panamby.vertx.broker.watchlist;

import java.util.HashMap;
import java.util.UUID;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatchListRestApi {

	public static void attach(Router parent) {
		
		final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
		
		final String path = "/account/watchlist/:accountId";
		
		parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));		
		parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));		
		parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));		
	}

	static String getAccountId(RoutingContext context) {
		String accountId = context.pathParam("accountId");
		log.debug("{} for account {}", context.normalizedPath(), accountId);
		return accountId;
	}

}
