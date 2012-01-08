package com.factual.driver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Represents a Factual Crosswalk query.
 * 
 * @author aaron
 */
public class CrosswalkQuery {
  private String factualId;
  private int limit;
  private String namespace;
  private String namespaceId;
  private final Set<String> only = Sets.newHashSet();


  /**
   * Adds the specified Factual ID to this Query. Returned Crosswalk data will
   * be for only the entity associated with the Factual ID.
   * 
   * @param factualId
   *          a unique Factual ID.
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery factualId(String factualId) {
    this.factualId = factualId;
    return this;
  }

  /**
   * Adds the specified <tt>limit</tt> to this Query. The amount of returned
   * Crosswalk records will not exceed this limit.
   * 
   * @param factualId
   *          a unique Factual ID.
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery limit(int limit) {
    this.limit = limit;
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
    this.namespace = namespace;
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
    this.namespaceId = namespaceId;
    return this;
  }

  /**
   * Restricts the results to only return ids for the specified namespace(s).
   * 
   * @return this CrosswalkQuery
   */
  public CrosswalkQuery only(String... namespaces) {
    for(String ns : namespaces) {
      only.add(ns);
    }
    return this;
  }

  protected String toUrlQuery() {
    return Joiner.on("&").skipNulls().join(
        urlPair("factual_id", factualId),
        (limit > 0 ? urlPair("limit", limit) : null),
        urlPair("namespace", namespace),
        urlPair("namespace_id", namespaceId),
        urlPair("only", onlysOrNull()));
  }

  private String onlysOrNull() {
    if(!only.isEmpty()) {
      return Joiner.on(",").skipNulls().join(only);
    } else {
      return null;
    }
  }

  private String urlPair(String name, Object val) {
    if(val != null) {
      try {
        return name + "=" + (val instanceof String ? URLEncoder.encode(val.toString(), "UTF-8") : val);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    } else {
      return null;
    }
  }

}
