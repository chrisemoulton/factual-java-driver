package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoRowFilterNestedOrAnd {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Build a query to find entities where:
    // (name begins with "Starbucks") OR (name begins with "Coffee")
    // OR
    // (name full text search matches on "tea" AND tel is not blank)
    Query q = new Query();
    q.or(
        q.or(
            q.criteria("name").beginsWith("Starbucks"),
            q.criteria("name").beginsWith("Coffee")
        ),
        q.and(
            q.criteria("name").search("tea"),
            q.criteria("tel").notBlank()
        )
    );

    System.out.println(
        factual.fetch("places", q));
  }

}
