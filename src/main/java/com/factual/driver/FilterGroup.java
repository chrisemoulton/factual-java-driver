package com.factual.driver;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;


/**
 * Represents a group of Filters as one Filter.
 * 
 * @author aaron
 */
public class FilterGroup implements Filter {
  private final List<Filter> filters;
  private String op = "$and";


  /**
   * Constructor. Defaults logic to AND.
   */
  public FilterGroup(List<Filter> filters) {
    this.filters = filters;
  }

  /**
   * Constructor. Defaults logic to AND.
   */
  public FilterGroup(Filter... filters) {
    this.filters = Lists.newArrayList();
    for(Filter f : filters) {
      this.filters.add(f);
    }
  }

  /**
   * Sets this FilterGroup's logic, e.g., "$or".
   */
  public FilterGroup op(String op) {
    this.op = op;
    return this;
  }

  /**
   * Sets this FilterGroup's logic to be OR.
   */
  public FilterGroup asOR() {
    return op("$or");
  }

  public void add(Filter filter) {
    filters.add(filter);
  }

  /**
   * Produces JSON representation for this FilterGroup
   * <p>
   * For example:
   * <pre>
   * {"$and":[{"first_name":{"$eq":"Bradley"}},{"region":{"$eq":"CA"}},{"locality":{"$eq":"Los Angeles"}}]}
   * </pre>
   */
  @Override
  public String toJsonStr() {
    return "{\"" + op + "\":[" + logicJsonStr() + "]}";
  }

  private String logicJsonStr() {
    List<String> logics = Lists.newArrayList();
    for(Filter f : filters) {
      logics.add(f.toJsonStr());
    }
    return Joiner.on(",").join(logics);
  }

}
