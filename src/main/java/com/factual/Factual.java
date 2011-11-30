package com.factual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.io.Closeables;

/**
 * Represents the public Factual API. Supports running queries against Factual
 * and inspecting the response. Supports the same levels of authentication
 * supported by Factual's API.
 * 
 * @author aaron
 */
public class Factual {
  private static final String DRIVER_HEADER_TAG = "factual-java-driver-v1.0";
  private String factHome = "http://api.v3.factual.com/";
  private final String key;
  private final OAuthHmacSigner signer;


  /**
   * Constructor. Represents your authenticated access to Factual.
   * 
   * @param key your oauth key.
   * @param secret your oauth secret.
   */
  public Factual(String key, String secret) {
    this.key = key;
    this.signer = new OAuthHmacSigner();
    this.signer.clientSharedSecret = secret;
  }

  /**
   * Change the base URL at which to contact Factual's API. This
   * may be useful if you want to talk to a test or staging
   * server.
   * <p>
   * Example value: <tt>http://staging.api.v3.factual.com/t/</tt>
   * 
   * @param urlBase
   *          the base URL at which to contact Factual's API.
   */
  public void setFactHome(String urlBase) {
    this.factHome = urlBase;
  }

  /**
   * Runs a read <tt>query</tt> against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the read query to run against <tt>table</tt>.
   * @return the response of running <tt>query</tt> against Factual.
   */
  public ReadResponse read(String tableName, Query query) {
    return new ReadResponse(request(urlForFetch(tableName, query)));
  }

  public CrosswalkResponse fetch(String tableName, CrosswalkQuery query) {
    return new CrosswalkResponse(request(urlForCrosswalk(tableName, query)));
  }

  private String urlForCrosswalk(String tableName, CrosswalkQuery query) {
    return factHome + tableName + "/crosswalk?" + query.toUrlQuery();
  }

  private String urlForFetch(String tableName, Query query) {
    return factHome + "t/" + tableName + "?" + query.toUrlQuery();
  }

  private String request(String urlStr) {
    GenericUrl url = new GenericUrl(urlStr);
    String requestMethod = "GET";

    // Configure OAuth request params
    OAuthParameters params = new OAuthParameters();
    params.consumerKey = key;
    params.computeNonce();
    params.computeTimestamp();
    params.signer = signer;

    BufferedReader br = null;
    try {
      // generate the signature
      params.computeSignature(requestMethod, url);

      // make the request
      HttpTransport transport = new NetHttpTransport();
      HttpRequestFactory f = transport.createRequestFactory(params);
      HttpRequest request = f.buildGetRequest(url);

      HttpHeaders headers = new HttpHeaders();
      headers.set("X-FACTUAL-LIB", DRIVER_HEADER_TAG);
      request.headers = headers;

      // get the response
      HttpResponse response = request.execute();
      br = new BufferedReader(new InputStreamReader(response.getContent()));

      return br.readLine();
    } catch (HttpResponseException e) {
      throw new FactualApiException(e).requestUrl(urlStr).requestMethod(requestMethod).response(e.response);
    } catch (IOException e) {
      throw new FactualApiException(e).requestUrl(urlStr).requestMethod(requestMethod);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    } finally {
      Closeables.closeQuietly(br);
    }
  }

}
