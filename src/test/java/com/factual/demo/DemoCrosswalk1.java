package com.factual.demo;

import static com.factual.FactualTest.read;

import com.factual.Crosswalk;
import com.factual.CrosswalkQuery;
import com.factual.CrosswalkResponse;
import com.factual.Factual;


public class DemoCrosswalk1 {
  private static String key = read("key.txt");
  private static String secret = read("secret.txt");
  private static Factual factual = new Factual(key, secret);

  public static void main(String[] args) {
    // Build a Crosswalk query to get data for a specific Factual entity:
    CrosswalkQuery q = new CrosswalkQuery();
    q.factualId("97598010-433f-4946-8fd5-4a6dd1639d77");

    // Run the query on Factual's "places" table:
    CrosswalkResponse resp = factual.fetch("places", q);

    // Print out the Crosswalk results:
    for(Crosswalk cw : resp.getCrosswalks()) {
      System.out.println(cw);
    }
  }

}
