package com.factual.demo;

import static com.factual.FactualTest.read;

import com.factual.Crosswalk;
import com.factual.CrosswalkQuery;
import com.factual.CrosswalkResponse;
import com.factual.Factual;


public class DemoCrosswalk3 {
  private static String key = read("key.txt");
  private static String secret = read("secret.txt");
  private static Factual factual = new Factual(key, secret);

  /**
   * Get all third party IDs or URIs for The Stand using it's Foursquare ID
   */
  public static void main(String[] args) {
    CrosswalkQuery q = new CrosswalkQuery()
    .namespace("foursquare")
    .namespaceId("4ae4df6df964a520019f21e3");  // Foursquare's id for The Stand

    // Run the query on Factual's "places" table:
    CrosswalkResponse resp = factual.fetch("places", q);

    // Print out the Crosswalk results:
    for(Crosswalk cw : resp.getCrosswalks()) {
      System.out.println(cw.getNamespace() + "\t" + cw.getUrl() + "\t" + cw.getNamespaceId());
    }
  }

}
