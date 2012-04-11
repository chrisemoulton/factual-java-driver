package com.factual.data_science_toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.factual.driver.FactualApiException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.io.Closeables;

/**
 * Provides a wrapper to Data Science Toolkit's public API.
 * 
 * @author aaron
 */
public class DataScienceToolkit {
  private static final HttpRequestFactory REQ_FACTORY = new NetHttpTransport().createRequestFactory();
  private static final String HOME = "http://www.datasciencetoolkit.org/";
  private static final String STREET_2_COORDINATES = HOME+"street2coordinates/";


  /**
   * Queries the Data Science Toolkit for the coordinates of a location
   * specified with <tt>text</tt>.
   * <p>
   * <tt>text</tt> might be something like
   * <tt>"12001 Chalon los angeles ca"</tt>.
   * 
   * @param text
   *          free form text including information about a desired location,
   *          e.g. city name, zip code, etc.
   * @return the matching coordinate data from the Data Science Toolkit, or null
   *         if no match.
   */
  public Coord streetToCoord(String text) {
    GenericUrl url = new GenericUrl(STREET_2_COORDINATES);
    url.appendRawPath(text);

    BufferedReader br = null;
    try {
      HttpRequest request = REQ_FACTORY.buildGetRequest(url);
      br = new BufferedReader(new InputStreamReader(request.execute().getContent()));

      JSONObject rootJsonObj = new JSONObject(br.readLine());
      String key = rootJsonObj.keys().next().toString();
      Object hit = rootJsonObj.get(key);

      if(hit instanceof JSONObject) {
        return new Coord((JSONObject) hit);
      } else {
        return null;
      }
    } catch (IOException e) {
      throw new FactualApiException(e);
    } catch (JSONException e) {
      throw new FactualApiException(e);
    } finally {
      Closeables.closeQuietly(br);
    }
  }

}
