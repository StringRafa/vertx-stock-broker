package com.panamby.vertx.broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		vertx.exceptionHandler(error -> 
			log.error("Unhandled: {}", error));
		
		vertx.deployVerticle(new MainVerticle())
			.onFailure(err -> log.error("Failed to deploy: {}", err))
			.onSuccess(id -> 
				log.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id)
		);		
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.deployVerticle(VersionInfoVerticle.class.getName())
				.onFailure(startPromise::fail)
				.onSuccess(id -> log.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
				.compose(next -> deployRestApiVerticle(startPromise));		
	}

	private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
		return vertx.deployVerticle(RestApiVerticle.class.getName(),
				new DeploymentOptions().setInstances(processors())
				)
			.onFailure(startPromise::fail)
			.onSuccess(id -> {
				log.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
				startPromise.complete();
			});
	}

	private int processors() {
		return Math.max(1, Runtime.getRuntime().availableProcessors());
	}

}
