package com.factual.driver;

import java.util.HashMap;

/**
 * General representation of a Shape, for the purpose of geo filters.
 * 
 * @author aaron
 */
public abstract class Shape {

  /**
   * The driver relies on calling toString() to get the representation
   * of Shapes.
   */
  @Override
  public String toString() {
    return toJsonStr();
  }

  /**
   * @return the full JSON representation of this Shape.
   */
  private String toJsonStr() {
    return JsonUtil.toJsonStr(withinStruct());
  }

  /**
   * Can be used by Shape implementations to get the full 'within'
   * structure for a query.
   * 
   * @return the 'within' structure for a query.
   */
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  private Object withinStruct() {
    return new HashMap(){{
      put("$within", toJsonObject());
    }};
  }

  /**
   * All Shapes must implement this to return a structure that represents
   * the shape with data that can be JSON-ized.
   * 
   * @return a structure that represents the shape
   */
  public abstract Object toJsonObject();

}
