package com.factual.driver;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Factual Diffs response
 * @author brandon
 *
 */
public class DiffsResponse extends Response {

	private String json = null;
	private List<Map<String, Object>> data = null;
	
	public DiffsResponse(String json) {
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
	    data = JsonUtil.data(jo.getJSONArray(Constants.DIFFS_DATA));
	}
	
	/**
	 * Get diffs response as a list of diffs, where each diff is represented as a map 
	 * @return list of diffs returned from this query.
	 */
	public List<Map<String, Object>> getData() {
		return data;
	}

	@Override
	public String getJson() {
		return json;
	}
}
