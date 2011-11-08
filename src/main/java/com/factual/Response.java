package com.factual;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Represents the response from running a query against Factual.
 *
 * @author aaron
 */
public class Response {
  public static final int UNDEFINED = -1;
  private String version;
  private String status;
  private int totalRowCount = UNDEFINED;
  private List<Map<String, Object>> data = Lists.newArrayList();


  public String getStatus() {
    return status;
  }

  public String getVersion() {
    return version;
  }

  public List<Map<String, Object>> getData() {
    return data;
  }

  /**
   * @return total underlying row count, or {@link #UNDEFINED} if unknown.
   */
  public int getTotalRowCount() {
    return totalRowCount;
  }

  /**
   * @return a Collection of all String values found in this Response's data
   *         rows as the <tt>field</tt> attribute.
   */
  public Collection<String> mapStrings(final String field) {
    return Collections2.transform(data, new Function<Map<String, Object>, String>() {
      @Override
      public String apply(Map<String, Object> row) {
        Object val = row.get(field);
        return val != null ? val.toString() : null;
      }});
  }

  /**
   * Parses a <tt>json</tt> response (ostensibly from Factual) and returns a
   * Response object representing the full response.
   * 
   * @return a Response object representing the full response <tt>json</tt>.
   */
  public static Response fromJson(String json) {
    Response response = new Response();
    try {
      JSONObject root = new JSONObject(json);
      response.version = root.getString("version");
      response.status = root.getString("status");

      JSONObject resp = root.getJSONObject("response");
      if(resp.has("total_row_count")) {
        response.totalRowCount = resp.getInt("total_row_count");
      }

      response.data = data(resp.getJSONArray("data"));
      return response;
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<Map<String, Object>> data(JSONArray arr) throws JSONException {
    List<Map<String, Object>> data = Lists.newArrayList();
    for(int i=0; i<arr.length(); i++) {
      data.add(row(arr.getJSONObject(i)));
    }
    return data;
  }

  private static Map<String, Object> row(JSONObject jo) throws JSONException {
    Map<String, Object> row = Maps.newHashMap();
    Iterator iter = jo.keys();
    while(iter.hasNext()) {
      String key = iter.next().toString();
      Object value = jo.get(key);
      row.put(key, value);
    }
    return row;
  }

}
