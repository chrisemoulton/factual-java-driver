package com.factual.driver;

import com.factual.data_science_toolkit.Coord;


/**
 * Represents a geographic sub query confining results to a circle.
 *
 * @author aaron
 */
public class Circle {
  private final double lat;
  private final double lon;
  private final int meters;


  /**
   * Constructs a geographic Circle representation.
   * 
   * @param lat the latitude of the center of this Circle.
   * @param lon the longitude of the center of this Circle.
   * @param metersRadius the radius, in meters, of this Circle.
   */
  public Circle(double lat, double lon, int meters) {
    this.lat = lat;
    this.lon = lon;
    this.meters = meters;
  }

  public Circle(Coord coord, int meters) {
    this(coord.getLatitude(), coord.getLongitude(), meters);
  }

  public String toJsonStr() {
    return "{\"$circle\":{\"$center\":[" + lat + "," + lon + "],\"$meters\":" + meters + "}}";
  }

}
