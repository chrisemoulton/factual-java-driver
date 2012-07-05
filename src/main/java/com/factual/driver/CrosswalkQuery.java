package com.factual.driver;

import java.util.Map;

/**
 * Represents a Factual Crosswalk query.
 * 
 * @deprecated Use a standard read query instead. For example:
 *             factual.fetch("crosswalk", new Query())
 * @author aaron
 */
@Deprecated
public class CrosswalkQuery {

  /**
   * Holds all parameters for this CrosswalkQuery.
   */
  private final Parameters queryParams = new Parameters();

  /**
   * 
   * @deprecated Use a standard read query instead. For example:
   *             factual.fetch("crosswalk", new
   *             Query().field("factual_id").equal(factualId))
   * 
   */
  @Deprecated
  public CrosswalkQuery() {

  }

  /**
   * Adds the specified Factual ID to this Query. Returned Crosswalk data will
   * be for only the entity associated with the Factual ID.
   * 
   * @deprecated Use a standard read query instead. For example:
   *             factual.fetch("crosswalk", new
   *             Query().field("factual_id").equal(factualId))
   * @param factualId
   *          a unique Factual ID.
   * @return this CrosswalkQuery
   */
  @Deprecated
  public CrosswalkQuery factualId(String factualId) {
    queryParams.setParam(Constants.CROSSWALK_FACTUAL_ID, factualId);
    return this;
  }

  /**
   * Adds the specified <tt>limit</tt> to this Query. The amount of returned
   * Crosswalk records will not exceed this limit.
   * 
   * @deprecated Use a standard read query instead. For example:
   *             factual.fetch("crosswalk", new Query().limit(limit))
   * @param limit
   *          The amount of returned Crosswalk records this Query will not
   *          exceed.
   * @return this CrosswalkQuery
   */
  @Deprecated
  public CrosswalkQuery limit(int limit) {
    queryParams.setParam(Constants.CROSSWALK_LIMIT, limit);
    return this;
  }

  /**
   * The namespace to search for a third party ID within.
   * 
   * @deprecated Use a standard read query instead.
   * @param namespace
   *          The namespace to search for a third party ID within.
   * @return this CrosswalkQuery
   */
  @Deprecated
  public CrosswalkQuery namespace(String namespace) {
    queryParams.setParam(Constants.CROSSWALK_NAMESPACE, namespace);
    return this;
  }

  /**
   * The id used by a third party to identify a place.
   * <p>
   * You must also supply <tt>namespace</tt> via {@link #namespace(String)}.
   * 
   * @deprecated Use a standard read query instead.
   * @param namespaceId
   *          The id used by a third party to identify a place.
   * @return this CrosswalkQuery
   */
  @Deprecated
  public CrosswalkQuery namespaceId(String namespaceId) {
    queryParams.setParam(Constants.CROSSWALK_NAMESPACE_ID, namespaceId);
    return this;
  }

  /**
   * Restricts the results to only return ids for the specified namespace(s).
   * 
   * @deprecated Use a standard read query instead.
   * @return this CrosswalkQuery
   */
  @Deprecated
  public CrosswalkQuery only(String... namespaces) {
    for (String namespace : namespaces)
      queryParams.addCommaSeparatedParam(Constants.CROSSWALK_ONLY, namespace);
    return this;
  }

  protected Map<String, Object> toUrlParams() {
    return queryParams.toUrlParams();
  }

  protected String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }
}