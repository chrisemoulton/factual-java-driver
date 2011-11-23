package com.factual;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;


/**
 * Unit tests for Query.
 * 
 * @author aaron
 */
public class QueryTest {

  @Test
  public void testFilter_shorthand_eq_1() throws UnsupportedEncodingException {
    Query query = new Query()
    .filter("first_name", "Bradley");

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"first_name\":{\"$eq\":\"Bradley\"}}",
        decoded);
  }

  @Test
  public void testFilter_shorthand_eq_3() throws UnsupportedEncodingException {
    Query query = new Query()
    .filter("first_name", "Bradley")
    .filter("region", "CA")
    .filter("locality", "Los Angeles");

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$and\":[{\"first_name\":{\"$eq\":\"Bradley\"}},{\"region\":{\"$eq\":\"CA\"}},{\"locality\":{\"$eq\":\"Los Angeles\"}}]}",
        decoded);
  }

  /**
   * Tests setting a complicated row filter via query.filter(Pred)
   * <p>
   * <pre>
   * {$and:[
   *   {region:{$in:["MA","VT","NH"]}},
   *   {$or:[
   *     {first_name:{$eq:"Chun"}},
   *     {last_name:{$eq:"Kok"}}]}]}
   * </pre>
   */
  @Test
  public void testFilter_Query_complicated() throws UnsupportedEncodingException {
    Pred in = new Pred("$in", "region", "MA", "VT", "NH");
    Pred fname = new Pred("$eq", "first_name", "Chun");
    Pred lname = new Pred("$eq", "last_name", "Kok");
    Pred or = new Pred("$or", fname, lname);

    Pred complicated = new Pred("$and", in, or);

    Query query = new Query()
    .filter(complicated);

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$and\":[{\"region\":{\"$in\":\"MA,VT,NH\"}},{\"$or\":[{\"first_name\":{\"$eq\":\"Chun\"}},{\"last_name\":{\"$eq\":\"Kok\"}}]}]}",
        decoded);
  }
}
