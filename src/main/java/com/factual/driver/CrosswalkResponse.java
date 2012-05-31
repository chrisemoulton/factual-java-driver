package com.factual.driver;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;



/**
 * Represents the response from running a Crosswalk lookup against Factual.
 *
 * @author aaron
 */
public class CrosswalkResponse extends Response {
  private final List<Crosswalk> crosswalks = Lists.newArrayList();
  private final String json;


  /**
   * Constructor, parses from a JSON response String.
   * 
   * @param json the JSON response String returned by Factual.
   */
  public CrosswalkResponse(String json) {
    this.json = json;
    try {
      JSONObject rootJsonObj = new JSONObject(json);
      Response.withMeta(this, rootJsonObj);
      parseCrosswalks(rootJsonObj.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.CROSSWALK_DATA));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getJson() {
    return json;
  }

  /**
   * Returns the ordered collection of Crosswalks represented by this response.
   * 
   * @return the ordered collection of Crosswalks represented by this response.
   */
  public List<Crosswalk> getCrosswalks() {
    return crosswalks;
  }

  /**
   * @return the first Crosswalk hit or null if no data was returned.
   */
  public Crosswalk first() {
    if(crosswalks.isEmpty()) {
      return null;
    } else {
      return crosswalks.get(0);
    }
  }

  /**
   * @return the size of the result set
   */
  public int size() {
    return crosswalks.size();
  }

  private void parseCrosswalks(JSONArray arr) throws JSONException {
    for(int i=0; i<arr.length(); i++) {
      crosswalks.add(crosswalkFrom(arr.getJSONObject(i)));
    }
  }

  private static Crosswalk crosswalkFrom(JSONObject jo) throws JSONException {
    Crosswalk cw = new Crosswalk();
    cw.setFactualId(jo.getString(Constants.CROSSWALK_FACTUAL_ID));
    cw.setNamespace(jo.getString(Constants.CROSSWALK_NAMESPACE));

    if(jo.has(Constants.CROSSWALK_NAMESPACE_ID)) {
      cw.setNamespaceId(jo.getString(Constants.CROSSWALK_NAMESPACE_ID));
    }

    if(jo.has(Constants.CROSSWALK_URL)) {
      cw.setUrl(jo.getString(Constants.CROSSWALK_URL));
    }
    return cw;
  }

}
