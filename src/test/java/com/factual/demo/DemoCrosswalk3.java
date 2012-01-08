package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Crosswalk;
import com.factual.driver.CrosswalkQuery;
import com.factual.driver.CrosswalkResponse;
import com.factual.driver.Factual;


public class DemoCrosswalk3 {
  private static String key = read("key.txt");
  private static String secret = read("secret.txt");
  private static Factual factual = new Factual(key, secret);

  public static void main(String[] args) {

    // Get all Crosswalk data for a specific Places entity, using its Foursquare ID
    CrosswalkResponse resp = factual.fetch("places",
        new CrosswalkQuery()
    .namespace("foursquare")
    .namespaceId("4ae4df6df964a520019f21e3"));

    // Print out the Crosswalk results:
    for(Crosswalk cw : resp.getCrosswalks()) {
      System.out.println(cw.getNamespace() + "\t" + cw.getUrl() + "\t" + cw.getNamespaceId());
    }
  }

}
