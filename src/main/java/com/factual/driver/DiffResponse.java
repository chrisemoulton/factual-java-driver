package com.factual.driver;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class DiffResponse extends Response {

	private String json = null;
	private Map<String, Map<String, Object>> data = null;
	
	public DiffResponse(String json) {
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
	    data = JsonUtil.data(jo.getJSONObject("data"));
	}
	
	public Map<String, Map<String, Object>> getData() {
		return data;
	}

	@Override
	public String getJson() {
		return json;
	}
}
