package com.factual.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Represents the public Factual API. Supports running queries against Factual
 * and inspecting the response. Supports the same levels of authentication
 * supported by Factual's API.
 *
 * @author aaron
 */
public class Factual {
  private static final String DRIVER_HEADER_TAG = "factual-java-driver-v1.4.2";
  private static final String DEFAULT_HOST_HEADER = "api.v3.factual.com";
  private String factHome = "http://api.v3.factual.com/";
  private String host = DEFAULT_HOST_HEADER;
  private final String key;
  private final OAuthHmacSigner signer;
  private boolean debug = false;
  private StreamHandler debugHandler = null;

  private final Queue<FullQuery> fetchQueue = Lists.newLinkedList();

  /**
   * Constructor. Represents your authenticated access to Factual.
   *
   * @param key your oauth key.
   * @param secret your oauth secret.
   */
  public Factual(String key, String secret) {
    this(key, secret, false);
  }

  /**
   * Constructor. Represents your authenticated access to Factual.
   *
   * @param key your oauth key.
   * @param secret your oauth secret.
   * @param debug whether or not this is in debug mode
   */
  public Factual(String key, String secret, boolean debug) {
    this.key = key;
    this.signer = new OAuthHmacSigner();
    this.signer.clientSharedSecret = secret;
    debug(debug);
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
   * Change the host header value for a request to Factual's API.
   *
   * @param host
   *          the host header value for a request to Factual's API.
   */
  public void setRequestHost(String host) {
    this.host = host;
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
    return new ReadResponse(get(urlForFetch(tableName), query.toUrlParams()));
  }

  protected static String urlForCrosswalk(String tableName) {
    return tableName + "/crosswalk";
  }

  protected static String urlForResolve(String tableName) {
    return tableName + "/resolve";
  }

  protected static String urlForFetch(String tableName) {
    return "t/" + tableName;
  }

  protected static String urlForMonetize() {
    return "places/monetize";
  }

  protected static String urlForFacets(String tableName) {
    return "t/" + tableName + "/facets";
  }

  protected static String urlForGeocode() {
    return "places/geocode";
  }

  protected static String urlForGeopulse() {
    return "places/geopulse";
  }

  /**
   * Runs a <tt>geopulse</tt> query against Factual.
   * 
   * @param geopulse the geopulse query to run.
   * @return the response of running <tt>geopulse</tt> against Factual.
   */
  public ReadResponse geopulse(Geopulse geopulse) {
    return new ReadResponse(get(urlForGeopulse(), geopulse.toUrlParams()));
  }

  /**
   * Reverse geocodes by returning a response containing the address nearest to the given point.
   * 
   * @param point the point for which the nearest address is returned
   * @return the response of running a reverse geocode query for <tt>point</tt> against Factual.
   */
  public ReadResponse reverseGeocode(Point point) {
    return new ReadResponse(get(urlForGeocode(), new Geocode(point).toUrlParams()));
  }

  /**
   * Runs a <tt>facet</tt> read against the specified Factual table.
   *
   *
   * @param tableName
   * 		  the name of the table you wish to query for facets (e.g., "places")
   * @param facet
   * 		  the facet query to run against <tt>table</tt>
   * @return the response of running <tt>facet</tt> against Factual.
   */
  public FacetResponse fetch(String tableName, FacetQuery facet) {
    return new FacetResponse(get(urlForFacets(tableName), facet.toUrlParams()));
  }

  /**
   * Runs a <tt>submit</tt> input against the specified Factual table.
   * @param tableName
   * 		  the name of the table you wish to submit updates for (e.g., "places")
   * @param factualId
   * 		  the factual id on which the submit is run
   * @param submit
   * 		  the submit parameters to run against <tt>table</tt>
   * @param metadata
   * 		  the metadata to send with information on this request
   * @return the response of running <tt>submit</tt> against Factual.
   */
  public SubmitResponse submit(String tableName, String factualId, Submit submit, Metadata metadata) {
    return submitCustom("t/"+tableName+"/"+factualId+"/submit", submit, metadata);
  }

  /**
   * Runs a <tt>submit</tt> to add a row against the specified Factual table.
   * @param tableName
   * 		  the name of the table you wish to submit the add for (e.g., "places")
   * @param submit
   * 		  the submit parameters to run against <tt>table</tt>
   * @param metadata
   * 		  the metadata to send with information on this request
   * @return the response of running <tt>submit</tt> against Factual.
   */
  public SubmitResponse submit(String tableName, Submit submit, Metadata metadata) {
    return submitCustom("t/"+tableName+"/submit", submit, metadata);
  }

  /**
   * Flags a row as a duplicate in the specified Factual table.
   *
   * @param tableName
   * 		  the name of the table you wish to flag a duplicate for (e.g., "places")
   * @param factualId
   * 		  the factual id that is the duplicate
   * @param metadata
   * 		  the metadata to send with information on this request
   *
   * @return the response from flagging a duplicate row.
   */
  public FlagResponse flagDuplicate(String tableName, String factualId, Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "duplicate", metadata);
  }

