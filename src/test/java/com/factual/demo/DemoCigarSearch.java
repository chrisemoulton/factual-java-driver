package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;


public class DemoCigarSearch {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    ReadResponse cigars = factual.fetch("places", new Query()
    .search("cigars")
    .near("1801 avenue of the stars, century city, ca", 5000));

    System.out.println(cigars.getJson());
  }

}
