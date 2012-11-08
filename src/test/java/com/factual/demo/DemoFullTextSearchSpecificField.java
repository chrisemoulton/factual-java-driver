package com.factual.demo;

import static com.factual.driver.FactualTest.factual;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoFullTextSearchSpecificField {

  public static void main(String[] args) {
    Factual factual = factual();

    // Build a query to full text search against the name field:
    Query q = new Query().field("name").search("Fried Chicken");

    System.out.println(
        factual.fetch("places", q));
  }

}
