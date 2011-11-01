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
 * Represents the public Factual API.
 * 
 * @author aaron
 */
public class Factual {
  private static final String FACT_HOME = "http://api.v3.factual.com/t/";
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
   * Runs <tt>query</tt> against a Factual table.
   * 
   * @param table
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the query you wish to run against <tt>table</tt>.
   * @return the results of running <tt>query</tt> against Factual.
   */
  public Results fetch(String table, Query query) {
    return Results.fromJson(request(table, query));
  }

  private String request(String table, Query query) {
    GenericUrl url;
    url = new GenericUrl(FACT_HOME + table + "?" + query.toUrlPairs());
    System.out.println("GURL:" + url);
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

  public static void main(String[] args) {
    Factual factual = new Factual("YOUR_KEY", "YOUR_SECRET");

    Results results = factual.fetch("places", new Query()
    .q("starbucks")
    .limit(10)
    .includeCount(true)
    .offset(8)
    .circle(new Circle(34.06021, -118.41828, 5000)));

    System.out.println("status: " + results.getStatus());
    System.out.println("total underlying rows: " + results.getTotalRowCount());
    System.out.println();
    System.out.println("Starbucks Addresses:");
    for(String addr : results.mapStrings("address")) {
      System.out.println(addr);
    }
  }

}
