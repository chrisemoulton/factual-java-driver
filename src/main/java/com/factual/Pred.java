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


  /**
   * Constructs a predicate. Operator always comes first, e.g. "$and", "$or",
   * "$in", "$eq", etc.
   * <p>
   * After the operator can be any number of args, which will
   * be handled properly depending on the operator.
   * <p>
   * Preds can be nested within each other (e.g., for "$and"s and "$or"s).
   * <p>
   * Examples:
   * 
   * <pre>new Pred("$eq", "first_name", "Chun")</pre>
   * 
   * <pre>new Pred("$in", "region", "MA", "VT", "NH")</pre>
   * 
   * <pre>new Pred("$or",
   *                      new Pred("$eq", "first_name", "Chun")
   *                      new Pred("$eq", "last_name", "Kok"))</pre>
   * 
   * 
   * @param op
   *          the operator, e.g. "$and", "$or", "$in", "$eq", etc.
   * @param args
   *          an arbitrary number of args, to be handled properly depending on
   *          the operator.
   */
  public Pred(String op, Object... args) {
    this.op = op;
    this.args = args;
  }

  /**
   * @return the JSON representation of this Pred
   */
  public String toJsonStr() {
    if(isSpecialForm()) {
      return "{\"" + op + "\":" + complexList(args) + "}";
    } else {
      return "{\"" + args[0] + "\":{\"" + op + "\":" + simpleList(rest(args)) + "}}";
    }
  }

  private Object[] rest(Object...objs) {
    return Arrays.copyOfRange(objs, 1, objs.length);
  }

  private boolean isSpecialForm() {
    return "$and".equals(op) || "$or".equals(op);
  }

  private String complexList(Object... objs) {
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

  /**
   * Handles lists like the value for $in, e.g. for...
   * 
   * <pre>
   * "$in", "region", "MA", "VT", "NH"
   * </pre>
   * 
   * ... so that "MA", "VT", and "NH" would be <tt>objs</tt>, and would turn
   * into: "MA, VT, NH".
   * <p>
   * This special case is required, since our public API doesn't treat the
   * argument to $in as a real JSON list.
   */
  private String simpleList(Object... objs) {
    if(objs.length == 1) {
      return toJsonStrReflect(objs[0]);
    } else {
      return "\"" + Joiner.on(",").join(objs) + "\"";
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