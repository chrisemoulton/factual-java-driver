package com.factual.demo;

import static com.factual.FactualTest.read;

import com.factual.CrosswalkQuery;
import com.factual.Factual;


public class DemoCrosswalkBuenaVista {
  private static String key = read("key.txt");
  private static String secret = read("secret.txt");
  private static Factual factual = new Factual(key, secret);

  public static void main(String[] args) {

    // Get Facebook Crosswalk data for The Buena Vista Social club, using its Foursquare ID
    System.out.println(
        factual.fetch("places", new CrosswalkQuery()
        .namespace("foursquare")
        .namespaceId("4ae4df6df964a520019f21e3")
        .only("facebook")
        ).getJson());
  }

}
