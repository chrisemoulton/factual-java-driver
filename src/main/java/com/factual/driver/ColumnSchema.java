package com.factual.driver;

import java.util.Map;

public class ColumnSchema {
  public final String name;
  public final String description;
  public final boolean faceted;
  public final boolean sortable;
  public final String label;
  public final String datatype;
  public final boolean searchable;

  /**
   * Constructor. Maps raw column schema data into a new ColumnSchema.
   * 
   * @param map
   *          A column schema map object as provided by Factual.
   */
  public ColumnSchema(Map<String, Object> map) {
    name = (String)map.get("name");
    description = (String)map.get("description");
    label = (String)map.get("label");
    datatype = (String)map.get("datatype");
    faceted = (Boolean)map.get("faceted");
    sortable = (Boolean)map.get("sortable");
    searchable = (Boolean)map.get("searchable");
  }

}
