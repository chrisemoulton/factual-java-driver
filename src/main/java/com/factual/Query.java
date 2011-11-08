package com.factual;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Represents a top level Factual query. Knows how to represent the query as URL
 * encoded key value pairs, ready for the query string in a GET request. (See
 * {@link #toUrlPairs()})
 * 
 * @author aaron
 */
public class Query {
  private String fullTextSearch;
  private int limit;
  private int offset;
  private boolean includeRowCount;
  private Circle circle;
  private final List<Logic> rowFilters = Lists.newArrayList();


  /**
   * Sets a full text search query. Factual will use this value to perform a
   * full text search against various attributes of the underlying table, such
   * as entity name, address, etc.
   * 
   * @param fullTextSearch
   *          the text for which to perform a full text search.
   * @return this Query
   */
  public Query fullTextSearch(String fullTextSearch) {
    this.fullTextSearch = fullTextSearch;
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

  /**
   * When true, the response will include a count of the total number of rows in
   * the table that conform to the request based on included filters.
   * Requesting the row count will increase the time required to return a
   * response. The default behavior is to NOT include a row count.
   * 
   * @param includeRowCount
   *          true if you want the results to include a count of the total
   *          number of rows in the table that conform to the request based on
   *          included filters.
   * @return this Query.
   */
  public Query includeRowCount(boolean includeRowCount) {
    this.includeRowCount = includeRowCount;
    return this;
  }

  public Query filter(String field, String val) {
    rowFilters.add(new Logic("$eq", field, val));
    return this;
  }

  public Query filter(String op, Object... args) {
    rowFilters.add(new Logic(op, args));
    return this;
  }

  /**
   * Adds a filter so that results can only be (roughly) within the specified
   * geographic circle.
   * 
   * @param circle The circle within which to bound the results.
   * @return this Query.
   */
  public Query within(Circle circle) {
    this.circle = circle;
    return this;
  }

  public String toUrlPairs() {
    return Joiner.on("&").skipNulls().join(
        urlPair("q", fullTextSearch),
        (limit > 0 ? urlPair("limit", limit) : null),
        (offset > 0 ? urlPair("offset", offset) : null),
        (includeRowCount ? urlPair("include_count", true) : null),
        urlPair("filters", rowFiltersJsonOrNull()),
        urlPair("geo", geoBoundsJsonOrNull()));
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

  private String geoBoundsJsonOrNull() {
    if(circle != null) {
      return circle.toJsonStr();
    } else {
      return null;
    }
  }

  private String rowFiltersJsonOrNull() {
    if(rowFilters.isEmpty()) {
      return null;
    } else {
      Logic[] preds = rowFilters.toArray(new Logic[]{});
      return new Logic("$and", preds).toString();
    }
  }

}
