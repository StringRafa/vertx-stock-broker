package com.panamby.vertx.broker.config;

import java.util.Arrays;
import java.util.List;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigLoader {
	
	public static final String SERVER_PORT = "SERVER_PORT";
	static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(SERVER_PORT);

	public static Future<BrokerConfig> load(Vertx vertx){
		
		final var exposedKeys = new JsonArray();
		EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
		log.debug("Fetch configuration for {}", exposedKeys.encode());
		
		var envStore = new ConfigStoreOptions()
				.setType("env")
				.setConfig(new JsonObject().put("keys", exposedKeys));
		
		var propertyStore = new ConfigStoreOptions()
				.setType("sys")
				.setConfig(new JsonObject().put("cache", false));
		
		var retriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverOptions()
				// Order defines overload rules
					.addStore(propertyStore)
					.addStore(envStore)
		);
		
		return retriever.getConfig().map(BrokerConfig::from);
	}
}
