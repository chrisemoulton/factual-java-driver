package com.factual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
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
    ReadResponse resp = factual.read("places",
        new Query().field("country").equal("US"));

    assertOk(resp);
    assertAll(resp, "country", "US");
  }

  /**
   * Find rows in the restaurant database whose name begins with "Star" and
   * return both the data and a total count of the matched rows.
   */
  @Test
  public void testCoreExample2() {
    ReadResponse resp = factual.read("places", new Query()
    .field("name").beginsWith("Star")
    .includeRowCount());

    assertOk(resp);
    assertStartsWith(resp, "name", "Star");
  }

  /**
   * Do a full-text search of the restaurant database for rows that match the
   * terms "Fried Chicken, Los Angeles"
   */
  @Test
  public void testCoreExample3() {
    ReadResponse resp = factual.read("places", new Query()
    .fullTextSearch("Fried Chicken, Los Angeles"));

    assertOk(resp);
  }

  /**
   * To support paging in your app, return rows 20-25 of the full-text search result
   * from Example 3
   */
  @Test
  public void testCoreExample4() {
    ReadResponse resp = factual.read("places", new Query()
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
    ReadResponse resp = factual.read("places", new Query()
    .field("name").equal("Stand")
    .within(new Circle(34.06018, -118.41835, 5000)));

    assertOk(resp);
  }

  /**
   * {"$and":[{"name":{"$bw":"McDonald's"},"category":{"$bw":"Food & Beverage"}}]}
   */
  @Test
  public void testRowFilters_2beginsWith() {
    ReadResponse resp = factual.read("places", new Query()
    .field("name").beginsWith("McDonald's")
    .field("category").beginsWith("Food & Beverage"));

    assertOk(resp);
    assertStartsWith(resp, "name", "McDonald");
    assertStartsWith(resp, "category", "Food & Beverage");
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
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testComplicated() {
    Query q = new Query();
    q.field("region").in("MA","VT","NH");
    q.or(
        q.criteria("name").beginsWith("Coffee"),
        q.criteria("name").beginsWith("Star")
    );

    ReadResponse resp = factual.read("places", q);

    assertOk(resp);

    // assert region in {"MA","VT","NH"}
    for(String region : resp.mapStrings("region")){
      assertTrue(
          "MA".equals(region) ||
          "VT".equals(region) ||
          "NH".equals(region)
      );
    }

    // assert name starts with (coffee || star)
    for(String name : resp.mapStrings("name")){
      assertTrue(
          name.toLowerCase().startsWith("coffee") ||
          name.toLowerCase().startsWith("star")
      );
    }
  }

  @Test
  public void testSimpleTel() {
    ReadResponse resp = factual.read("places", new Query()
    .field("tel").beginsWith("(212)"));

    assertStartsWith(resp, "tel", "(212)");

    assertOk(resp);
  }

  /**
   * Search for places with names that have the terms "Fried Chicken"
   */
  @Test
  public void testFullTextSearch_on_a_field() {
    ReadResponse resp = factual.read("places", new Query()
    .field("name").fullTextSearch("Fried Chicken"));

    for(String name : resp.mapStrings("name")) {
      System.out.println(name);
      assertTrue(name.toLowerCase().contains("frie") || name.toLowerCase().contains("fry") || name.toLowerCase().contains("chicken"));
    }
  }

  @Test
  public void testCrosswalk_ex1() {
    CrosswalkResponse resp =
      factual.fetch("places", new CrosswalkQuery()
      .factualId("97598010-433f-4946-8fd5-4a6dd1639d77"));
    List<Crosswalk> crosswalks = resp.getCrosswalks();

    assertOk(resp);
    assertFalse(crosswalks.isEmpty());
    assertFactualId(crosswalks, "97598010-433f-4946-8fd5-4a6dd1639d77");
  }

  @Test
  public void testCrosswalk_ex2() {
    CrosswalkResponse resp =
      factual.fetch("places", new CrosswalkQuery()
      .factualId("97598010-433f-4946-8fd5-4a6dd1639d77")
      .only("loopt"));
    List<Crosswalk> crosswalks = resp.getCrosswalks();

    assertOk(resp);
    assertEquals(1, crosswalks.size());
    assertFactualId(crosswalks, "97598010-433f-4946-8fd5-4a6dd1639d77");
    assertNamespace(crosswalks, "loopt");
  }

  @Test
  public void testCrosswalk_ex3() {
    CrosswalkResponse resp =
      factual.fetch("places", new CrosswalkQuery()
      .namespace("foursquare")
      .namespaceId("4ae4df6df964a520019f21e3"));
    List<Crosswalk> crosswalks = resp.getCrosswalks();

    assertOk(resp);
    assertFalse(crosswalks.isEmpty());
    // The Stand
    assertFactualId(crosswalks, "97598010-433f-4946-8fd5-4a6dd1639d77");
  }

  @Test
  public void testCrosswalk_ex4() {
    CrosswalkResponse resp =
      factual.fetch("places", new CrosswalkQuery()
      .namespace("foursquare")
      .namespaceId("4ae4df6df964a520019f21e3")
      .only("yelp"));
    List<Crosswalk> crosswalks = resp.getCrosswalks();

    assertOk(resp);
    assertFalse(crosswalks.isEmpty());
    assertNamespace(crosswalks, "yelp");
  }

  @Test
  public void testCrosswalk_limit() {
    CrosswalkResponse resp =
      factual.fetch("places", new CrosswalkQuery()
      .factualId("97598010-433f-4946-8fd5-4a6dd1639d77")
      .limit(1));
    List<Crosswalk> crosswalks = resp.getCrosswalks();

    assertOk(resp);
    assertEquals(1, crosswalks.size());
  }

  @Test
  public void testApiException() {
    Factual badness = new Factual("badkey", "badsecret");
    try{
      badness.read("places", new Query().field("country").equal(true));
      fail("Expected to catch a FactualApiException");
    } catch (FactualApiException e) {
      assertEquals(401, e.getResponse().statusCode);
      assertEquals("Unauthorized", e.getResponse().statusMessage);
      assertTrue(e.getRequestUrl().startsWith("http://api.v3.factual.com/t/places"));
    }
  }

  private void assertFactualId(List<Crosswalk> crosswalks, String id) {
    for(Crosswalk cw : crosswalks) {
      assertEquals(id, cw.getFactualId());
    }
  }

  private void assertNamespace(List<Crosswalk> crosswalks, String ns) {
    for(Crosswalk cw : crosswalks) {
      assertEquals(ns, cw.getNamespace());
    }
  }

  private static final void assertOk(Response resp) {
    assertEquals("ok", resp.getStatus());
  }

  private void assertAll(ReadResponse resp, String field, String expected) {
    for(String out : resp.mapStrings(field)) {
      assertEquals(expected, out);
    }
  }

  private void assertStartsWith(ReadResponse resp, String field, String substr) {
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
