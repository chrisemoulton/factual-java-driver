package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Report request against Factual.
 * 
 * @author brandon
 */
public class ReportResponse extends Response {
	private String json = null;

	/**
	 * Constructor, parses from a JSON response String.
	 * 
	 * @param json the JSON response String returned by Factual.
	 */
	public ReportResponse(String json) {
		this.json = json;
		try {
			JSONObject rootJsonObj = new JSONObject(json);
			Response.withMeta(this, rootJsonObj);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getJson() {
		return json;
	}
}
