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
   */
  public RowResponse(InternalResponse resp) {
    super(resp);
  }

  public boolean isDeprecated() {
    return resp.getStatusCode() == 301;
  }

  public Map<String, Object> getRowData() {
    return first();
  }

}
