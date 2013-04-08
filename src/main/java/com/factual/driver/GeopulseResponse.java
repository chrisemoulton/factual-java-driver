package com.factual.driver;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;



/**
 * Represents the response from running a geopulse request against Factual, such as
 * a geolocation based query for specific places entities.
 *
 * @author aaron
 */
public class GeopulseResponse extends Response {
  protected InternalResponse resp = null;
  private Map<String, Map<String, Object>> data = Maps.newHashMap();


  /**
   * Constructor, parses from a JSON response String.
   */
  public GeopulseResponse(InternalResponse resp) {
    this.resp = resp;
    try{
      JSONObject rootJsonObj = new JSONObject(resp.getContent());
      Response.withMeta(this, rootJsonObj);
      data = JsonUtil.data(rootJsonObj.getJSONObject(Constants.RESPONSE).getJSONObject(Constants.QUERY_DATA));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return The full JSON response from Factual
   */
  @Override
  public String getJson() {
    return resp.getContent();
  }

  /**
   * A mapping of the geopulse data returned by Factual. Represented as
   * Maps, where each Map is a record in the results.
   * 
   * @return the main data returned by Factual.
   */
  public Map<String, Map<String, Object>> getData() {
    return data;
  }

  /**
   * @return the size of the result set
   */
  public int size() {
    return data.size();
  }
}
