package com.factual.driver;

import java.util.HashMap;

import com.factual.data_science_toolkit.Coord;


/**
 * Represents a geographic sub query confining results to a circle.
 *
 * @author aaron
 */
public class Circle implements Convertible {
  private final double lat;
  private final double lon;
  private final int meters;


  /**
   * Constructs a geographic Circle representation.
   * 
   * @param lat the latitude of the center of this Circle.
   * @param lon the longitude of the center of this Circle.
   * @param meters the radius, in meters, of this Circle.
   */
  public Circle(double lat, double lon, int meters) {
    this.lat = lat;
    this.lon = lon;
    this.meters = meters;
  }

  /**
   * Constructs a geographic Circle representation.
   * 
   * @param coord the coordinates of the center of the Circle.
   * @param meters the radius, in meters, of this Circle.
   */
  public Circle(Coord coord, int meters) {
    this(coord.getLatitude(), coord.getLongitude(), meters);
  }

  /**
   * View this circle as a json string representation
   * 
   * @return a json string representation of this Circle
   */
  public String toJsonStr() {
    return JsonUtil.toJsonStr(toJsonObject());
  }

  /**
   * View this Circle as an object representation that can be serialized as json
   * 
   * @return an object representation of this circle that can be serialized as json
   */
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  @Override
  public Object toJsonObject() {
	  return new HashMap() {
		{
			put("$circle", new HashMap() {
				{
					put("$center", new String[]{String.valueOf(lat), String.valueOf(lon)});
					put("$meters", meters);
				}
			});
		}
	  };
  }
  
  @Override
  public String toString() {
	  return toJsonStr();
  }

}
