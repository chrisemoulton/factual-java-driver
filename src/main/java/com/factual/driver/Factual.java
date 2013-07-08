package com.factual.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
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
  private static final String DRIVER_HEADER_TAG = "factual-java-driver-v1.7.8";
  private static final String DEFAULT_HOST_HEADER = "api.v3.factual.com";
  private String factHome = "http://api.v3.factual.com/";
  private String host = DEFAULT_HOST_HEADER;
  private final String key;
  private final OAuthHmacSigner signer;
  private boolean debug = false;
  private int readTimeout = -1;
  private int connectionTimeout = -1;
  private StreamHandler debugHandler = null;
  private Logger logger = null;

  /**
   * Constructor. Represents your authenticated access to Factual.
   * 
   * @param key
   *          your oauth key.
   * @param secret
   *          your oauth secret.
   */
  public Factual(String key, String secret) {
    this(key, secret, false);
  }

  /**
   * Constructor. Represents your authenticated access to Factual.
   * 
   * @param key
   *          your oauth key.
   * @param secret
   *          your oauth secret.
   * @param debug
   *          whether or not this is in debug mode
   */
  public Factual(String key, String secret, boolean debug) {
    this.key = key;
    this.signer = new OAuthHmacSigner();
    this.signer.clientSharedSecret = secret;
    debug(debug);
  }

  /**
   * Change the base URL at which to contact Factual's API. This may be useful
   * if you want to talk to a test or staging server.
   * <p>
   * Example value: <tt>http://staging.api.v3.factual.com/t/</tt>
   * 
   * @param urlBase
   *          the base URL at which to contact Factual's API.
   */
  public synchronized void setFactHome(String urlBase) {
    this.factHome = urlBase;
  }

  /**
   * Change the host header value for a request to Factual's API.
   * 
   * @param host
   *          the host header value for a request to Factual's API.
   */
  public synchronized void setRequestHost(String host) {
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
    return new ReadResponse(getInternal(urlForFetch(tableName), query.toUrlParams()));
  }

  /**
   * Runs a read <tt>query</tt> against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to query (e.g., "places")
   * @param factualId
   *          the factual id
   * @param query
   *          the read query to run against <tt>table</tt>.
   * @return the response of running <tt>query</tt> against Factual.
   */
  public RowResponse fetchRow(String tableName, String factualId, RowQuery query) {
    return new RowResponse(getInternal(urlForFetchRow(tableName, factualId), query.toUrlParams()));
  }

  /**
   * Runs a read <tt>query</tt> against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to query (e.g., "places")
   * @param factualId
   *          the factual id
   * @return the response of running <tt>query</tt> against Factual.
   */
  public RowResponse fetchRow(String tableName, String factualId) {
    return fetchRow(tableName, factualId, new RowQuery());
  }

  protected static String urlForResolve(String tableName) {
    return "t/" + tableName + "/resolve";
  }

  protected static String urlForMatch(String tableName) {
    return "t/" + tableName + "/match";
  }

  protected static String urlForFetch(String tableName) {
    return "t/" + tableName;
  }

  protected static String urlForFetchRow(String tableName, String factualId) {
    return "t/" + tableName + "/" + factualId;
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
    return "geopulse/context";
  }

  /**
   * Runs a <tt>geopulse</tt> query against Factual.
   * 
   * @param geopulse
   *          the geopulse query to run.
   * @return the response of running <tt>geopulse</tt> against Factual.
   */
  public ReadResponse geopulse(Geopulse geopulse) {
    return new ReadResponse(getInternal(urlForGeopulse(), geopulse.toUrlParams()));
  }

  /**
   * Reverse geocodes by returning a response containing the address nearest to
   * the given point.
   * 
   * @param point
   *          the point for which the nearest address is returned
   * @return the response of running a reverse geocode query for <tt>point</tt>
   *         against Factual.
   */
  public ReadResponse reverseGeocode(Point point) {
    return new ReadResponse(getInternal(urlForGeocode(),
        new Geocode(point).toUrlParams()));
  }

  /**
   * Runs a <tt>facet</tt> read against the specified Factual table.
   * 
   * 
   * @param tableName
   *          the name of the table you wish to query for facets (e.g.,
   *          "places")
   * @param facet
   *          the facet query to run against <tt>table</tt>
   * @return the response of running <tt>facet</tt> against Factual.
   */
  public FacetResponse fetch(String tableName, FacetQuery facet) {
    return new FacetResponse(getInternal(urlForFacets(tableName), facet.toUrlParams()));
  }

  /**
   * Runs a <tt>submit</tt> input against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to submit updates for (e.g.,
   *          "places")
   * @param factualId
   *          the factual id on which the submit is run
   * @param submit
   *          the submit parameters to run against <tt>table</tt>
   * @param metadata
   *          the metadata to send with information on this request
   * @return the response of running <tt>submit</tt> against Factual.
   */
  public SubmitResponse submit(String tableName, String factualId,
      Submit submit, Metadata metadata) {
    return submitCustom("t/" + tableName + "/" + factualId + "/submit", submit,
        metadata);
  }

  /**
   * Runs a <tt>insert</tt> to add a row against the specified Factual table.
   * Insert is virtually identical to submit. The only difference between the
   * two is that insert will not search for potential duplicate rows first.
   * 
   * @param tableName
   *          the name of the table you wish to insert the add for (e.g.,
   *          "places")
   * @param insert
   *          the insert parameters to run against <tt>table</tt>
   * @param metadata
   *          the metadata to send with information on this request
   * @return the response of running <tt>insert</tt> against Factual.
   */
  public InsertResponse insert(String tableName, Insert insert,
      Metadata metadata) {
    return insertCustom("t/" + tableName + "/insert", insert, metadata);
  }

  /**
   * Runs a <tt>insert</tt> against the specified Factual table. Insert is
   * virtually identical to submit. The only difference between the two is that
   * insert will not search for potential duplicate rows first.
   * 
   * @param tableName
   *          the name of the table you wish to insert updates for (e.g.,
   *          "places")
   * @param factualId
   *          the factual id on which the insert is run
   * @param insert
   *          the insert parameters to run against <tt>table</tt>
   * @param metadata
   *          the metadata to send with information on this request
   * @return the response of running <tt>insert</tt> against Factual.
   */
  public InsertResponse insert(String tableName, String factualId,
      Insert insert, Metadata metadata) {
    return insertCustom("t/" + tableName + "/" + factualId + "/insert", insert,
        metadata);
  }

  /**
   * Runs a <tt>clear</tt> against the specified Factual table. Insert is
   * virtually identical to submit. The only difference between the two is that
   * insert will not search for potential duplicate rows first.
   * 
   * @param tableName
   *          the name of the table you wish to insert updates for (e.g.,
   *          "places")
   * @param factualId
   *          the factual id on which the insert is run
   * @param clear
   *          the clear parameters to run against <tt>table</tt>
   * @param metadata
   *          the metadata to send with information on this request
   * @return the response of running <tt>insert</tt> against Factual.
   */
  public ClearResponse clear(String tableName, String factualId, Clear clear,
      Metadata metadata) {
    return clearCustom("t/" + tableName + "/" + factualId + "/clear", clear,
        metadata);
  }

  /**
   * Runs a <tt>submit</tt> to add a row against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to submit the add for (e.g.,
   *          "places")
   * @param submit
   *          the submit parameters to run against <tt>table</tt>
   * @param metadata
   *          the metadata to send with information on this request
   * @return the response of running <tt>submit</tt> against Factual.
   */
  public SubmitResponse submit(String tableName, Submit submit,
      Metadata metadata) {
    return submitCustom("t/" + tableName + "/submit", submit, metadata);
  }

  /**
   * Flags a row as a duplicate in the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to flag a duplicate for (e.g.,
   *          "places")
   * @param factualId
   *          the factual id that is the duplicate
   * @param metadata
   *          the metadata to send with information on this request
   * 
   * @return the response from flagging a duplicate row.
   */
  public FlagResponse flagDuplicate(String tableName, String factualId,
      Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "duplicate", metadata);
  }

  protected static String urlForFlag(String tableName, String factualId) {
    return "t/" + tableName + "/" + factualId + "/flag";
  }

  /**
   * Flags a row as inaccurate in the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to flag an inaccurate row for
   *          (e.g., "places")
   * @param factualId
   *          the factual id that is inaccurate
   * @param metadata
   *          the metadata to send with information on this request
   * 
   * @return the response from flagging an inaccurate row.
   */
  public FlagResponse flagInaccurate(String tableName, String factualId,
      Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "inaccurate", metadata);
  }

  /**
   * Flags a row as inappropriate in the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to flag an inappropriate row for
   *          (e.g., "places")
   * @param factualId
   *          the factual id that is inappropriate
   * @param metadata
   *          the metadata to send with information on this request
   * 
   * @return the response from flagging an inappropriate row.
   */
  public FlagResponse flagInappropriate(String tableName, String factualId,
      Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "inappropriate",
        metadata);
  }

  /**
   * Flags a row as non-existent in the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to flag a non-existent row for
   *          (e.g., "places")
   * @param factualId
   *          the factual id that is non-existent
   * @param metadata
   *          the metadata to send with information on this request
   * 
   * @return the response from flagging a non-existent row.
   */
  public FlagResponse flagNonExistent(String tableName, String factualId,
      Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "nonexistent", metadata);
  }

  /**
   * Flags a row as spam in the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to flag a row as spam (e.g.,
   *          "places")
   * @param factualId
   *          the factual id that is spam
   * @param metadata
   *          the metadata to send with information on this request
   * 
   * @return the response from flagging a row as spam.
   */
  public FlagResponse flagSpam(String tableName, String factualId,
      Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "spam", metadata);
  }

  /**
   * Flags a row as problematic in the specified Factual table.
   * 
   * @param tableName
   *          the name of the table for which you wish to flag as problematic
   *          (e.g., "places")
   * @param factualId
   *          the factual id that has a problem other than duplicate,
   *          inaccurate, inappropriate, non-existent, or spam.
   * @param metadata
   *          the metadata to send with information on this request
   * 
   * @return the response from flagging a row as problematic.
   */
  public FlagResponse flagOther(String tableName, String factualId,
      Metadata metadata) {
    return flagCustom(urlForFlag(tableName, factualId), "other", metadata);
  }

  /**
   * Runs a GET request against the specified endpoint path, using the given
   * parameters and your OAuth credentials. Returns the raw response body
   * returned by Factual.
   * <p>
   * The necessary URL base will be automatically prepended to <tt>path</tt>. If
   * you need to change it, e.g. to make requests against a development instance
   * of the Factual service, please see {@link #setFactHome(String)}.
   * 
   * @param path
   *          the endpoint path to run the request against. example: "t/places"
   * @param queryParams
   *          the query string parameters to send with the request. do not
   *          encode or escape these; that will be done automatically.
   * @return the response body from running this GET request against Factual.
   * @throws FactualApiException
   *           if something goes wrong.
   */
  public String get(String path, Map<String, Object> queryParams) {
    return getInternal(path, queryParams).getContent();
  }

  private InternalResponse getInternal(String path, Map<String, Object> queryParams) {
    return request(new RawReadRequest(path, queryParams));
  }

  /**
   * 
   * Runs a "GET" request against the path specified using the parameter string
   * specified and your Oauth token.
   * 
   * @param path
   *          the path to run the request against
   * @param params
   *          the url-encoded parameter string to send with the request
   * @return the response body from running this GET request against Factual.
   */
  public String get(String path, String params) {
    return request(new SimpleGetRequest(path, params)).getContent();
  }

  /**
   * Runs a POST request against the specified endpoint path, using the given
   * parameters and your OAuth credentials. Returns the raw response body
   * returned by Factual.
   * <p>
   * The necessary URL base will be automatically prepended to <tt>path</tt>. If
   * you need to change it, e.g. to make requests against a development instance
   * of the Factual service, please see {@link #setFactHome(String)}.
   * 
   * @param path
   *          the endpoint path to run the request against. example: "t/places"
   * @param queryParams
   *          the query parameters to send with the request. send null or empty
   *          to specify none. do not encode or escape these; that will be done
   *          automatically.
   * @param postContent
   *          the POST content parameters to send with the request. do not
   *          encode or escape these; that will be done automatically.
   * @return the response body from running this POST request against Factual.
   * @throws FactualApiException
   *           if something goes wrong.
   */
  public String post(String path, Map<String, Object> queryParams,
      Map<String, String> postContent) {
    return postInternal(path, queryParams, postContent).getContent();
  }

  private InternalResponse postInternal(String path, Map<String, Object> queryParams,
      Map<String, String> postContent) {
    return requestPost(new RawReadRequest(path, queryParams, postContent));
  }

  public FactualStream stream(String tableName, DiffsQuery diff, DiffsCallback cb) {
    return stream(new DiffsRequest(urlForFetch(tableName) + "/diffs", diff.toUrlParams(), cb), "GET", true);
  }

  public DiffsResponse fetch(String tableName, DiffsQuery diff) {
    DiffsResponse resp = new DiffsResponse();
    FactualStream stream = stream(tableName, diff, resp);
    stream.start();
    return resp;
  }

  private ClearResponse clearCustom(String root, Clear clear, Metadata metadata) {
    Map<String, Object> params = Maps.newHashMap();
    params.putAll(metadata.toUrlParams());
    params.putAll(clear.toUrlParams());
    // Oauth library currently doesn't support POST body content.
    InternalResponse resp = postInternal(root, params, new HashMap<String, String>());
    return new ClearResponse(resp);
  }

  private InsertResponse insertCustom(String root, Insert insert,
      Metadata metadata) {
    Map<String, Object> params = Maps.newHashMap();
    params.putAll(metadata.toUrlParams());
    params.putAll(insert.toUrlParams());
    // Oauth library currently doesn't support POST body content.
    InternalResponse resp = postInternal(root, params, new HashMap<String, String>());
    return new InsertResponse(resp);
  }

  private SubmitResponse submitCustom(String root, Submit submit,
      Metadata metadata) {
    Map<String, Object> params = Maps.newHashMap();
    params.putAll(metadata.toUrlParams());
    params.putAll(submit.toUrlParams());
    // Oauth library currently doesn't support POST body content.
    InternalResponse resp = postInternal(root, params, new HashMap<String, String>());
    return new SubmitResponse(resp);
  }

  private FlagResponse flagCustom(String root, String flagType,
      Metadata metadata) {
    Map<String, Object> params = Maps.newHashMap();
    params.putAll(metadata.toUrlParams());
    params.put("problem", flagType);
    // Oauth library currently doesn't support POST body content.
    InternalResponse resp = postInternal(root, params, new HashMap<String, String>());
    return new FlagResponse(resp);
  }

  /**
   * Use this to send all queued reads as a multi request
   * 
   * @return response for a multi request
   */
  public MultiResponse sendRequests(MultiRequest multiRequest) {
    Map<String, String> multi = Maps.newHashMap();
    Map<String, RequestImpl> queries = multiRequest.getQueries();
    for (Entry<String, RequestImpl> entry : queries.entrySet()) {
      RequestImpl fullQuery = entry.getValue();
      String url = "/" + fullQuery.toUrlString();
      multi.put(entry.getKey(), url);
    }

    String json = JsonUtil.toJsonStr(multi);
    Map<String, Object> params = Maps.newHashMap();
    params.put("queries", json);
    InternalResponse internalResp = getInternal("multi", params);
    MultiResponse resp = new MultiResponse(queries);
    resp.setJson(internalResp.getContent());
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
    return new ReadResponse(getInternal(urlForMonetize(), query.toUrlParams()));
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
  public ResolveResponse resolves(ResolveQuery query) {
    return resolves("places", query);
  }

  /**
   * Asks Factual to resolve the entity for the attributes specified by
   * <tt>query</tt>.
   * <p>
   * Returns the read response from a Factual Resolve request, which includes
   * all records that are potential matches.
   * 
   * @param query
   *          the Resolve query to run against Factual's Places table.
   * @return the response from Factual for the Resolve request.
   */
  public ResolveResponse resolves(String tableId, ResolveQuery query) {
    query.allCandidates();
    return fetch(tableId, query);
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
    return resolve("places", query);
  }

  /**
   * Asks Factual to resolve the entity for the attributes specified by
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
  public Map<String, Object> resolve(String tableId, ResolveQuery query) {
    ResolveResponse resp = resolves(tableId, query);
    if (resp.isResolved())
      return resp.getResolved();
    else
      return null;
  }

  /**
   * Asks Factual to resolve the entity for the attributes specified by
   * <tt>query</tt>. Returns a factual id for the resolved entity if Factual
   * successfully identified the entity with full confidence, or null if the
   * entity was not resolved.
   * 
   * @param query
   *          a Match query with partial attributes for an entity.
   * @return a factual id for the resolved entity if Factual successfully
   *         identified the entity with full confidence, or null if the entity
   *         was not resolved.
   */
  public String match(String tableId, MatchQuery query) {
    ResolveResponse resp = new ResolveResponse(request(new ReadRequest(
        urlForMatch(tableId), query.toUrlParams())));
    if (resp.getData().size() > 0)
      return String.valueOf(resp.getData().get(0).get("factual_id"));
    else
      return null;
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
  public ResolveResponse fetch(String tableName, ResolveQuery query) {
    return new ResolveResponse(request(new ReadRequest(
        urlForResolve(tableName), query.toUrlParams())));
  }

  public SchemaResponse schema(String tableName) {
    Map<String, Object> params = Maps.newHashMap();
    return new SchemaResponse(getInternal(urlForSchema(tableName), params));
  }

  private String urlForSchema(String tableName) {
    return "t/" + tableName + "/schema";
  }

  private InternalResponse request(Request query) {
    return request(query, true);
  }

  private InternalResponse request(Request query, boolean useOAuth) {
    return request(query, "GET", useOAuth);
  }

  private InternalResponse requestPost(Request query) {
    return requestPost(query, true);
  }

  private InternalResponse requestPost(Request query, boolean useOAuth) {
    return request(query, "POST", useOAuth);
  }

  private InternalResponse request(Request fullQuery, String requestMethod,
      boolean useOAuth) {
    String urlStr = factHome + fullQuery.toUrlString();

    BufferedReader br = null;
    try {
      HttpRequest request = createRequest(urlStr, fullQuery, requestMethod, useOAuth);
      // get the response
      HttpResponse resp = request.execute();

      InternalResponse internalResponse = new InternalResponse(resp, fullQuery.getLineCallback());
      /*
      br = new BufferedReader(new InputStreamReader(resp
          .getContent()));
      String line = null;
      StringBuffer sb = new StringBuffer();
      LineCallback cb = fullQuery.getLineCallback();
      while ((line = br.readLine()) != null) {
        if (cb != null)
          cb.onLine(line);
        sb.append(line);
      }
      return sb.toString();
       */
      return internalResponse;
    } catch (HttpResponseException e) {
      throw new FactualApiException(e).requestUrl(urlStr)
      .requestMethod(requestMethod).response(e.getStatusCode(), e.getMessage());
    } catch (IOException e) {
      throw new FactualApiException(e).requestUrl(urlStr).requestMethod(
          requestMethod);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    } finally {
      Closeables.closeQuietly(br);
    }
  }

  private FactualStream stream(Request fullQuery, String requestMethod,
      boolean useOAuth) {
    String urlStr = factHome + fullQuery.toUrlString();

    BufferedReader br = null;
    try {
      HttpRequest request = createRequest(urlStr, fullQuery, requestMethod, useOAuth);

      // get the response
      br = new BufferedReader(new InputStreamReader(request.execute()
          .getContent()));
      return new FactualStream(br, fullQuery.getLineCallback());
    } catch (HttpResponseException e) {
      throw new FactualApiException(e).requestUrl(urlStr)
      .requestMethod(requestMethod).response(e.getStatusCode(), e.getMessage());
    } catch (IOException e) {
      throw new FactualApiException(e).requestUrl(urlStr).requestMethod(
          requestMethod);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpRequest createRequest(String urlStr, Request fullQuery, String requestMethod,
      boolean useOAuth) throws GeneralSecurityException, IOException {
    Map<String, String> postData = fullQuery.getPostData();
    GenericUrl url = new GenericUrl(urlStr);
    if (debug) {
      fullQuery.printDebug();
      logger = Logger.getLogger(HttpTransport.class.getName());
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

    if (readTimeout != -1)
      request.setReadTimeout(readTimeout);
    if (connectionTimeout != -1)
      request.setConnectTimeout(connectionTimeout);

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Factual-Lib", DRIVER_HEADER_TAG);
    headers.set("Host", host);
    request.setHeaders(headers);
    return request;
  }

  /**
   * Set the driver in or out of debug mode.
   * 
   * @param debug
   *          whether or not this is in debug mode
   */
  public synchronized void debug(boolean debug) {
    this.debug = debug;
    if (debug && debugHandler == null) {
      debugHandler = new StreamHandler(System.out, new SimpleFormatter());
      debugHandler.setLevel(Level.ALL);
    }
  }

  /**
   * Sets the timeout in milliseconds to establish a connection or {@code 0} for
   * an infinite timeout.
   * 
   * <p>
   * By default it is 20000 (20 seconds).
   * </p>
   */
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  /**
   * Sets the timeout in milliseconds to read data from an established
   * connection or {@code 0} for an infinite timeout.
   * 
   * <p>
   * By default it is 20000 (20 seconds).
   * </p>
   */
  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  protected static interface Request {

    public String toUrlString();

    public Map<String, String> getPostData();

    public Response getResponse(InternalResponse resp);

    public void printDebug();

    public LineCallback getLineCallback();
  }

  protected static class ReadRequest extends RequestImpl {
    public ReadRequest(String path, Map<String, Object> params) {
      super(path, params);
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new ReadResponse(resp);
    }

  }

  /**
   * Represents a request against Factual given a path and parameters
   * 
   * @author brandon
   * 
   */
  protected static abstract class RequestImpl implements Request {

    private final Map<String, Object> params;
    private final Map<String, String> postData;
    private final String path;
    protected LineCallback cb;

    public RequestImpl(String path, Map<String, Object> params) {
      this(path, params, new HashMap<String, String>());
    }

    public RequestImpl(String path, Map<String, Object> params,
        Map<String, String> postData) {
      this.path = path;
      this.params = params;
      this.postData = postData;
    }

    public Map<String, Object> getRequestParams() {
      return params;
    }

    @Override
    public String toUrlString() {
      return UrlUtil.toUrl(path, getRequestParams());
    }

    @Override
    public Map<String, String> getPostData() {
      return postData;
    }

    @Override
    public abstract Response getResponse(InternalResponse resp);

    @Override
    public void printDebug() {
      System.out.println("=== " + path + " ===");
      System.out.println("Parameters:");
      if (params != null) {
        for (String key : params.keySet()) {
          System.out.println(key + ": " + params.get(key));
        }
      }
    }

    @Override
    public LineCallback getLineCallback() {
      return cb;
    }

  }

  protected static class ResolveRequest extends RequestImpl {
    public ResolveRequest(String path, Map<String, Object> params) {
      super(path, params);
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new ResolveResponse(resp);
    }
  }

  protected static class FacetRequest extends RequestImpl {

    public FacetRequest(String path, Map<String, Object> params) {
      super(path, params);
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new FacetResponse(resp);
    }

  }

  protected static class SchemaRequest extends RequestImpl {

    public SchemaRequest(String path, Map<String, Object> params) {
      super(path, params);
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new SchemaResponse(resp);
    }

  }

  protected static class DiffsRequest extends RequestImpl {

    public DiffsRequest(String path, Map<String, Object> params, final DiffsCallback cb) {
      super(path, params);
      this.cb = new LineCallback() {
        @Override
        public void onLine(String line) {
          cb.onDiff(line);
        }
      };
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new RawReadResponse(resp);
    }

  }

  protected static class RawReadRequest extends RequestImpl {

    public RawReadRequest(String path, Map<String, Object> params) {
      super(path, params);
    }

    public RawReadRequest(String path, Map<String, Object> params,
        Map<String, String> postData) {
      super(path, params, postData);
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new RawReadResponse(resp);
    }

  }

  protected static class SimpleGetRequest implements Request {
    private final String path;
    private final String params;

    public SimpleGetRequest(String path, String params) {
      this.path = path;
      this.params = params;
    }

    @Override
    public String toUrlString() {
      return UrlUtil.toUrl(path, params);
    }

    @Override
    public Map<String, String> getPostData() {
      return null;
    }

    @Override
    public Response getResponse(InternalResponse resp) {
      return new RawReadResponse(resp);
    }

    @Override
    public void printDebug() {
      System.out.println("=== " + path + " ===");
      System.out.println("Parameters:");
      System.out.println(params);
    }

    @Override
    public LineCallback getLineCallback() {
      return null;
    }
  }
}
