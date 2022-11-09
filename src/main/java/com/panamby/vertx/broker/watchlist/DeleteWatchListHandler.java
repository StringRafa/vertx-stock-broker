package com.panamby.vertx.broker.watchlist;

import java.util.HashMap;
import java.util.UUID;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteWatchListHandler implements Handler<RoutingContext> {
	
	private final HashMap<UUID, WatchList> watchListPerAccount;
	
	public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
		this.watchListPerAccount = watchListPerAccount;
	}

	@Override
	public void handle(RoutingContext context) {

		String accountId = WatchListRestApi.getAccountId(context);
		final WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));
		log.info("Deleted: {}, Remaining: {}", deleted, watchListPerAccount.values());
		context.response()
			.end(deleted.toJsonObject().toBuffer());		
	}

}
