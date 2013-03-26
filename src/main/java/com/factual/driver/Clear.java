package com.factual.driver;

import java.util.List;
import java.util.Map;

public class Clear {
  private final Parameters queryParams = new Parameters();

  public Clear() {
  }

  /**
   * Constructor for a submit with values initialized as key value pairs in
   * mapping.
   */
  public Clear(List<String> fields) {
    for (String field : fields)
      addField(field);
  }

  protected String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }

  /**
   * Add the name of a field to be cleared in this request
   * 
   * @param field
   *          the field name
   * @return this Clear
   */
  public Clear addField(String field) {
    queryParams.addCommaSeparatedParam(Constants.CLEAR_FIELDS, field);
    return this;
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams(null);
  }
}
