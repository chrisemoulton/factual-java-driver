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
    .field("first_name").equal("Bradley");

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"first_name\":{\"$eq\":\"Bradley\"}}",
        decoded);
  }

  @Test
  public void testFilter_shorthand_eq_3() throws UnsupportedEncodingException {
    Query query = new Query()
    .field("first_name").equal("Bradley")
    .field("region").equal("CA")
    .field("locality").equal("Los Angeles");

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$and\":[{\"first_name\":{\"$eq\":\"Bradley\"}},{\"region\":{\"$eq\":\"CA\"}},{\"locality\":{\"$eq\":\"Los Angeles\"}}]}",
        decoded);
  }

  /**
   * Tests query.or syntax
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
  public void testOr() throws UnsupportedEncodingException {
    Query q = new Query()
    .field("region").in("MA", "VT", "NH");

    q.or(
        q.criteria("first_name").equal("Chun"),
        q.criteria("last_name").equal("Kok")
    );

    String queryStr = q.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$and\":[{\"region\":{\"$in\":\"[MA,VT,NH]\"}},{\"$or\":[{\"first_name\":{\"$eq\":\"Chun\"}},{\"last_name\":{\"$eq\":\"Kok\"}}]}]}",
        decoded);
  }

  //  @Test
  //  public void test() throws UnsupportedEncodingException {
  //    Query q = new Query();
  //
  //    q.and(
  //        q.or(
  //            q.criteria("first_name").equal("Chun"),
  //            q.criteria("last_name").equal("Kok")
  //        ),
  //        q.or(
  //            q.criteria("region").equal("NM"),
  //            q.criteria("region").equal("CA")
  //        )
  //    );
  //
  //    String queryStr = q.toUrlQuery();
  //    String decoded = URLDecoder.decode(queryStr, "UTF-8");
  //
  //    System.out.println(decoded);
  //  }
}
