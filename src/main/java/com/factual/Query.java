package com.factual;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.common.base.Joiner;

/**
 * Represents a top level Factual query. Knows how to represent the query as URL
 * encoded key value pairs, ready for the query string in a GET request. (See
 * {@link #toUrlPairs()})
 * 
 * @author aaron
 */
public class Query {
  private String q;
  private int limit;
  private int offset;
  private boolean includeCount;
  private Circle circle;

  /**
   * Sets a full text search query
   * 
   * @param q the text for which to search
   * @return this Query
   */
  public Query q(String q) {
    this.q = q;
    return this;
  }

  public Query limit(int limit) {
    this.limit = limit;
    return this;
  }

  public Query offset(int offset) {
    this.offset = offset;
    return this;
  }

  public Query includeCount(boolean includeCount) {
    this.includeCount = includeCount;
    return this;
  }

  public Query circle(Circle circle) {
    this.circle = circle;
    return this;
  }

  public String toUrlPairs() {
    return Joiner.on("&").skipNulls().join(
        urlPair("q", q),
        urlPair("limit", limit),
        urlPair("offset", offset),
        urlPair("include_count", includeCount),
        urlPair("geo", geo()));
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

  private String geo() {
    if(circle != null) {
      return circle.toJsonStr();
    } else {
      return null;
    }
  }

}
