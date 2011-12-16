package com.factual.demo;

import static com.factual.FactualTest.read;

import java.util.Map;

import com.factual.Factual;
import com.factual.ReadResponse;
import com.factual.ResolveQuery;


public class DemoResolve {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Get all entities that are possibly a match
    ReadResponse resp = factual.resolves(new ResolveQuery()
    .add("name", "Buena Vista")
    .add("latitude", 34.06)
    .add("longitude", -118.40));

    for(Map<?, ?> rec : resp.getData()) {
      System.out.println(rec);
    }

    System.out.println("---");

    // Get the entity that is a full match, or null:
    System.out.println(
        factual.resolve(new ResolveQuery()
        .add("name", "Buena Vista")
        .add("latitude", 34.06)
        .add("longitude", -118.40)));
  }

}
