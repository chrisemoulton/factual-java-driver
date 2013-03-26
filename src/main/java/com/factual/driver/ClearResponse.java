package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Clear request against Factual.
 * 
 * @author brandon
 */
public class ClearResponse extends Response {
  private String factualId;
  private String commitId;
  private InternalResponse resp = null;

  /**
   * Constructor, parses from a JSON response String.
   */
  public ClearResponse(InternalResponse resp) {
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
    factualId = jo.getString(Constants.CLEAR_FACTUAL_ID);
    commitId = jo.getString(Constants.CLEAR_COMMIT_ID);
  }

  /**
   * @return the factual id that submit was performed on
   */
  public String getFactualId() {
    return factualId;
  }

  /**
   * @return the commit id associated with this clear
   */
  public String getCommitId() {
    return commitId;
  }

  @Override
  public String getJson() {
    return resp.getContent();
  }
}
