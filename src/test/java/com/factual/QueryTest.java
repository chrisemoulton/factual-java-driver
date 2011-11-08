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

    String queryStr = query.toUrlPairs();
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

    String queryStr = query.toUrlPairs();
    String decoded = URLDecoder.decode(queryStr, "UTF-8");

    assertEquals("filters={\"$and\":[{\"first_name\":{\"$eq\":\"Bradley\"}},{\"region\":{\"$eq\":\"CA\"}},{\"locality\":{\"$eq\":\"Los Angeles\"}}]}",
        decoded);
  }

}
