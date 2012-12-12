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
 * 
 * @author brandon
 * 
 */
public class DiffsResponse extends Response implements DiffsCallback {

  private String json = "";
  private final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

  public DiffsResponse() {
  }

  protected static Map<String, Object> parseItem(String jsonObj) throws JSONException {
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
   * Get diffs response as a list of diffs, where each diff is represented as a
   * map
   * 
   * @return list of diffs returned from this query.
   */
  public List<Map<String, Object>> getData() {
    return data;
  }

  @Override
  public String getJson() {
    return json;
  }

  @Override
  public void onDiff(String line) {
    json += line;
    json += System.getProperty("line.separator");
    try {
      data.add(parseItem(line));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}