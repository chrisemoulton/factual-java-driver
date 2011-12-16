package com.factual.demo;

import static com.factual.FactualTest.read;

import com.factual.Circle;
import com.factual.Factual;
import com.factual.Query;


public class DemoGeoQuery {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Build a Query that finds entities located within 5000 meters of a latitude, longitude.
    // Sort results by distance, ascending:
    Query q = new Query()
    .within(new Circle(34.06018, -118.41835, 5000))
    .sortAsc("$distance");

    System.out.println(
        factual.fetch("places", q));

  }

}
