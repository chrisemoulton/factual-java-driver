package com.factual.demo;

import static com.factual.driver.FactualTest.factual;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoSimpleRead {

  public static void main(String[] args) {
    Factual factual = factual();

    System.out.println(
        factual.fetch("places", new Query().limit(3)));
  }

}
