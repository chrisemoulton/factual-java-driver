package com.factual.data_science_toolkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class DataScienceToolkitTest {
  DataScienceToolkit kit = new DataScienceToolkit();

  @Test
  public void testStreetToCoord() {
    Coord coord = kit.streetToCoord("12001 Chalon los angeles ca");

    assertEquals("12001 Chalon Rd", coord.getAddress());
    assertEquals("Los Angeles", coord.getLocality());
    assertEquals("CA", coord.getRegion());
    assertEquals("US", coord.getCountry());
    assertTrue(coord.getLatitude() != 0);
    assertTrue(coord.getLongitude() != 0);
    assertTrue(coord.getConfidence() != 0);
  }

}
