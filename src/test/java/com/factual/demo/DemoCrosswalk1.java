package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Crosswalk;
import com.factual.driver.CrosswalkQuery;
import com.factual.driver.CrosswalkResponse;
import com.factual.driver.Factual;


public class DemoCrosswalk1 {
  private static String key = read("key.txt");
  private static String secret = read("secret.txt");
  private static Factual factual = new Factual(key, secret);

  public static void main(String[] args) {
    // Get all Crosswalk data for a specific Places entity, using its on Factual ID:
    CrosswalkResponse resp = factual.fetch("places",
        new CrosswalkQuery().factualId("97598010-433f-4946-8fd5-4a6dd1639d77"));

    for(Crosswalk cw : resp.getCrosswalks()) {
      System.out.println(cw);
    }
  }

}
