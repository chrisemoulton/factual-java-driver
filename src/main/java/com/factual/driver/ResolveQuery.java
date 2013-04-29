package com.factual.driver;

import java.util.Map;

public class ResolveQuery {

  /**
   * Holds all parameters for this ResolveQuery.
   */
  private final Parameters queryParams = new Parameters();
  
  public ResolveQuery add(String key, Object val) {
    queryParams.setJsonMapParam(Constants.RESOLVE_VALUES, key, val);
    return this;
  }
  
  /**
   * When this is called before running the query, Resolve will return all 
   * potential candidates for resolution and meta-data regarding resolution quality.
   */
  public ResolveQuery allCandidates() {
	  debug(true);
	  return this;
  }
  
  /**
   * Whether to turn on debug for this Resolve query.
   * 
   * When debug is turned on, Resolve will return all potential candidates for
   * resolution and meta-data regarding resolution quality. When debug is turned
   * off (the default), Resolve returns 0 results (meaning no match), or 1 result
   * (meaning that was the unequivocal match).
   */
  public ResolveQuery debug(boolean dbg) {
    if(dbg) {
	  queryParams.setParam("debug", true);
    } else {
      queryParams.unsetParam("debug");
    }
    return this;
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams();
  }

  protected String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }
}
