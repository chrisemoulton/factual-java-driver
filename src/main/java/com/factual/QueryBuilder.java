package com.factual;

import com.google.common.base.Joiner;

public class QueryBuilder {
  private final Query query;
  private final String fieldName;


  public QueryBuilder(Query query, String fieldName) {
    this.query = query;
    this.fieldName = fieldName;
  }

  public Query equal(Object arg) {
    return addFilter("$eq", arg);
  }

  public Query startsWith(String arg) {
    return addFilter("$bw", arg);
  }

  public Query isBlank() {
    return addFilter("$blank", true);
  }

  public Query in(Object... args) {
    return addFilter("$in", "[" + Joiner.on(",").join(args) + "]");
  }

  private Query addFilter(String op, Object arg) {
    query.add(new FieldFilter(op, fieldName, arg));
    return query;
  }

}
