package com.factual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Integration tests for the Factual Java driver.
 * 
 * @author aaron
 */
public class FactualTest {
  private static Factual factual;


  @BeforeClass
  public static void connect() throws IOException {
    String key = read("key.txt");
    String secret = read("secret.txt");
    factual = new Factual(key, secret);
  }

  /**
   * Find rows in the global places database in the United States
   */
  @Test
  public void testExample1() {
    Response resp = factual.fetch("places",
        new Query().filter("country", "US"));

    assertOk(resp);
    assertAll(resp, "country", "US");
  }

  /**
   * Find rows in the restaurant database whose name begins with "Star" and
   * return both the data and a total count of the matched rows.
   */
  @Test
  public void testExample2() {
    Response resp = factual.fetch("places", new Query()
    .filter("$bw", "name", "Star")
    .includeRowCount());

    assertOk(resp);
    for(String out : resp.mapStrings("name")) {
      assertTrue(out.startsWith("Star"));
    }
  }

  private static final void assertOk(Response response) {
    assertEquals("ok", response.getStatus());
    assertFalse(response.getData().isEmpty());
  }

  private void assertAll(Response resp, String field, String expected) {
    for(String out : resp.mapStrings(field)) {
      assertEquals(expected, out);
    }
  }

  /**
   * Reads value from named file in src/test/resources
   */
  private static String read(String name) throws IOException {
    return FileUtils.readFileToString(new File("src/test/resources/" + name)).trim();
  }

  public static void main(String[] args) {
    Factual factual = new Factual("YOUR_KEY", "YOUR_SECRET");

    Response response = factual.fetch("places", new Query()
    .fullTextSearch("starbucks")
    .limit(10)
    .includeRowCount(true)
    .offset(8)
    .within(new Circle(34.06021, -118.41828, 5000)));

    System.out.println("status: " + response.getStatus());
    System.out.println("total underlying rows: " + response.getTotalRowCount());
    System.out.println();
    System.out.println("Starbucks Addresses:");
    for(String addr : response.mapStrings("address")) {
      System.out.println(addr);
    }

  }

}
