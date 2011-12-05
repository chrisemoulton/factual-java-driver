package com.factual.demo;

import static com.factual.FactualTest.read;

import java.util.Map;

import com.factual.Factual;
import com.factual.Query;
import com.factual.ReadResponse;


public class DemoSimpleRead {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Create a simple query to get 3 random records
    Query q = new Query().limit(3);

    // Run the query on Factual's "places" table
    ReadResponse resp = factual.fetch("places", q);

    // Print out each record
    for(Map record : resp.getData()) {
      System.out.println(record);
    }

  }

}
