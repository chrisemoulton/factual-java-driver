package com.factual.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
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
  private static final String DRIVER_HEADER_TAG = "factual-java-driver-v1.0.2";
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
  public ReadResponse fetch(String tableName, Query query) {
    return new ReadResponse(request(urlForFetch(tableName, query)));
  }

  /**
   * Convenience method to return Crosswalks for the specific query.
   */
  public List<Crosswalk> crosswalks(String table, CrosswalkQuery query) {
    return fetch(table, query).getCrosswalks();
  }

  /**
   * Query's Factual for the Crosswalk data matching the specified
   * <tt>query</tt>.
   * 
   * @param tableName
   *          the name of the table to crosswalk.
   * @param query
   *          the Crosswalk query.
   * @return Factual's response to the Crosswalk query.
   */
  public CrosswalkResponse fetch(String tableName, CrosswalkQuery query) {
    return new CrosswalkResponse(request(urlForCrosswalk(tableName, query)));
  }

  /**
   * Asks Factual to resolve the Places entity for the attributes specified by
   * <tt>query</tt>.
   * <p>
   * Returns the read response from a Factual Resolve request, which includes
   * all records that are potential matches.
   * 
   * @param query
   *          the Resolve query to run against Factual's Places table.
   * @return the response from Factual for the Resolve request.
   */
  public ReadResponse resolves(ResolveQuery query) {
    return fetch("places", query);
  }

  /**
   * Asks Factual to resolve the Places entity for the attributes specified by
   * <tt>query</tt>. Returns a record representing the resolved entity if
   * Factual successfully identified the entity with full confidence, or null if
   * the entity was not resolved.
   * 
   * @param query
   *          a Resolve query with partial attributes for an entity.
   * @return a record representing the resolved entity if Factual successfully
   *         identified the entity with full confidence, or null if the entity
   *         was not resolved.
   */
  public Map<String, Object> resolve(ResolveQuery query) {
    return resolves(query).first();
  }

  /**
   * Asks Factual to resolve the entity for the attributes specified by
   * <tt>query</tt>, within the table called <tt>tableName</tt>.
   * <p>
   * Returns the read response from a Factual Resolve request, which includes
   * all records that are potential matches.
   * <p>
   * Each result record will include a confidence score (<tt>"similarity"</tt>),
   * and a flag indicating whether Factual decided the entity is the correct
   * resolved match with a high degree of accuracy (<tt>"resolved"</tt>).
   * <p>
   * There will be 0 or 1 entities returned with "resolved"=true. If there was a
   * full match, it is guaranteed to be the first record in the response.
   * 
   * @param tableName
   *          the name of the table to resolve within.
   * @param query
   *          a Resolve query with partial attributes for an entity.
   * @return the response from Factual for the Resolve request.
   */
  public ReadResponse fetch(String tableName, ResolveQuery query) {
    return new ReadResponse(request(urlForResolve(tableName, query)));
  }

  public SchemaResponse schema(String tableName) {
    return new SchemaResponse(request(urlForSchema(tableName)));
  }

  private String urlForSchema(String tableName) {
    return factHome + "t/" + tableName + "/schema";
  }

  private String urlForCrosswalk(String tableName, CrosswalkQuery query) {
    return factHome + tableName + "/crosswalk?" + query.toUrlQuery();
  }

  private String urlForResolve(String tableName, ResolveQuery query) {
    return factHome + tableName + "/resolve?" + query.toUrlQuery();
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
      headers.set("X-Factual-Lib", DRIVER_HEADER_TAG);
      request.headers = headers;

      // get the response
      br = new BufferedReader(new InputStreamReader(request.execute().getContent()));
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
