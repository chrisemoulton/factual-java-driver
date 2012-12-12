package com.factual.driver;

import java.util.Date;
import java.util.Map;

/**
 * Represents a Factual Diffs query.
 * 
 * @author brandon
 * 
 */
public class DiffsQuery {

  public DiffsQuery() {

  }

  /**
   * Constructor. Create a request to find diffs on a Factual table.
   * 
   * @param after
   *          diffs returned which were generated after the given time.
   */
  public DiffsQuery(long after) {
    after(after);
  }

  /**
   * Constructor. Create a request to find diffs on a Factual table.
   * 
   * @param after
   *          diffs returned which were generated after the given time.
   */
  public DiffsQuery(Date after) {
    after(after);
  }

  private boolean isValidTimestamp(long timestamp) {
    return (timestamp > 1325376000000L);
  }

  /**
   * Diffs returned which were generated before the given date
   * 
   * @param date
   *          diffs returned which were generated before the given date.
   * @return this DiffsQuery
   */
  public DiffsQuery before(Date date) {
    return before(date.getTime());
  }

  /**
   * Diffs returned which were generated after the given date
   * 
   * @param date
   *          diffs returned which were generated after the given date.
   * @return this DiffsQuery
   */
  public DiffsQuery after(Date date) {
    return after(date.getTime());
  }

  /**
   * Diffs returned which were generated before the given timestamp
   * 
   * @param timestamp
   *          diffs returned which were generated before the given timestamp.
   * @return this DiffsQuery
   */
  public DiffsQuery before(long timestamp) {
    if (!isValidTimestamp(timestamp))
      throw new RuntimeException(
      "Invalid timestamp.  Please use milliseconds for a date later than Jan 1, 2012 (1325376000000 ms).");
    addParam(Constants.DIFFS_END_DATE, timestamp);
    return this;
  }

  /**
   * Diffs returned which were generated after the given timestamp
   * 
   * @param timestamp
   *          diffs returned which were generated after the given timestamp.
   * @return this DiffsQuery
   */
  public DiffsQuery after(long timestamp) {
    if (!isValidTimestamp(timestamp))
      throw new RuntimeException(
      "Invalid timestamp.  Please use milliseconds for a date later than Jan 1, 2012 (1325376000000 ms).");
    addParam(Constants.DIFFS_START_DATE, timestamp);
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
