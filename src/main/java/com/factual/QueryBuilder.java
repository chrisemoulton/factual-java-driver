package com.factual;

import com.google.common.base.Joiner;


/**
 * Provides fluent interface to specifying row filter predicate logic.
 * 
 * @author aaron
 */
public class QueryBuilder {
  private final Query query;
  private final String fieldName;


  /**
   * Constructor. Specifies the name of the field for which to build filter
   * logic. Instance methods are used to specify the desired logic.
   */
  public QueryBuilder(Query query, String fieldName) {
    this.query = query;
    this.fieldName = fieldName;
  }

  public Query fullTextSearch(Object arg) {
    return addFilter("$search", arg);
  }

  public Query equal(Object arg) {
    return addFilter("$eq", arg);
  }

  public Query notEqual(Object arg) {
    return addFilter("$neq", arg);
  }

  public Query in(Object... args) {
    return addFilter("$in", "[" + Joiner.on(",").join(args) + "]");
  }

  public Query notIn(Object... args) {
    return addFilter("$nin", "[" + Joiner.on(",").join(args) + "]");
  }

  public Query beginsWith(String arg) {
    return addFilter("$bw", arg);
  }

  public Query notBeginsWith(String arg) {
    return addFilter("$nbw", arg);
  }

  public Query beginsWithAny(Object... args) {
    return addFilter("$bwin", "[" + Joiner.on(",").join(args) + "]");
  }

  public Query notBeginsWithAny(Object... args) {
    return addFilter("$nbwin", "[" + Joiner.on(",").join(args) + "]");
  }

  public Query isBlank() {
    return addFilter("$blank", true);
  }

  public Query isNotBlank() {
    return addFilter("$blank", false);
  }

  public Query greaterThan(Object arg) {
    return addFilter("$gt", arg);
  }

  public Query greaterThanOrEqual(Object arg) {
    return addFilter("$gte", arg);
  }

  public Query lessThan(Object arg) {
    return addFilter("$lt", arg);
  }

  public Query lessThanOrEqual(Object arg) {
    return addFilter("$lte", arg);
  }

  private Query addFilter(String op, Object arg) {
    query.add(new FieldFilter(op, fieldName, arg));
    return query;
  }

}
