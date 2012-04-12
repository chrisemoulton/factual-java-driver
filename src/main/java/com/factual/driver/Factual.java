package com.factual.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.base.Joiner;
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
  private static final String DRIVER_HEADER_TAG = "factual-java-driver-v1.2.0";
  private String factHome = "http://api.v3.factual.com/";
  private final String key;
  private final OAuthHmacSigner signer;
  private boolean debug = false;
  private StreamHandler debugHandler = null;
  
  private Queue<FullQuery> fetchQueue = Lists.newLinkedList();
  

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
   * Runs a read <tt>query</tt> against the specified Factual table.
   * 
   * @param tableName
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the read query to run against <tt>table</tt>.
   * @return the response of running <tt>query</tt> against Factual.
   */
  public ReadResponse fetch(String tableName, Query query) {
    return fetchCustom(urlForFetch(tableName), query);
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
	return fetchCustom(urlForFacets(tableName), facet);
  }

  /**
   * Runs a <tt>contribute</tt> input against the specified Factual table.
   * 
   * @param tableName
   * 		  the name of the table you wish to contribute updates for (e.g., "places")
   * @param factualId
   * 		  the factual id on which the contribute is run
   * @param contribute
   * 		  the contribute parameters to run against <tt>table</tt>
   * @param metadata
   * 		  the metadata to send with information on this request
   * 	  	 
   * @return the response of running <tt>contribute</tt> against Factual.
   */
  public ContributeResponse contribute(String tableName, String factualId, Contribute contribute, Metadata metadata) {
	return contributeCustom("t/"+tableName+"/"+factualId+"/contribute", contribute, metadata);
  }

  /**
   * Runs a <tt>contribute</tt> to add a row against the specified Factual table.
   * 
   * @param tableName
   * 		  the name of the table you wish to contribute the add for (e.g., "places")
   * @param contribute
   * 		  the contribute parameters to run against <tt>table</tt>
   * @param metadata
   * 		  the metadata to send with information on this request
   * 	  	 
   * @return the response of running <tt>contribute</tt> against Factual.
   */
  public ContributeResponse contribute(String tableName, Contribute contribute, Metadata metadata) {
	return contributeCustom("t/"+tableName+"/contribute", contribute, metadata);
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
   * @param params the parameters to send with the request, URL-encoded
   * @return the response of running <tt>query</tt> against Factual.
   */
  public String get(String path, Map<String, Object> params) {
	List<Object> paramList = Lists.newArrayList();
	for (Entry<String, Object> entry : params.entrySet()) {
		paramList.add(Parameters.urlPair(entry.getKey(), String.valueOf(entry.getValue()), true));
	}
	String paramString = Joiner.on("&").skipNulls().join(paramList);
	return request(toUrl(factHome + path, paramString));
  }
  
  private ReadResponse fetchCustom(String root, Query query) {
	return new ReadResponse(request(toUrl(factHome + root, query.toUrlQuery())));
  }
  
  private FacetResponse fetchCustom(String root, FacetQuery facet) {
	return new FacetResponse(request(toUrl(factHome + root, facet.toUrlQuery())));
  }
  
  private CrosswalkResponse fetchCustom(String root, CrosswalkQuery query) {
	return new CrosswalkResponse(request(toUrl(factHome + root, query.toUrlQuery())));
  } 
  
  private ReadResponse fetchCustom(String root, ResolveQuery query) {
	return new ReadResponse(request(toUrl(factHome + root, query.toUrlQuery())));
  }
  
  private ContributeResponse contributeCustom(String root, Contribute contribute, Metadata metadata) {
	Map<String, String> params = Maps.newHashMap();
	// TODO: Switch parameters to POST content when supported.
	//params.putAll(metadata.toParamMap());
	//params.putAll(contribute.toParamMap());
	return new ContributeResponse(requestPost(factHome + root+"?"+contribute.toUrlQuery()+"&"+metadata.toUrlQuery(), params));
  }

  private FlagResponse flagCustom(String root, String flagType, Metadata metadata) {
	Map<String, String> params = Maps.newHashMap();
	// TODO: Switch parameters to POST content when supported.
	//params.putAll(metadata.toParamMap());
	//params.put("problem", flagType);
	return new FlagResponse(requestPost(factHome + root+"?problem="+flagType+"&"+metadata.toUrlQuery(), params));
  }
  
  private String toUrl(String root, String parameters) {
	return root + "?" + parameters;
  }

  public DiffsResponse fetch(String tableName, DiffsQuery diff) {
	return fetchCustom(urlForFetch(tableName)+"/diffs", diff);
  }

  private DiffsResponse fetchCustom(String root, DiffsQuery diff) {
	return new DiffsResponse(request(toUrl(factHome + root, diff.toUrlQuery())));
  }

  private class FullQuery {
	  protected Object query;
	  protected String table;
	  public FullQuery(String table, Object query) {
		  this.table = table;
		  this.query = query;
	  }
  }
  
  /**
   * Queue a read request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the read query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, Query query) {
	fetchQueue.add(new FullQuery(table, query));
  }

  /**
   * Queue a crosswalk request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to use crosswalk against (e.g., "places")
   * @param query
   *          the crosswalk query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, CrosswalkQuery query) {
	fetchQueue.add(new FullQuery(table, query));
  }

  /**
   * Queue a resolve request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to use resolve against (e.g., "places")
   * @param query
   *          the resolve query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, ResolveQuery query) {
	fetchQueue.add(new FullQuery(table, query));
  }
  
  /**
   * Queue a facet request for inclusion in the next multi request.
   * @param table
   *          the name of the table you wish to use a facet request against (e.g., "places")
   * @param query
   *          the facet query to run against <tt>table</tt>.
   */
  public void queueFetch(String table, FacetQuery query) {
	fetchQueue.add(new FullQuery(table, query));
  }
  
  /**
   * Use this to send all queued reads as a multi request
   * @return response for a multi request
   */
  public MultiResponse sendRequests() {
	Map<String, String> multi = Maps.newHashMap();
	int i = 0;
	Map<String, Object> requestMapping = Maps.newHashMap();
	while (!fetchQueue.isEmpty()) {
		FullQuery fullQuery = fetchQueue.poll();
		String url = null;
		Object query = fullQuery.query;
		String table = fullQuery.table;
	    if (query instanceof Query) {
			url = toUrl("/"+urlForFetch(table), ((Query)query).toUrlQuery());
	    } else if (query instanceof CrosswalkQuery) {
			url = toUrl("/"+urlForCrosswalk(table), ((CrosswalkQuery)query).toUrlQuery());
	    } else if (query instanceof ResolveQuery) {
			url = toUrl("/"+urlForResolve(table), ((ResolveQuery)query).toUrlQuery());
	    } else if (query instanceof FacetQuery) {
			url = toUrl("/"+urlForFacets(table), ((FacetQuery)query).toUrlQuery());
	    }
		if (url != null) {
			String multiKey = "q"+i;
			multi.put(multiKey, url);
			requestMapping.put(multiKey, query);
			i++;
		}
	}
	String json = JsonUtil.toJsonStr(multi);
	String url = "";
	try {
		String encoded = URLEncoder.encode(json, "UTF-8");
		url = toUrl(factHome + "multi", "queries=" + encoded+"&"+"KEY="+key);
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	String jsonResponse = request(url, false);
	MultiResponse resp = new MultiResponse(requestMapping);
	resp.setJson(jsonResponse);
	return resp;
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
    return fetchCustom(urlForCrosswalk(tableName), query);
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
    return fetchCustom(urlForResolve(tableName), query);
  }

  public SchemaResponse schema(String tableName) {
    return new SchemaResponse(request(factHome+urlForSchema(tableName)));
  }

  private String urlForSchema(String tableName) {
    return "t/" + tableName + "/schema";
  }

  private String urlForCrosswalk(String tableName) {
    return tableName + "/crosswalk";
  }

  private String urlForResolve(String tableName) {
    return tableName + "/resolve";
  }

  private String urlForFetch(String tableName) {
    return "t/" + tableName;
  }

  private String urlForFlag(String tableName, String factualId) {
	return "t/"+tableName+"/"+factualId+"/flag";
  }

  private String urlForFacets(String tableName) {
	return "t/" + tableName+"/facets";
  }
  
  private String request(String urlStr) {
	  return request(urlStr, true);
  }
  
  private String request(String urlStr, boolean useOAuth) {
	  return request(urlStr, "GET", null, useOAuth);
  }

  private String requestPost(String urlStr, Map<String, String> postData) {
	  return requestPost(urlStr, postData, true);
  }

  private String requestPost(String urlStr, Map<String, String> postData, boolean useOAuth) {
	  return request(urlStr, "POST", postData, useOAuth);
  }
  
  private String request(String urlStr, String requestMethod, Map<String, String> postData, boolean useOAuth) {
    GenericUrl url = new GenericUrl(urlStr);
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
      request.setHeaders(headers);
      if (debug) {
          Logger logger = Logger.getLogger(HttpTransport.class.getName());
          logger.removeHandler(debugHandler);
          logger.setLevel(Level.ALL);
          logger.addHandler(debugHandler);
	  }
      
      // get the response
      br = new BufferedReader(new InputStreamReader(request.execute().getContent()));
      
      return br.readLine();
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

}
