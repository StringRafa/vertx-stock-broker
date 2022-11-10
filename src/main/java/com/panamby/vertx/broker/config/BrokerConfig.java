package com.panamby.vertx.broker.config;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class BrokerConfig {

	int serverPort;
	
	public static BrokerConfig from(final JsonObject config) {
		final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
		if(Objects.isNull(serverPort)) {
			throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
		}
		return BrokerConfig.builder()
				.serverPort(serverPort)
				.build();
	}
}
