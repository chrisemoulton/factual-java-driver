package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the response from running a Insert request against Factual.
 * 
 * @author brandon
 */
public class InsertResponse extends Response {
  private InternalResponse resp = null;
  private String factualId;
  private String commitId;
  private boolean newEntity;

  /**
   * Constructor, parses from a JSON response String.
   */
  public InsertResponse(InternalResponse resp) {
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
    factualId = jo.getString(Constants.INSERT_FACTUAL_ID);
    newEntity = jo.getBoolean(Constants.INSERT_NEW_ENTITY);
    commitId = jo.getString(Constants.INSERT_COMMIT_ID);
  }

  /**
   * @return the factual id that submit was performed on
   */
  public String getFactualId() {
    return factualId;
  }

  /**
   * @return whether or not this was a submission to add a new row or update an
   *         existing row
   */
  public boolean isNewEntity() {
    return newEntity;
  }

  /**
   * @return the commit id associated with this insert
   */
  public String getCommitId() {
    return commitId;
  }

  @Override
  public String getJson() {
    return resp.getContent();
  }
}
