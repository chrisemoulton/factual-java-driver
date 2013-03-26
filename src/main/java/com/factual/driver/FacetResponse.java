package com.factual.driver;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Facet lookup against Factual.
 * 
 * @author brandon
 */
public class FacetResponse extends Response {
  private Map<String, Map<String, Object>> data = null;
  private InternalResponse resp = null;

  /**
   * Constructor, parses from a JSON response String.
   */
  public FacetResponse(InternalResponse resp) {
    this.resp = resp;
    try {
      JSONObject rootJsonObj = new JSONObject(resp.getContent());
      Response.withMeta(this, rootJsonObj);
      parseResponse(rootJsonObj.getJSONObject(Constants.RESPONSE));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private void parseResponse(JSONObject jo) throws JSONException {
    data = JsonUtil.data(jo.getJSONObject(Constants.FACET_DATA));
  }

  /**
   * An collection of the facet data returned by Factual. Represented as a nested mapping
   * from field name to facet to count.
   * 
   * @return the facet data returned by Factual.
   */
  public Map<String, Map<String, Object>> getData() {
    return data;
  }

  @Override
  public String getJson() {
    return resp.getContent();
  }
}