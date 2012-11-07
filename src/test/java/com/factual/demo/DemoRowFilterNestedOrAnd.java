package com.factual.demo;

import static com.factual.driver.FactualTest.factual;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoRowFilterNestedOrAnd {

  public static void main(String[] args) {
    Factual factual = factual();

    // Build a query to find entities where:
    // (name begins with "Starbucks") OR (name begins with "Coffee")
    // OR
    // (name full text search matches on "tea" AND tel is not blank)
    Query q = new Query();
    q.or(
        q.or(
            q.field("name").beginsWith("Starbucks"),
            q.field("name").beginsWith("Coffee")
        ),
        q.and(
            q.field("name").search("tea"),
            q.field("tel").notBlank()
        )
    );

    System.out.println(
        factual.fetch("places", q));
  }

}
