package com.panamby.vertx.broker;

import com.panamby.vertx.broker.assets.AssetsRestApi;
import com.panamby.vertx.broker.config.BrokerConfig;
import com.panamby.vertx.broker.config.ConfigLoader;
import com.panamby.vertx.broker.quotes.QuotesRestApi;
import com.panamby.vertx.broker.watchlist.WatchListRestApi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestApiVerticle extends AbstractVerticle {

	@Override
	public void start(final Promise<Void> startPromise) throws Exception {	
		
		ConfigLoader.load(vertx)
			.onFailure(startPromise::fail)
			.onSuccess(configuration -> {
				log.info("Retrieved Configuration: {}", configuration);
				startHttpServerAndAttachRoutes(startPromise, configuration);
			});		
	}
	
	private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, final BrokerConfig configuration) {
		
		// One pool for each Rest Api Verticle
		PgPool db = createDbPool(configuration);		
		
		final Router restApi = Router.router(vertx);
		  restApi.route()
		  	.handler(BodyHandler.create())
		  	.failureHandler(handleFailure());
		  AssetsRestApi.attach(restApi, db);
		  QuotesRestApi.attach(restApi);
		  WatchListRestApi.attach(restApi);
		  
		    vertx.createHttpServer()
		    .requestHandler(restApi)
		    .exceptionHandler(error -> log.error("HTTP Server error: ", error))
		    .listen(configuration.getServerPort(), http -> {
		      if (http.succeeded()) {
		        startPromise.complete();
		        log.info("HTTP server started on port {}", configuration.getServerPort());
		      } else {
		        startPromise.fail(http.cause());
		      }
		    });
	}

	private PgPool createDbPool(final BrokerConfig configuration) {
		// Create DB Pool
		final PgConnectOptions connectOptions = new PgConnectOptions()
				.setHost(configuration.getDbConfig().getHost())
				.setPort(configuration.getDbConfig().getPort())
				.setDatabase(configuration.getDbConfig().getDatabase())
				.setUser(configuration.getDbConfig().getUser())
				.setPassword(configuration.getDbConfig().getPassword());
		
		var poolOptions = new PoolOptions()
				.setMaxSize(4);
		
		return PgPool.pool(vertx, connectOptions, poolOptions);
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
