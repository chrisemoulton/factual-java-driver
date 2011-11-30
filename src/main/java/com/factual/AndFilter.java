package com.factual;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;


public class AndFilter implements Filter {
  private final List<Filter> filters;


  public AndFilter(List<Filter> filters) {
    this.filters = filters;
  }

  public AndFilter(Filter... filters) {
    this.filters = Lists.newArrayList();
    for(Filter f : filters) {
      this.filters.add(f);
    }
  }

  public void add(Filter filter) {
    filters.add(filter);
  }

  /**
   * Produces JSON representation of the represented AND filter logic.
   * <p>
   * For example:
   * <pre>
   * {"$and":[{"first_name":{"$eq":"Bradley"}},{"region":{"$eq":"CA"}},{"locality":{"$eq":"Los Angeles"}}]}
   * </pre>
   */
  @Override
  public String toJsonStr() {
    return "{\"$and\":[" + logicJsonStr() + "]}";
  }

  private String logicJsonStr() {
    List<String> logics = Lists.newArrayList();
    for(Filter f : filters) {
      logics.add(f.toJsonStr());
    }
    return Joiner.on(",").join(logics);
  }

}
