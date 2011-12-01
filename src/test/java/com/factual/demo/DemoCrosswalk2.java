package com.factual.demo;

import static com.factual.FactualTest.read;

import com.factual.Crosswalk;
import com.factual.CrosswalkQuery;
import com.factual.CrosswalkResponse;
import com.factual.Factual;


public class DemoCrosswalk2 {
  private static String key = read("key.txt");
  private static String secret = read("secret.txt");
  private static Factual factual = new Factual(key, secret);

  /**
   * Get all Crosswalk data for The Stand, but for just the Loopt namespace
   */
  public static void main(String[] args) {
    CrosswalkQuery q = new CrosswalkQuery()
    .factualId("97598010-433f-4946-8fd5-4a6dd1639d77")  // The Stand restaurant
    .only("loopt");

    // Run the query on Factual's "places" table:
    CrosswalkResponse resp = factual.fetch("places", q);

    // Print out the Crosswalk results:
    for(Crosswalk cw : resp.getCrosswalks()) {
      System.out.println(cw);
    }
  }

}