  protected static String urlForFlag(String tableName, String factualId) {
    return "t/"+tableName+"/"+factualId+"/flag";
  }

  /**
   * Flags a row as inaccurate in the specified Factual table.
   *
   * @param tableName
   * 		  the name of the table you wish to flag an inaccurate row for (e.g., "places")
   * @param factualId
   * 		  the factual id that is inaccurate
   * @param metadata
   * 		  the metadata to send with information on this request
   *
   * @return the response from flagging an inaccurate row.
   */
  public FlagResponse flagInaccurate(String tableName, String factualId, Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "inaccurate", metadata);
  }

  /**
   * Flags a row as inappropriate in the specified Factual table.
   *
   * @param tableName
   * 		  the name of the table you wish to flag an inappropriate row for (e.g., "places")
   * @param factualId
   * 		  the factual id that is inappropriate
   * @param metadata
   * 		  the metadata to send with information on this request
   *
   * @return the response from flagging an inappropriate row.
   */
  public FlagResponse flagInappropriate(String tableName, String factualId, Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "inappropriate", metadata);
  }

  /**
   * Flags a row as non-existent in the specified Factual table.
   *
   * @param tableName
   * 		  the name of the table you wish to flag a non-existent row for (e.g., "places")
   * @param factualId
   * 		  the factual id that is non-existent
   * @param metadata
   * 		  the metadata to send with information on this request
   *
   * @return the response from flagging a non-existent row.
   */
  public FlagResponse flagNonExistent(String tableName, String factualId, Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "nonexistent", metadata);
  }

  /**
   * Flags a row as spam in the specified Factual table.
   *
   * @param tableName
   * 		  the name of the table you wish to flag a row as spam (e.g., "places")
   * @param factualId
   * 		  the factual id that is spam
   * @param metadata
   * 		  the metadata to send with information on this request
   *
   * @return the response from flagging a row as spam.
   */
  public FlagResponse flagSpam(String tableName, String factualId, Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "spam", metadata);
  }

  /**
   * Flags a row as problematic in the specified Factual table.
   *
   * @param tableName
   * 		  the name of the table for which you wish to flag as problematic (e.g., "places")
   * @param factualId
   * 		  the factual id that has a problem other than duplicate, inaccurate, inappropriate, non-existent, or spam.
   * @param metadata
   * 		  the metadata to send with information on this request
   *
   * @return the response from flagging a row as problematic.
   */
  public FlagResponse flagOther(String tableName, String factualId, Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "other", metadata);
  }

  /**
   * Runs a "GET" request against the path specified using the parameters specified and your Oauth token.
   * @param path the path to run the request against
   * @param params the parameters to send with the request
   * @return the response of running <tt>query</tt> against Factual.
   */
  public String get(String path, Map<String, Object> params) {
    return request(new FullQuery(path, params, FullQuery.ResponseType.RAW_READ_RESPONSE));
  }

  private String post(String path, Map<String, Object> params) {
    return requestPost(new FullQuery(path, params, FullQuery.ResponseType.RAW_READ_RESPONSE));
  }

  private SubmitResponse submitCustom(String root, Submit submit, Metadata metadata) {
    Map<String, Object> params = Maps.newHashMap();
    // TODO: Switch parameters to POST content when supported.
    params.putAll(metadata.toUrlParams());
    params.putAll(submit.toUrlParams());
    String jsonResponse = post(root, params);
    return new SubmitResponse(jsonResponse);
  }

  private FlagResponse flagCustom(String root, String flagType, Metadata metadata) {
    Map<String, Object> params = Maps.newHashMap();
    // TODO: Switch parameters to POST content when supported.
    params.putAll(metadata.toUrlParams());
    params.put("problem", flagType);
    String jsonResponse = post(root, params);
    return new FlagResponse(jsonResponse);
  }

  /**
   * Queue a raw read request for inclusion in the next multi request.
   * @param path the path to run the request against
   * @param params the parameters to send with the request
   */
  public void queueFetch(String path, Map<String, Object> params) {
    fetchQueue.add(new FullQuery(path, params, FullQuery.ResponseType.RAW_READ_RESPONSE));
  }

  /**
   * Queue a read request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the read query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, Query query) {
    fetchQueue.add(new FullQuery(urlForFetch(table), query.toUrlParams(), FullQuery.ResponseType.READ_RESPONSE));
  }

  /**
   * Queue a crosswalk request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to use crosswalk against (e.g., "places")
   * @param query
   *          the crosswalk query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, CrosswalkQuery query) {
    fetchQueue.add(new FullQuery(urlForCrosswalk(table), query.toUrlParams(), FullQuery.ResponseType.CROSSWALK_RESPONSE));
  }

  /**
   * Queue a resolve request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to use resolve against (e.g., "places")
   * @param query
   *          the resolve query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, ResolveQuery query) {
    fetchQueue.add(new FullQuery(urlForResolve(table), query.toUrlParams(), FullQuery.ResponseType.READ_RESPONSE));
  }

  /**
   * Queue a facet request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to use a facet request against (e.g., "places")
   * @param query
   *          the facet query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, FacetQuery query) {
    fetchQueue.add(new FullQuery(urlForFacets(table), query.toUrlParams(), FullQuery.ResponseType.FACET_RESPONSE));
  }

  public void queueFetch(Geocode query) {
    fetchQueue.add(new FullQuery(urlForGeocode(), query.toUrlParams(), FullQuery.ResponseType.READ_RESPONSE));
  }

  public void queueFetch(Geopulse query) {
    fetchQueue.add(new FullQuery(urlForGeopulse(), query.toUrlParams(), FullQuery.ResponseType.READ_RESPONSE));
  }

  /**
   * Use this to send all queued reads as a multi request
   * @return response for a multi request
   */
  public MultiResponse sendRequests() {
    Map<String, String> multi = Maps.newHashMap();
    int i = 0;
    Map<String, FullQuery> requestMapping = Maps.newLinkedHashMap();
    while (!fetchQueue.isEmpty()) {
      FullQuery fullQuery = fetchQueue.poll();
      String url = "/"+fullQuery.toUrlString();
      if (url != null) {
        String multiKey = "q"+Integer.toString(i);
        multi.put(multiKey, url);
        requestMapping.put(multiKey, fullQuery);
        i++;
      }
    }
    String json = JsonUtil.toJsonStr(multi);
    Map<String, Object> params = Maps.newHashMap();
    params.put("queries", json);
    String jsonResponse = get("multi", params);
    MultiResponse resp = new MultiResponse(requestMapping);
    resp.setJson(jsonResponse);
    return resp;
  }

  /**
   * Runs a monetize <tt>query</tt> against the specified Factual table.
   *
   * @param query
   *          the query to run against monetize.
   * @return the response of running <tt>query</tt> against Factual.
   */
  public ReadResponse monetize(Query query) {
    return new ReadResponse(get(urlForMonetize(), query.toUrlParams()));
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
    return new CrosswalkResponse(request(new FullQuery(urlForCrosswalk(tableName), query.toUrlParams(), FullQuery.ResponseType.CROSSWALK_RESPONSE)));
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
    return new ReadResponse(request(new FullQuery(urlForResolve(tableName), query.toUrlParams(), FullQuery.ResponseType.READ_RESPONSE)));
  }

  public SchemaResponse schema(String tableName) {
    Map<String, Object> params = Maps.newHashMap();
    return new SchemaResponse(get(urlForSchema(tableName), params));
  }

  private String urlForSchema(String tableName) {
    return "t/" + tableName + "/schema";
  }

  private String request(FullQuery query) {
    return request(query, true);
  }

  private String request(FullQuery query, boolean useOAuth) {
    return request(query, "GET", useOAuth);
  }

  private String requestPost(FullQuery query) {
    return requestPost(query, true);
  }

  private String requestPost(FullQuery query, boolean useOAuth) {
    return request(query, "POST", useOAuth);
  }

  private String request(FullQuery fullQuery, String requestMethod, boolean useOAuth) {
    Map<String, Object> requestParams = fullQuery.getRequestParams();
    Map<String, String> postData = fullQuery.getPostData();
    String urlStr = factHome + fullQuery.toUrlString();
    GenericUrl url = new GenericUrl(urlStr);

    if (debug) {
      System.out.println("=== "+urlStr+" ===");
      System.out.println("Parameters:");
      for (String key : requestParams.keySet()) {
        System.out.println(key+": "+requestParams.get(key));
      }
      Logger logger = Logger.getLogger(HttpTransport.class.getName());
      logger.removeHandler(debugHandler);
      logger.setLevel(Level.ALL);
      logger.addHandler(debugHandler);
    }

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
      HttpRequestFactory f = null;
      if (useOAuth) {
        f = transport.createRequestFactory(params);
      } else {
        f = transport.createRequestFactory();
      }
      HttpRequest request = null;
      if ("POST".equals(requestMethod))
        if (postData == null)
          request = f.buildPostRequest(url, null);
        else
          request = f.buildPostRequest(url, new UrlEncodedContent(postData));
      else
        request = f.buildGetRequest(url);
      HttpHeaders headers = new HttpHeaders();
      headers.set("X-Factual-Lib", DRIVER_HEADER_TAG);
      headers.set("Host", host);
      request.setHeaders(headers);

      // get the response
      br = new BufferedReader(new InputStreamReader(request.execute().getContent()));
      String line = null;
      StringBuffer sb = new StringBuffer();
      while ((line = br.readLine())!= null) {
        sb.append(line);
      }
      return sb.toString();

    } catch (HttpResponseException e) {
      throw new FactualApiException(e).requestUrl(urlStr).requestMethod(requestMethod).response(e.getResponse());
    } catch (IOException e) {
      throw new FactualApiException(e).requestUrl(urlStr).requestMethod(requestMethod);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    } finally {
      Closeables.closeQuietly(br);
    }
  }

  /**
   * Set the driver in or out of debug mode.
   * @param debug whether or not this is in debug mode
   */
  public void debug(boolean debug) {
    this.debug = debug;
    if (debug && debugHandler == null) {
      debugHandler = new StreamHandler(System.out, new SimpleFormatter());
      debugHandler.setLevel(Level.ALL);
    }
  }

  /**
   * Represents a request against Factual given a path and parameters
   * 
   * @author brandon
   * 
   */
  protected static class FullQuery {

    public static enum ResponseType { READ_RESPONSE, CROSSWALK_RESPONSE, FACET_RESPONSE, FLAG_RESPONSE, SCHEMA_RESPONSE, SUBMIT_RESPONSE, RAW_READ_RESPONSE};

    private final Map<String, Object> params;
    private final String path;
    private final ResponseType responseType;

    public FullQuery(String path, Map<String, Object> params, ResponseType responseType) {
      this.path = path;
      this.params = params;
      this.responseType = responseType;
    }

    public Map<String, Object> getRequestParams() {
      return params;
    }

    public String toUrlString() {
      return UrlUtil.toUrl(path, getRequestParams());
    }

    public Map<String, String> getPostData() {
      return Maps.newHashMap();
    }

    public Response getResponse(String json) {
      switch (responseType) {
      case READ_RESPONSE:
        return new ReadResponse(json);
      case CROSSWALK_RESPONSE:
        return new CrosswalkResponse(json);
      case FACET_RESPONSE:
        return new FacetResponse(json);
      case FLAG_RESPONSE:
        return new FlagResponse(json);
      case SUBMIT_RESPONSE:
        return new SubmitResponse(json);
      case SCHEMA_RESPONSE:
        return new SchemaResponse(json);
      case RAW_READ_RESPONSE:
        return new RawReadResponse(json);
      }
      return null;
    }
  }
}
