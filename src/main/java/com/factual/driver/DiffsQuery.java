package com.factual.driver;

import java.util.Map;

/**
 * Represents a Factual Diffs query.
 * 
 * @author brandon
 * 
 */
public class DiffsQuery {

  /**
   * Constructor. Create a request to find diffs on a Factual table between two
   * times.
   * 
   * @param before
   *          the before time to create this diff against.
   */
  public DiffsQuery(long before) {
    before(before);
  }

  /**
   * The before time to create this diff against.
   * 
   * @param timestamp
   *          before time for this diff.
   * @return this DiffsQuery
   */
  public DiffsQuery before(long timestamp) {
    addParam(Constants.DIFFS_START_DATE, timestamp);
    return this;
  }

  /**
   * The after time to create this diff against.
   * 
   * @param timestamp
   *          after time for this diff.
   * @return this DiffsQuery
   */
  public DiffsQuery after(long timestamp) {
    addParam(Constants.DIFFS_END_DATE, timestamp);
    return this;
  }

  /**
   * Holds all parameters for this DiffsQuery.
   */
  private final Parameters queryParams = new Parameters();

  public String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams();
  }

  /**
   * Set a parameter and value pair for specifying url parameters, specifically
   * those not yet available as convenience methods.
   * 
   * @param key
   *          the field name of the parameter to add
   * @param value
   *          the field value that will be serialized using value.toString()
   * @return this DiffsQuery
   */
  private DiffsQuery addParam(String key, Object value) {
    queryParams.setParam(key, value);
    return this;
  }
}