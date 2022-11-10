package com.panamby.vertx.broker;

import com.panamby.vertx.broker.config.ConfigLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

	public static final int PORT = 8888;

	public static void main(String[] args) {
//		System.setProperty(ConfigLoader.SERVER_PORT, "8888");
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
		vertx.deployVerticle(RestApiVerticle.class.getName(),
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
