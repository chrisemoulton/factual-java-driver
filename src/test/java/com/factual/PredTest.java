package com.factual;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PredTest {

  /**
   * Tests a top-level AND with a nested OR and an $in:
   * 
   * <pre>
   * {$and:[
   *   {region:{$in:["MA","VT","NH"]}},
   *   {$or:[
   *     {first_name:{$eq:"Chun"}},
   *     {last_name:{$eq:"Kok"}}]}]}
   * </pre>
   */
  @Test
  public void testComplicated() {
    Pred in = new Pred("$in", "region", "MA", "VT", "NH");
    Pred fname = new Pred("$eq", "first_name", "Chun");
    Pred lname = new Pred("$eq", "last_name", "Kok");
    Pred or = new Pred("$or", fname, lname);

    Pred complicated = new Pred("$and", in, or);

    String predJsonStr = complicated.toJsonStr();

    assertEquals("{\"$and\":[{\"region\":{\"$in\":\"MA,VT,NH\"}},{\"$or\":[{\"first_name\":{\"$eq\":\"Chun\"}},{\"last_name\":{\"$eq\":\"Kok\"}}]}]}",
        predJsonStr);
  }

}
