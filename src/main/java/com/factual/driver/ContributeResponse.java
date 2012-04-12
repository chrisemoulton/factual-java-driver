package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Contribute request against Factual.
 * 
 * @author brandon
 */
public class ContributeResponse extends Response {
	private String json = null;
	private String factualId;
	private boolean newEntity;
	
	/**
	 * Constructor, parses from a JSON response String.
	 * 
	 * @param json the JSON response String returned by Factual.
	 */
	public ContributeResponse(String json) {
		this.json = json;
		try {
			JSONObject rootJsonObj = new JSONObject(json);
			Response.withMeta(this, rootJsonObj);
			parseResponse(rootJsonObj.getJSONObject(Constants.RESPONSE));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private void parseResponse(JSONObject jo) throws JSONException {
	    factualId = jo.getString(Constants.CONTRIBUTE_FACTUAL_ID);
	    newEntity = jo.getBoolean(Constants.CONTRIBUTE_NEW_ENTITY);
	}
	
	/**
	 * @return the factual id that contribute was performed on
	 */
	public String getFactualId() {
		return factualId;
	}

	/**
	 * @return whether or not this was a contribution to add a new row or update an existing row
	 */
	public boolean isNewEntity() {
		return newEntity;
	}
	
	@Override
	public String getJson() {
		return json;
	}
}
