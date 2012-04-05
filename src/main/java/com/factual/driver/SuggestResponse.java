package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Suggest request against Factual.
 * 
 * @author brandon
 */
public class SuggestResponse extends Response {
	private String json = null;
	private String factualId;
	private boolean newEntity;
	
	/**
	 * Constructor, parses from a JSON response String.
	 * 
	 * @param json the JSON response String returned by Factual.
	 */
	public SuggestResponse(String json) {
		this.json = json;
		try {
			JSONObject rootJsonObj = new JSONObject(json);
			Response.withMeta(this, rootJsonObj);
			parseResponse(rootJsonObj.getJSONObject("response"));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private void parseResponse(JSONObject jo) throws JSONException {
	    factualId = jo.getString("factual_id");
	    newEntity = jo.getBoolean("new_entity");
	}
	
	/**
	 * @return the factual id that suggest was performed on
	 */
	public String getFactualId() {
		return factualId;
	}

	/**
	 * @return whether or not this was a suggestion to add a new row or update an existing row
	 */
	public boolean isNewEntity() {
		return newEntity;
	}
	
	@Override
	public String getJson() {
		return json;
	}
}
