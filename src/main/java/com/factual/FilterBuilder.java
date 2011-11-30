package com.factual;

import com.google.common.base.Joiner;

public class FilterBuilder {
  private final String fieldName;


  public FilterBuilder(String fieldName) {
    this.fieldName = fieldName;
  }

  public FieldFilter equal(Object arg) {
    return newFilter("$eq", arg);
  }

  public FieldFilter startsWith(String arg) {
    return newFilter("$bw", arg);
  }

  public FieldFilter isBlank() {
    return newFilter("$blank", true);
  }

  public FieldFilter in(Object... args) {
    return newFilter("$in", "[" + Joiner.on(",").join(args) + "]");
  }

  private FieldFilter newFilter(String op, Object arg) {
    return new FieldFilter(op, fieldName, arg);
  }

}
