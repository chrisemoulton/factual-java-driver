package com.factual.driver;

import java.util.Map;

public class MatchQuery {

  /**
   * Holds all parameters.
   */
  private final Parameters queryParams = new Parameters();

  public MatchQuery add(String key, Object val) {
    queryParams.setJsonMapParam(Constants.RESOLVE_VALUES, key, val);
    return this;
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams();
  }

  protected String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }
}
