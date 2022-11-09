package com.panamby.vertx.broker.watchlist;

import java.util.List;

import com.panamby.vertx.broker.assets.Asset;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {

	private List<Asset> assets;
	
	public JsonObject toJsonObject() {
		return JsonObject.mapFrom(this);
	}
}
