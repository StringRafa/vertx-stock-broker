package com.panamby.vertx.broker;

import com.panamby.vertx.broker.config.ConfigLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionInfoVerticle extends AbstractVerticle{

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		ConfigLoader.load(vertx)
		.onFailure(startPromise::fail)
		.onSuccess(configuration -> {
			log.info("Current Application Version is: {}", configuration.getVersion());
			startPromise.complete();
		});	
	}
}
