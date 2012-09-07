package com.factual.demo;

import static com.factual.driver.FactualTest.factual;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoRowFilterOr {

  public static void main(String[] args) {
    Factual factual = factual();

    // Build a query to find entities where the name begins with "Coffee" OR the telephone is blank:
    Query q = new Query();
    q.or(
        q.field("name").beginsWith("Coffee"),
        q.field("tel").blank());

    System.out.println(
        factual.fetch("places", q));
  }

}
