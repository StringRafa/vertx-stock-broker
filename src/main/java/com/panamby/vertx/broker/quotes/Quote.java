package com.panamby.vertx.broker.quotes;

import java.math.BigDecimal;

import com.panamby.vertx.broker.assets.Asset;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Quote {

	Asset asset;
	BigDecimal bid;
	BigDecimal ask;
	BigDecimal lastPrice;
	BigDecimal volume;
	
	public JsonObject toJsonObject() {
		return JsonObject.mapFrom(this);
	}
}
