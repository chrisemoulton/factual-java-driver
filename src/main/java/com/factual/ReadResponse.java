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
 * Represents the response from running a fetch request against Factual, such as
 * a geolocation based query for specific places entities.
 *
 * @author aaron
 */
public class ReadResponse extends Response {
  private List<Map<String, Object>> data = Lists.newArrayList();


  /**
   * Constructor, parses from a JSON response String.
   * 
   * @param json the JSON response String returned by Factual.
   */
  public ReadResponse(String json) {
    try{
      JSONObject rootJsonObj = new JSONObject(json);
      Response.withMeta(this, rootJsonObj);
      data = data(rootJsonObj.getJSONObject("response").getJSONArray("data"));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<String, Object> first() {
    return data.get(0);
  }

  /**
   * An ordered collection of the main data returned by Factual. Represented as
   * Maps, where each Map is a record in the results.
   * 
   * @return the main data returned by Factual.
   */
  public List<Map<String, Object>> getData() {
    return data;
  }

  /**
   * @return the size of the result set
   */
  public int size() {
    return data.size();
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

  //TODO: Bradley: "mapToStrings, mapToDoubles, etc."

  private static List<Map<String, Object>> data(JSONArray arr) throws JSONException {
    List<Map<String, Object>> data = Lists.newArrayList();
    for(int i=0; i<arr.length(); i++) {
      data.add(row(arr.getJSONObject(i)));
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

}
