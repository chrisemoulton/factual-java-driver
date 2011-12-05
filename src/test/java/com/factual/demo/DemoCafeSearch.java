package com.factual.demo;

import static com.factual.FactualTest.read;

import java.util.Map;

import com.factual.Circle;
import com.factual.Factual;
import com.factual.Query;
import com.factual.ReadResponse;


public class DemoCafeSearch {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    Query q = new Query()
    .only("name", "address")
    .search("cafe")
    .within(new Circle(34.06018, -118.41835, 5000))
    .field("postcode").equal("90067")
    .field("category").equal("Food & Beverage")
    .limit(25);

    // Run the query on Factual's "places" table
    ReadResponse resp = factual.fetch("places", q);

    // Print out each record
    for(Map record : resp.getData()) {
      System.out.println(record);
    }

  }

}
