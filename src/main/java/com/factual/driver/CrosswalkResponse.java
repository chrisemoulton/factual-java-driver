package com.factual.driver;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;

/**
 * Represents the response from running a Crosswalk lookup against Factual.
 * 
 * @deprecated No longer in use due to deprecated CrosswalkQuery.
 * @author aaron
 */
@Deprecated
public class CrosswalkResponse extends Response {
  private final List<Crosswalk> crosswalks = Lists.newArrayList();
  private final String json;

  /**
   * Constructor, parses from a JSON response String.
   * 
   * @deprecated No longer in use due to deprecated CrosswalkQuery.
   * @param json
   *          the JSON response String returned by Factual.
   */
  @Deprecated
  public CrosswalkResponse(String json) {
    this.json = json;
    try {
      JSONObject rootJsonObj = new JSONObject(json);
      Response.withMeta(this, rootJsonObj);
      parseCrosswalks(rootJsonObj.getJSONObject(Constants.RESPONSE)
          .getJSONArray(Constants.CROSSWALK_DATA));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @Deprecated
  public String getJson() {
    return json;
  }

  /**
   * Returns the ordered collection of Crosswalks represented by this response.
   * 
   * @deprecated No longer in use due to deprecated CrosswalkQuery.
   * @return the ordered collection of Crosswalks represented by this response.
   */
  @Deprecated
  public List<Crosswalk> getCrosswalks() {
    return crosswalks;
  }

  /**
   * @deprecated No longer in use due to deprecated CrosswalkQuery.
   * @return the first Crosswalk hit or null if no data was returned.
   */
  @Deprecated
  public Crosswalk first() {
    if (crosswalks.isEmpty()) {
      return null;
    } else {
      return crosswalks.get(0);
    }
  }

  /**
   * @deprecated No longer in use due to deprecated CrosswalkQuery.
   * @return the size of the result set
   */
  @Deprecated
  public int size() {
    return crosswalks.size();
  }

  private void parseCrosswalks(JSONArray arr) throws JSONException {
    for (int i = 0; i < arr.length(); i++) {
      crosswalks.add(crosswalkFrom(arr.getJSONObject(i)));
    }
  }

  private static Crosswalk crosswalkFrom(JSONObject jo) throws JSONException {
    Crosswalk cw = new Crosswalk();
    cw.setFactualId(jo.getString(Constants.CROSSWALK_FACTUAL_ID));
    cw.setNamespace(jo.getString(Constants.CROSSWALK_NAMESPACE));

    if (jo.has(Constants.CROSSWALK_NAMESPACE_ID)) {
      cw.setNamespaceId(jo.getString(Constants.CROSSWALK_NAMESPACE_ID));
    }

    if (jo.has(Constants.CROSSWALK_URL)) {
      cw.setUrl(jo.getString(Constants.CROSSWALK_URL));
    }
    return cw;
  }

}
