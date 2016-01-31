package com.gethightower.api.examples;

import java.io.*;
import org.json.*;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;

public class ShowDealPipeline {

	public void showDealPipeline() throws UnirestException {

		String url = String.format("https://%s/api/exports/v2/deal_pipeline.json", Configuration.HOST);

		HttpResponse<JsonNode> response = Unirest.get(url)
			.header("accept", "application/json")
			.basicAuth(Configuration.API_KEY, Configuration.API_SECRET)
			.asJson();

		JsonNode body = response.getBody();

		if (response.getStatus() != 200) {
			System.err.println(String.format("GET failed (HTTP %s)", response.getStatus()));
			System.err.println(body);
			return;
		}

		JSONArray assets = body.getArray();

		System.out.println(String.format("%d assets with deals modified in previous 7 days", assets.length()));

		for (int i = 0; i < assets.length(); i++) {
			JSONObject asset = assets.getJSONObject(i);
			JSONArray deals = asset.getJSONArray("deals");

			System.out.println(String.format("asset: %s (ID: %d)", asset.getString("name"), asset.getInt("id")));

			for (int j = 0; j < deals.length(); j++) {
				JSONObject deal = deals.getJSONObject(j);
				JSONObject dealStage = deal.getJSONObject("deal_stage");

				System.out.println(String.format("  deal: %s/%s (ID: %d)", deal.getString("tenant_name"), dealStage.getString("description"), deal.getInt("id")));
			}
		}
	}


	public static void main(String[] args) throws UnirestException {

		ShowDealPipeline me = new ShowDealPipeline();

		me.showDealPipeline();

	}

}
