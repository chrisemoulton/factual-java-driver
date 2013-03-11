package com.factual.driver;

import java.util.Map;

/**
 * Represents the response from running a fetch row request against Factual.
 *
 * @author brandon
 */
public class RowResponse extends ReadResponse {

  /**
   * Constructor, parses from a JSON response String.
   * 
   * @param json the JSON response String returned by Factual.
   */
  public RowResponse(InternalResponse resp) {
    super(resp);
  }

  public boolean isDeprected() {
    return resp.getStatusCode() == 301;
  }

  public Map<String, Object> getRowData() {
    return first();
  }

}
