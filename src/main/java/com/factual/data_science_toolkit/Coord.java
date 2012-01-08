package com.factual.data_science_toolkit;

import org.json.JSONException;
import org.json.JSONObject;

import com.factual.driver.FactualApiException;

/**
 * Represents a coordinate that came from a DataScienceToolkit query.
 * 
 * @author aaron
 *
 */
public class Coord {
  private final String region;
  private final String address;
  private final String locality;
  private final double latitude;
  private final double longitude;
  private final String country;
  private final double confidence;

  /**
   * Constructs a Coord from <tt></tt>, which is expected to be a marshalled
   * JSON object from a DataScienceToolkit query for a coord.
   * <p>
   * Example in:
   * <pre>
   * {"region":"CA",
   *  "street_name":"Chalon Rd",
   *  "country_code3":"USA",
   *  "locality":"Los Angeles",
   *  "street_number":"12001",
   *  "longitude":-118.480875,
   *  "latitude":34.08179,
   *  "confidence":0.61,
   *  "street_address":"12001 Chalon Rd",
   *  "country_code":"US",
   *  "fips_county":"06037",
   *  "country_name":"United States"}
   * </pre>
   */
  public Coord(JSONObject in) {
    try {
      region = in.getString("region");
      address = in.getString("street_address");
      locality = in.getString("locality");
      latitude = in.getDouble("latitude");
      longitude = in.getDouble("longitude");
      country = in.getString("country_code");
      confidence = in.getDouble("confidence");
    } catch (JSONException e) {
      throw new FactualApiException(e);
    }
  }

  public String getRegion() {
    return region;
  }

  public String getAddress() {
    return address;
  }

  public String getLocality() {
    return locality;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getCountry() {
    return country;
  }

  public double getConfidence() {
    return confidence;
  }

  @Override
  public String toString() {
    return "[Coord: address=" + address + ", lat=" + latitude + ", lon=" + longitude + "]";
  }

}
