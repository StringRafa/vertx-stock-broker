package com.panamby.vertx.broker;

import com.panamby.vertx.broker.assets.AssetsRestApi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
	  var vertx = Vertx.vertx();
	  vertx.exceptionHandler( error -> 
		 log.error("Unhandled: {}", error)
	  );
	  vertx.deployVerticle(new MainVerticle(), ar -> {
		  if(ar.failed()) {
			  log.error("Failed to deploy: {}", ar.cause());
			  return;
		  }
		  log.info("Deployed {}!", MainVerticle.class.getName());
	  });
}
	
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
	  final Router restApi = Router.router(vertx);
	  AssetsRestApi.attach(restApi);
	  
    vertx.createHttpServer()
    .requestHandler(restApi)
    .exceptionHandler(error -> log.error("HTTP Server error: ", error))
    .listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        log.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
