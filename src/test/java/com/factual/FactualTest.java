package com.factual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Integration tests for the Factual Java driver. Expects your key and secret to be in:
 * <pre>
 * src/test/resources/key.txt
 * src/test/resources/secret.txt
 * </pre>
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
  public void testCoreExample1() {
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
  public void testCoreExample2() {
    Response resp = factual.fetch("places", new Query()
    .filter("$bw", "name", "Star")
    .includeRowCount());

    assertOk(resp);
    assertStartsWith(resp, "name", "Star");
  }

  /**
   * Do a full-text search of the restaurant databse for rows that match the
   * terms "Fried Chicken, Los Angeles"
   */
  @Test
  public void testCoreExample3() {
    Response resp = factual.fetch("places", new Query()
    .fullTextSearch("Fried Chicken, Los Angeles"));

    assertOk(resp);
  }

  /**
   * To support paging in your app, return rows 20-25 of the full-text search result
   * from Example 3
   */
  @Test
  public void testCoreExample4() {
    Response resp = factual.fetch("places", new Query()
    .fullTextSearch("Fried Chicken, Los Angeles")
    .offset(20)
    .limit(5));

    assertOk(resp);
    assertEquals(5, resp.getData().size());
  }

  /**
   * Return rows from the global places database with a name equal to "Stand"
   * within 5000 meters of the specified lat/lng
   */
  @Test
  public void testCoreExample5() {
    Response resp = factual.fetch("places", new Query()
    .filter("name", "Stand")
    .within(new Circle(34.06018, -118.41835, 5000)));

    assertOk(resp);
  }

  /**
   * {"$and":[{"name":{"$bw":"McDonald's"},"category":{"$bw":"Food & Beverage"}}]}
   */
  @Test
  public void testRowFilters_2beginsWith() {
    Response resp = factual.fetch("places", new Query()
    .filter("$bw", "name", "McDonald's")
    .filter("$bw", "category", "Food & Beverage"));

    assertOk(resp);
    assertStartsWith(resp, "name", "McDonald");
    assertStartsWith(resp, "category", "Food & Beverage");
  }

  /**
   * {"$or":[{"tel":{"$blank":true}},{"tel":{"$bw":"(212)"}}]}
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testRowFilters_2_ORs() {
    Pred tel = new Pred("$blank", "tel", true);
    Pred bw = new Pred("$bw", "name", "Star");
    Pred starOrTelBlank = new Pred("$or", tel, bw);

    Response resp = factual.fetch("places", new Query()
    .filter(starOrTelBlank));

    assertOk(resp);
  }

  /**
   * Tests a top-level AND with a nested OR and an $in:
   * 
   * <pre>
   * {$and:[
   *   {region:{$in:["MA","VT","NH"]}},
   *   {$or:[
   *     {name:{$bw:"Star"}},
   *     {name:{$bw:"Coffee"}}]}]}
   * </pre>
   */
  @Test
  public void testComplicated() {
    Pred in = new Pred("$in", "region", "MA", "VT", "NH");
    Pred bwStar = new Pred("$bw", "name", "Star");
    Pred bwCoffee = new Pred("$bw", "name", "Coffee");
    Pred orStarCoffee = new Pred("$or", bwStar, bwCoffee);

    Pred complicated = new Pred("$and", in, orStarCoffee);

    Response resp = factual.fetch("places", new Query()
    .filter(complicated));

    assertOk(resp);
  }

  @Test
  @Ignore("Until our server-side parser properly handles ( and )")
  public void testSimpleTel() {
    Pred tel = new Pred("$bw", "tel", "(212)");

    Response resp = factual.fetch("places", new Query()
    .filter(tel));

    assertOk(resp);
  }

  private static final void assertOk(Response resp) {
    assertEquals("ok", resp.getStatus());
    assertFalse(resp.getData().isEmpty());
  }

  private void assertAll(Response resp, String field, String expected) {
    for(String out : resp.mapStrings(field)) {
      assertEquals(expected, out);
    }
  }

  private void assertStartsWith(Response resp, String field, String substr) {
    for(String out : resp.mapStrings(field)) {
      assertTrue(out.startsWith(substr));
    }
  }

  /**
   * Reads value from named file in src/test/resources
   */
  private static String read(String name) throws IOException {
    return FileUtils.readFileToString(new File("src/test/resources/" + name)).trim();
  }

}
