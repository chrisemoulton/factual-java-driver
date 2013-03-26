package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Flag request against Factual.
 * 
 * @author brandon
 */
public class FlagResponse extends Response {
  private InternalResponse resp = null;

  /**
   * Constructor, parses from a JSON response String.
   */
  public FlagResponse(InternalResponse resp) {
    this.resp = resp;
    try {
      JSONObject rootJsonObj = new JSONObject(resp.getContent());
      Response.withMeta(this, rootJsonObj);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getJson() {
    return resp.getContent();
  }
}
