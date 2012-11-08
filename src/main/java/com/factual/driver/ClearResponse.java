package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Clear request against Factual.
 * 
 * @author brandon
 */
public class ClearResponse extends Response {
  private String json = null;
  private String factualId;

  /**
   * Constructor, parses from a JSON response String.
   * 
   * @param json
   *          the JSON response String returned by Factual.
   */
  public ClearResponse(String json) {
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
    factualId = jo.getString(Constants.CLEAR_FACTUAL_ID);
  }

  /**
   * @return the factual id that submit was performed on
   */
  public String getFactualId() {
    return factualId;
  }

  @Override
  public String getJson() {
    return json;
  }
}
