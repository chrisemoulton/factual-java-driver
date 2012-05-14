package com.factual.driver;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;

/**
 * Represents a Factual Multi response.
 * 
 * @author brandon
 *
 */
public class MultiResponse extends Response {
	private String json = null;
	private List<Response> data = Lists.newArrayList();

	private Map<String, Object> requestMapping = null;
	
	/**
	 * 
	 * @param requestMapping
	 */
	public MultiResponse(Map<String, Object> requestMapping) {
		this.requestMapping = requestMapping;
	}

	/**
	 * Parses from a json response string
	 * @param json json response string to parse from
	 */
	public void setJson(String json) {
		this.json = json;
		try {
			JSONObject rootJsonObj = new JSONObject(json);
			parseResponse(rootJsonObj);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void parseResponse(JSONObject jo) throws JSONException {
	   data.clear();
	   for (Entry<String, Object> entry : requestMapping.entrySet()) {
		   String responseJson = jo.getJSONObject(entry.getKey()).toString();
		   Object type = entry.getValue();	
		   Response resp = null;
		   if (type instanceof Query) {
				resp = new ReadResponse(responseJson);
		   } else if (type instanceof CrosswalkQuery) {
				resp = new CrosswalkResponse(responseJson);
		   } else if (type instanceof ResolveQuery) {
				resp = new ReadResponse(responseJson);
		   } else if (type instanceof FacetQuery) {
				resp = new FacetResponse(responseJson);
		   } else if (type instanceof Map) {
			   	resp = new RawReadResponse(responseJson);
		   }
		   if (resp != null)
			   data.add(resp);
	   }
	}
	
    /**
     * A collection of the responses returned by Factual for a multi query.
     * 
     * @return the multi query data returned by Factual.
     */	
	public List<Response> getData() {
		return data;
	}

	@Override
	public String getJson() {
		return json;
	}
}
