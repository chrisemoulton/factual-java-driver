package com.factual.demo;

import static com.factual.driver.FactualTest.factual;

import java.util.Map;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;

public class DemoCrosswalk2 {
  private static Factual factual = factual();

  public static void main(String[] args) {
    // Get Loopt's Crosswalk data for a specific Places entity, using its
    // Factual ID:

    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id")
        .isEqual("97598010-433f-4946-8fd5-4a6dd1639d77").field("namespace")
        .isEqual("loopt"));

    // Print out the Crosswalk results:
    for (Map<String, Object> cw : resp.getData()) {
      System.out.println(cw);
    }
  }

}
