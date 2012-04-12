package com.factual.driver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;

/**
 * Represents a Factual Diffs response
 * @author brandon
 *
 */
public class DiffsResponse extends Response {

	private String json = null;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	
	public DiffsResponse(String json) {
		this.json = json;
		try {
			parseJson(json);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void parseJson(String json) throws JSONException {
		if (json == null || "".equals(json))
			return;
		boolean started = false;
		int braceCount = 0;
		int beginIdx = 0;
		int endIdx = 0;
		for (int i=0; i<json.length();i++) {
			if (json.charAt(i) == '{') {
				if (!started) {
					started = true;
					beginIdx = i;
				}
				braceCount++;
			} else if (json.charAt(i) == '}') {
				braceCount--;
			}
			if (started && braceCount == 0) {
				endIdx = i+1;
				String jsonObj = json.substring(beginIdx, endIdx);
				Map<String, Object> itemMap = parseItem(jsonObj);
				data.add(itemMap);
				started = false;
			}
		}
	}

	private Map<String, Object> parseItem(String jsonObj) throws JSONException {
		System.out.println(jsonObj);
		JSONObject jsonItem = new JSONObject(jsonObj);
	    Iterator<?> iter = jsonItem.keys();
	    Map<String, Object> itemMap = Maps.newHashMap();
	    while (iter.hasNext()) {
	    	String key = iter.next().toString();
	    	itemMap.put(key, jsonItem.getString(key));
	    }
	    return itemMap;
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
