package com.factual.driver;

import java.util.Map;

/**
 * Represents an insert request to add or update a Factual row.
 * 
 * @author brandon
 */
public class Insert {

  /**
   * Holds all parameters for this Insert.
   */
  private final Parameters queryParams = new Parameters();

  public Insert() {
  }

  /**
   * Constructor for a insert with values initialized as key value pairs in
   * mapping.
   * 
   * @param values
   *          values this submit is initialized with
   */
  public Insert(Map<String, Object> values) {
    for (String key : values.keySet())
      setValue(key, values.get(key));
  }

  protected String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }

  /**
   * Set the value for a single field in this insert request. Added to a JSON
   * hash of field names and values to be added to a Factual table.
   * 
   * @param field
   *          the field name
   * @param value
   *          the value for the specified field
   * @return this Insert
   */
  public Insert setValue(String field, Object value) {
    queryParams.setJsonMapParam(Constants.INSERT_VALUES, field, value);
    return this;
  }

  /**
   * Set the value to null for a single field in this insert request.
   * 
   * @param field
   *          the field to set as empty, or null.
   * @return this Insert
   */
  public Insert removeValue(String field) {
    setValue(field, null);
    return this;
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams(null);
  }

}
