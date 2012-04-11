package com.factual.driver;

/**
 * Represents a Factual Crosswalk query.
 * 
 * @author aaron
 */
public class CrosswalkQuery {

  /**
   * Holds all parameters for this CrosswalkQuery.
   */
  private final Parameters queryParams = new Parameters();

  /**
   * Adds the specified Factual ID to this Query. Returned Crosswalk data will
   * be for only the entity associated with the Factual ID.
   * 
   * @param factualId
   *          a unique Factual ID.
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery factualId(String factualId) {
    queryParams.setParam(Constants.CROSSWALK_FACTUAL_ID, factualId);
    return this;
  }

  /**
   * Adds the specified <tt>limit</tt> to this Query. The amount of returned
   * Crosswalk records will not exceed this limit.
   * 
   * @param limit
   *          The amount of returned Crosswalk records this Query will not exceed.
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery limit(int limit) {
	queryParams.setParam(Constants.CROSSWALK_LIMIT, limit);
    return this;
  }

  /**
   * The namespace to search for a third party ID within.
   * 
   * @param namespace
   *          The namespace to search for a third party ID within.
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery namespace(String namespace) {
    queryParams.setParam(Constants.CROSSWALK_NAMESPACE, namespace);
    return this;
  }

  /**
   * The id used by a third party to identify a place.
   * <p>
   * You must also supply <tt>namespace</tt> via {@link #namespace(String)}.
   * 
   * @param namespaceId
   *          The id used by a third party to identify a place.
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery namespaceId(String namespaceId) {
	queryParams.setParam(Constants.CROSSWALK_NAMESPACE_ID, namespaceId);
    return this;
  }

  /**
   * Restricts the results to only return ids for the specified namespace(s).
   * 
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery only(String... namespaces) {
	for (String namespace : namespaces)
		queryParams.addCommaSeparatedParam(Constants.CROSSWALK_ONLY, namespace);
	return this;
  }

  protected String toUrlQuery() {
	return queryParams.toUrlQuery(true);
  }
  
}