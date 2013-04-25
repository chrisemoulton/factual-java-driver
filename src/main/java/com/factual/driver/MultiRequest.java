package com.factual.driver;

import java.util.HashMap;
import java.util.Map;

import com.factual.driver.Factual.FacetRequest;
import com.factual.driver.Factual.RawReadRequest;
import com.factual.driver.Factual.ReadRequest;
import com.factual.driver.Factual.RequestImpl;
import com.factual.driver.Factual.ResolveRequest;

public class MultiRequest {

  private final Map<String, RequestImpl> queries = new HashMap<String, RequestImpl>();

  /**
   * Add a raw read request for inclusion in the next multi request.
   * 
   * @param queryName
   *          the name for this subquery within the multi request
   * @param path
   *          the path to run the request against
   * @param params
   *          the parameters to send with the request
   */
  public void addQuery(String queryName, String path, Map<String, Object> params) {
    queries.put(queryName, new RawReadRequest(path, params));
  }

  /**
   * Add a read request for inclusion in the next multi request.
   * 
   * @param queryName
   *          the name for this subquery within the multi request
   * @param table
   *          the name of the table you wish to query (e.g., "places")
   * @param query
   *          the read query to run against <tt>table</tt>.
   */
  public void addQuery(String queryName, String table, Query query) {
    queries.put(queryName, new ReadRequest(Factual.urlForFetch(table), query.toUrlParams()));
  }

  /**
   * Add a resolve request for inclusion in the next multi request.
   * 
   * @param queryName
   *          the name for this subquery within the multi request
   * @param table
   *          the name of the table you wish to use resolve against (e.g.,
   *          "places")
   * @param query
   *          the resolve query to run against <tt>table</tt>.
   */
  public void addQuery(String queryName, String table, ResolveQuery query) {
    queries.put(queryName, new ResolveRequest(Factual.urlForResolve(table), query.toUrlParams()));
  }

  /**
   * Add a facet request for inclusion in the next multi request.
   * 
   * @param queryName
   *          the name for this subquery within the multi request
   * @param table
   *          the name of the table you wish to use a facet request against
   *          (e.g., "places")
   * @param query
   *          the facet query to run against <tt>table</tt>.
   */
  public void addQuery(String queryName, String table, FacetQuery query) {
    queries.put(queryName, new FacetRequest(Factual.urlForFacets(table), query.toUrlParams()));
  }

  /**
   * Add a geocode request for inclusion in the next multi request.
   * 
   * @param queryName
   *          the name for this subquery within the multi request
   * @param query
   *          the geocode query to run
   */
  public void addQuery(String queryName, Geocode query) {
    queries.put(queryName, new ReadRequest(Factual.urlForGeocode(), query.toUrlParams()));
  }

  /**
   * Add a geopulse request for inclusion in the next multi request.
   * 
   * @param queryName
   *          the name for this subquery within the multi request
   * @param query
   *          the geopulse query to run
   */
  public void addQuery(String queryName, Geopulse query) {
    queries.put(queryName, new ReadRequest(Factual.urlForGeopulse(), query.toUrlParams()));
  }

  /**
   * Remove a query from the next multi request.
   * 
   * @param queryName
   *          the name for the subquery to remove from the multi request
   */
  public void removeQuery(String queryName) {
    queries.remove(queryName);
  }

  /*
  public Iterator<Entry<String, String>> iterator() {
     Map<String, String> nameURLMap = new HashMap<String, String>();
     for(Entry<String, RequestImpl> entry : queries.entrySet()) {
       nameURLMap.put(entry.getKey(), entry.getValue().toUrlString());
     }
     return nameURLMap.entrySet().iterator();
  }
   */

  protected Map<String, RequestImpl> getQueries() {
    return queries;
  }
}
