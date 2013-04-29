package com.factual.driver;

import java.util.HashMap;

public abstract class Shape {

  /**
   * Can be used by Shape implementations to get the full 'within'
   * structure for a query.
   * 
   * @return the 'within' structure for a query.
   */
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public Object withinStruct() {
    return new HashMap(){{
      put("$within", toJsonObject());  
    }};
  }
  
  /**
   * @return the full JSON representation of this Shape.
   */
  public String toJsonStr() {
    return JsonUtil.toJsonStr(withinStruct());
  }
  
  /**
   * All Shapes must implement this to return a structure that represents
   * the shape with data that can be JSON-ized. 
   * 
   * @return a structure that represents the shape
   */
  public abstract Object toJsonObject();
  
  /**
   * The driver relies on calling toString() to get the representation
   * of Shapes.
   */
  @Override
  public String toString() {
    return toJsonStr();
  }  

}
