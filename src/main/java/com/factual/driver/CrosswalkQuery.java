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
    queryParams.setParam("factual_id", factualId);
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
	queryParams.setParam("limit", limit);
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
    queryParams.setParam("namespace", namespace);
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
	queryParams.setParam("namespace_id", namespaceId);
    return this;
  }

  /**
   * Restricts the results to only return ids for the specified namespace(s).
   * 
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery only(String... namespaces) {
	for (String namespace : namespaces)
		queryParams.addCommaSeparatedParam("only", namespace);
	return this;
  }
  
  /**
   * Set a parameter and value pair, where the value will be serialized as json.  
   * Maps and lists to specify object and array structure respectively.
   * Useful when adding a parameter that is json in structure, but does not yet have driver convenience methods.
   * 
   * For example, the following value as input:
   * <pre>
   * {@code
   * new HashMap() {{
   * put("$and", new Map[] {
   * 	new HashMap() {{
   * 		put("name", new HashMap() {{
   *  			put("$bw", "McDonald's");
   *  		}});	
   *  		put("category", new HashMap() {{
   *  			put("$bw", "Food & Beverage");
   *  		}});
   * 	}}});
   * }};
   * }
   * </pre>
   * Will be serialized to json as:
   * {"$and":[{"name":{"$bw":"McDonald's"},"category":{"$bw":"Food & Beverage"}}
   * 
   * @param key the field name of the parameter to be serialized as json
   * @param value the field value that is an object to be serialized as json
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery addJsonParam(String key, Object value) {
	queryParams.setJsonParam(key, value);
	return this;
  }
  
  /**
   * Set a parameter and value pair for specifying url parameters, especially those not yet available as convenience methods.
   * @param key the field name of the parameter to add
   * @param value the field value that will be serialized using value.toString()
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery addParam(String key, Object value) {
	queryParams.setParam(key, value);
	return this;
  }

  protected String toUrlQuery() {
	return queryParams.toUrlQuery(true);
  }
  
}