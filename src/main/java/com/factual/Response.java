package com.factual;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Represents the basic concept of a response from Factual.
 *
 * @author aaron
 */
public abstract class Response {
  public static final int UNDEFINED = -1;
  private String version;
  private String status;
  private int totalRowCount = UNDEFINED;
  private int includedRows;


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
   * @return amount of result rows returned in this response.
   */
  public int getIncludedRowCount() {
    return includedRows;
  }

  /**
   * @return true if Factual's response did not include any results records for
   *         the query, false otherwise.
   */
  public boolean isEmpty() {
    return includedRows == 0;
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
      if(respJson.has("included_rows")) {
        resp.includedRows = respJson.getInt("included_rows");
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return getJson();
  }

  /**
   * Subclasses of Response must provide access to the original JSON
   * representation of Factual's response.
   * 
   * @return the original JSON representation of Factual's response.
   */
  public abstract String getJson();

}
