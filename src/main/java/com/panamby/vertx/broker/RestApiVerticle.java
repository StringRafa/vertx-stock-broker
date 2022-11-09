package com.panamby.vertx.broker;

import com.panamby.vertx.broker.assets.AssetsRestApi;
import com.panamby.vertx.broker.quotes.QuotesRestApi;
import com.panamby.vertx.broker.watchlist.WatchListRestApi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestApiVerticle extends AbstractVerticle {

	@Override
	public void start(final Promise<Void> startPromise) throws Exception {		
		startHttpServerAndAttachRoutes(startPromise);
	}
	
	private void startHttpServerAndAttachRoutes(Promise<Void> startPromise) {
		final Router restApi = Router.router(vertx);
		  restApi.route()
		  	.handler(BodyHandler.create())
		  	.failureHandler(handleFailure());
		  AssetsRestApi.attach(restApi);
		  QuotesRestApi.attach(restApi);
		  WatchListRestApi.attach(restApi);
		  
		    vertx.createHttpServer()
		    .requestHandler(restApi)
		    .exceptionHandler(error -> log.error("HTTP Server error: ", error))
		    .listen(MainVerticle.PORT, http -> {
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
