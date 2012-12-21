package com.factual.driver;

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
    .field("first_name").isEqual("Bradley");

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"first_name\":{\"$eq\":\"Bradley\"}}",
        decoded);
  }

  @Test
  public void testFilter_shorthand_eq_3() throws UnsupportedEncodingException {
    Query query = new Query()
    .field("first_name").isEqual("Bradley")
    .field("region").isEqual("CA")
    .field("locality").isEqual("Los Angeles");

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
        q.field("first_name").isEqual("Chun"),
        q.field("last_name").isEqual("Kok")
    );

    String queryStr = q.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$and\":[{\"region\":{\"$in\":[\"MA\",\"VT\",\"NH\"]}},{\"$or\":[{\"last_name\":{\"$eq\":\"Kok\"}},{\"first_name\":{\"$eq\":\"Chun\"}}]}]}",
        decoded);
  }

  @Test
  public void testNestedFilterLogic() throws UnsupportedEncodingException {
    Query q = new Query();
    q.or(
        q.or(
            q.field("first_name").isEqual("Chun"),
            q.field("last_name").isEqual("Kok")
        ),
        q.and(
            q.field("score").isEqual("38"),
            q.field("city").isEqual("Los Angeles")
        )
    );

    /**
    filters={"$or":[
                      {"$and":[
                                {"score":{"$eq":"38"}},
                                {"city":{"$eq":"Los Angeles"}}]},
                      {"$or":[
                                {"first_name":{"$eq":"Chun"}},
                                {"last_name":{"$eq":"Kok"}}]}]}
     */
    String queryStr = q.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$or\":[{\"$and\":[{\"city\":{\"$eq\":\"Los Angeles\"}},{\"score\":{\"$eq\":\"38\"}}]},{\"$or\":[{\"last_name\":{\"$eq\":\"Kok\"}},{\"first_name\":{\"$eq\":\"Chun\"}}]}]}",
        decoded);
  }

  @Test
  public void testSorts() throws UnsupportedEncodingException {
    Query query = new Query()
    .sortDesc("$distance")
    .sortAsc("name");

    String queryStr = query.toUrlQuery();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("sort=$distance:desc,name:asc", decoded);
  }

}
