package com.factual.driver;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class JsonUtil {

  /**
   * Takes a JSONArray of records, where each record is a dictionary, and
   * returns the translated List of Maps.
   */
  public static List<Map<String, Object>> data(JSONArray arr) throws JSONException {
    List<Map<String, Object>> data = Lists.newArrayList();
    for(int i=0; i<arr.length(); i++) {
      data.add(row(arr.getJSONObject(i)));
    }
    return data;
  }
  
  /**
   * Takes a JSONObject of records, where each record is a dictionary, and
   * returns the translated Map of Maps.
   */
  public static Map<String, Map<String, Object>> data(JSONObject jo) throws JSONException {
    Map<String, Map<String, Object>> data = Maps.newHashMap();
    Iterator<?> iter = jo.keys();
    while (iter.hasNext()) {
    	String key = iter.next().toString();
    	data.put(key, row(jo.getJSONObject(key)));
    }
    return data;
  }
  
  private static Map<String, Object> row(JSONObject jo) throws JSONException {
    Map<String, Object> row = Maps.newHashMap();
    Iterator<?> iter = jo.keys();
    while(iter.hasNext()) {
      String key = iter.next().toString();
      Object value = jo.get(key);
      row.put(key, value);
    }
    return row;
  }
  
  protected static String toJsonStr(Object obj) {
	try {
	  return new ObjectMapper().writeValueAsString(obj);
	} catch (JsonGenerationException e) {
	  throw new RuntimeException(e);
	} catch (JsonMappingException e) {
	  throw new RuntimeException(e);
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
  }
  
}
