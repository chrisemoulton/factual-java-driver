package com.factual.driver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Represents a Factual row query. Knows how to represent the query as URL
 * encoded key value pairs, ready for the query string in a GET request. (See
 * {@link #toUrlQuery()})
 * 
 * @author brandon
 */
public class RowQuery {

  protected final Parameters queryParams = new Parameters();

  /**
   * Sets the fields to select. This is optional; default behaviour is generally
   * to select all fields in the schema.
   * 
   * @param fields
   *          the fields to select.
   * @return this Query
   */
  public RowQuery only(String... fields) {
    for (String field : fields) {
      queryParams.addCommaSeparatedParam(Constants.QUERY_SELECT, field);
    }
    return this;
  }

  /**
   * @return array of select fields set by only(), null if none.
   */
  public String[] getSelectFields() {
    return queryParams.getCommaSeparatedParam(Constants.QUERY_SELECT);
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams(null);
  }

  @Override
  public String toString() {
    try {
      return URLDecoder.decode(UrlUtil.toUrlQuery(toUrlParams()), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }

}