package com.factual;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Represents a logical predicate to be used when filtering. Supports
 * arbitrarily complex nesting with other Preds.
 * <p>
 * Knows how to represent itself as JSON, ready to be used as filters in
 * Factual's public API.
 * 
 * @author aaron
 */
public class Pred {
  private final String op;
  private final Object[] args;
  private final ObjectMapper objectMapper = new ObjectMapper();


  public Pred(String op, Object... args) {
    this.op = op;
    this.args = args;
  }

  /**
   * @return the JSON representation of this Pred
   */
  public String toJsonStr() {
    if(isSpecialForm()) {
      return "{\"" + op + "\":" + toString(args) + "}";
    } else {
      return "{\"" + args[0] + "\":{\"" + op + "\":" + toString(rest(args)) + "}}";
    }
  }

  private Object[] rest(Object...objs) {
    return Arrays.copyOfRange(objs, 1, objs.length);
  }

  private boolean isSpecialForm() {
    return "$and".equals(op) || "$or".equals(op);
  }

  private String toString(Object... objs) {
    if(objs.length == 1) {
      return toJsonStrReflect(objs[0]);
    } else {
      List<String> elems = Lists.newArrayList();
      for(Object obj : objs) {
        elems.add(toJsonStrReflect(obj));
      }
      return "[" + Joiner.on(",").join(elems) + "]";
    }
  }

  private String toJsonStrReflect(Object obj) {
    if (obj instanceof Pred) {
      return ((Pred)obj).toJsonStr();
    } else {
      return toJsonStr(obj);
    }
  }

  private String toJsonStr(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonGenerationException e) {
      throw new RuntimeException(e);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}