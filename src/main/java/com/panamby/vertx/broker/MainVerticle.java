package com.panamby.vertx.broker;

import com.panamby.vertx.broker.assets.AssetsRestApi;
import com.panamby.vertx.broker.quotes.QuotesRestApi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  public static final int PORT = 8888;

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
	  restApi.route().failureHandler(handleFailure());
	  AssetsRestApi.attach(restApi);
	  QuotesRestApi.attach(restApi);
	  
	    vertx.createHttpServer()
	    .requestHandler(restApi)
	    .exceptionHandler(error -> log.error("HTTP Server error: ", error))
	    .listen(PORT, http -> {
	      if (http.succeeded()) {
	        startPromise.complete();
	        log.info("HTTP server started on port 8888");
	      } else {
	        startPromise.fail(http.cause());
	      }
	    });
  }

private Handler<RoutingContext> handleFailure() {
	return errorContext -> {
		  if(errorContext.response().ended()) {
			  // Ignore completed response
			  return;
		  }
		  log.error("Route Error:", errorContext.failure());
		  errorContext.response()
		  	.setStatusCode(500)
		  	.end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
	  };
}
}
