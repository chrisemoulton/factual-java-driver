package com.factual.driver;

/**
 * Represents a Geocode query against Factual
 * 
 * @author brandon
 *
 */
public class Geocode {
  /**
   * Holds all parameters for this Query.
   */
  protected final Parameters queryParams = new Parameters();

  /**
   * Represents a Geocode query against Factual
   * 
   * @param point The geographic point at which this geocode request is executed.
   */
  public Geocode(Point point) {
	queryParams.setParam(Constants.FILTER_GEO, point);
  }
  
  public String toUrlQuery() {
	return queryParams.toUrlQuery(null, true);
  }
}
