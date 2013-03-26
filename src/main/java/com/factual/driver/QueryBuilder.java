package com.factual.driver;

import java.util.List;

/**
 * Provides fluent interface to specifying row filter predicate logic.
 * 
 * @author brandon
 */
public class QueryBuilder<T extends Filterable> {
  protected final T query;
  protected final String fieldName;

  /**
   * Constructor. Specifies the name of the field for which to build filter
   * logic. Instance methods are used to specify the desired logic.
   */
  public QueryBuilder(T query, String fieldName) {
    this.query = query;
    this.fieldName = fieldName;
  }

  /**
   * Specifies a full text search.
   * 
   * @param arg
   *          the term(s) for which to full text search against.
   * @return the represented query, with the specified full text search added
   *         in.
   */
  public T search(Object arg) {
    return addFilter("$search", arg);
  }

  /**
   * @deprecated use {@link #isEqual(Object)}
   */
  @Deprecated
  public T equal(Object arg) {
    return addFilter("$eq", arg);
  }

  public T isEqual(Object arg) {
    return addFilter("$eq", arg);
  }

  public T notEqual(Object arg) {
    return addFilter("$neq", arg);
  }

  public T includesAnyList(List<Object> args) {
    return addFilter("$includes_any", args);
  }

  public T includesAny(Object... args) {
    return addFilter("$includes_any", args);
  }

  public T includes(Object arg) {
    return addFilter("$includes", arg);
  }

  public T inList(List<Object> args) {
    return addFilter("$in", args);
  }

  public T in(Object... args) {
    return addFilter("$in", args);
  }

  public T notInList(List<Object> args) {
    return addFilter("$nin", args);
  }

  public T notIn(Object... args) {
    return addFilter("$nin", args);
  }

  public T beginsWith(String arg) {
    return addFilter("$bw", arg);
  }

  public T notBeginsWith(String arg) {
    return addFilter("$nbw", arg);
  }

  public T beginsWithAny(Object... args) {
    return addFilter("$bwin", args);
  }

  public T notBeginsWithAny(Object... args) {
    return addFilter("$nbwin", args);
  }

  public T blank() {
    return addFilter("$blank", true);
  }

  public T notBlank() {
    return addFilter("$blank", false);
  }

  public T greaterThan(Object arg) {
    return addFilter("$gt", arg);
  }

  public T greaterThanOrEqual(Object arg) {
    return addFilter("$gte", arg);
  }

  public T lessThan(Object arg) {
    return addFilter("$lt", arg);
  }

  public T lessThanOrEqual(Object arg) {
    return addFilter("$lte", arg);
  }

  protected T addFilter(String op, Object arg) {
    query.add(new FieldFilter(op, fieldName, arg));
    return query;
  }

  @Override
  public boolean equals(Object e){
    System.err.println("WARNING: this does not act as an equality filter.  If you intend to do this, use .isEqual(Object) instead.");
    return super.equals(e);
  }


}