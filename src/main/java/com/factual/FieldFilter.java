package com.factual;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class FieldFilter implements Filter {
  private final String fieldName;
  private final String op;
  private final Object arg;


  public FieldFilter(String op, String fieldName, Object arg) {
    this.op = op;
    this.fieldName = fieldName;
    this.arg = arg;
  }

  /**
   * Produces JSON representation of the represented filter logic.
   * <p>
   * For example:
   * <pre>
   * {"first": {"$eq":"Jack"}}
   * {"first": {"$in":"a, b, c"}}
   * </pre>
   */
  @Override
  public String toJsonStr() {
    return "{\"" + fieldName + "\":{\"" + op + "\":" + toJsonStr(arg) + "}}";
  }

  private String toJsonStr(Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (JsonGenerationException e) {
      throw new RuntimeException(e);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
