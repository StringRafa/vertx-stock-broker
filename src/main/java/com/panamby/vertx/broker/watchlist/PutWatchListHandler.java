package com.panamby.vertx.broker.watchlist;

import java.util.HashMap;
import java.util.UUID;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class PutWatchListHandler implements Handler<RoutingContext> {
	
	private final HashMap<UUID, WatchList> watchListPerAccount;
	
	public PutWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
		this.watchListPerAccount = watchListPerAccount;
	}

	@Override
	public void handle(RoutingContext context) {
		
		String accountId = WatchListRestApi.getAccountId(context);
		
		JsonObject json = context.getBodyAsJson();
		WatchList watchList = json.mapTo(WatchList.class);
		watchListPerAccount.put(UUID.fromString(accountId), watchList);
		context.response().end(json.toBuffer());		
	}

}
