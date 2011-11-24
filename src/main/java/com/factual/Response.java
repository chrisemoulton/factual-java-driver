package com.factual;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Represents the basic concept of a response from Factual.
 *
 * @author aaron
 */
public class Response {
  public static final int UNDEFINED = -1;
  private String version;
  private String status;
  private int totalRowCount = UNDEFINED;


  /**
   * The status returned by the Factual API server, e.g. "ok".
   * 
   * @return status returned by the Factual API server.
   */
  public String getStatus() {
    return status;
  }

  /**
   * The version tag returned by the Factual API server, e.g. "3".
   * 
   * @return the version tag returned by the Factual API server.
   */
  public String getVersion() {
    return version;
  }

  /**
   * @return total underlying row count, or {@link #UNDEFINED} if unknown.
   */
  public int getTotalRowCount() {
    return totalRowCount;
  }

  /**
   * Parses response metadata from <tt>rootJsonObj</tt> and adds it to <tt>response</tt>
   * 
   * @param response the response object to which to add metadata.
   * @param rootJsonObj the top-level JSON response Object built from a Factual response.
   */
  public static void withMeta(Response resp, JSONObject rootJsonObj) {
    try {
      resp.version = rootJsonObj.getString("version");
      resp.status = rootJsonObj.getString("status");
      JSONObject respJson = rootJsonObj.getJSONObject("response");
      if(respJson.has("total_row_count")) {
        resp.totalRowCount = respJson.getInt("total_row_count");
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

}
