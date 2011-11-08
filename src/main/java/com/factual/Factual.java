package com.factual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * Represents the public Factual API. Supports running queries against Factual
 * and inspecting the response. Supports the same levels of authentication
 * supported by Factual's API.
 * 
 * @author aaron
 */
public class Factual {
  private String factHome = "http://api.v3.factual.com/t/";
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
   * Change the base URL at which to contact Factual's API.
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
   * Runs <tt>query</tt> against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the query you wish to run against <tt>table</tt>.
   * @return the response of running <tt>query</tt> against Factual.
   */
  public Response fetch(String tableName, Query query) {
    return Response.fromJson(request(tableName, query));
  }

  private String request(String tableName, Query query) {
    GenericUrl url;
    url = new GenericUrl(factHome + tableName + "?" + query.toUrlPairs());
    String requestMethod = "GET";

    // Configure OAuth request params
    OAuthParameters params = new OAuthParameters();
    params.consumerKey = key;
    params.computeNonce();
    params.computeTimestamp();
    params.signer = signer;

    try {
      // generate the signature
      params.computeSignature(requestMethod, url);

      // make the request
      HttpTransport transport = new NetHttpTransport();
      HttpRequestFactory f = transport.createRequestFactory(params);
      HttpRequest request = f.buildGetRequest(url);

      // get the response
      HttpResponse response = request.execute();
      BufferedReader br = new BufferedReader(new InputStreamReader(response.getContent()));

      return br.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

}
