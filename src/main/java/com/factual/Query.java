package com.factual;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.factual.data_science_toolkit.Coord;
import com.factual.data_science_toolkit.DataScienceToolkit;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Represents a top level Factual query. Knows how to represent the query as URL
 * encoded key value pairs, ready for the query string in a GET request. (See
 * {@link #toUrlQuery()})
 * 
 * @author aaron
 */
public class Query {
  private String fullTextSearch;
  private String[] selectFields;
  private long limit;
  private long offset;
  private boolean includeRowCount;
  private Circle circle;

  /**
   * Holds all row filters for this Query. Implicit top-level AND.
   */
  private final List<Filter> rowFilters = Lists.newArrayList();

  /**
   * Holds all results sorts for this Query. Example contents:
   * <tt>"$distance:desc","name:asc","locality:asc"</tt>
   */
  private final List<String> sorts = Lists.newArrayList();


  /**
   * Sets a full text search query. Factual will use this value to perform a
   * full text search against various attributes of the underlying table, such
   * as entity name, address, etc.
   * 
   * @param term
   *          the text for which to perform a full text search.
   * @return this Query
   */
  public Query search(String term) {
    this.fullTextSearch = term;
    return this;
  }

  /**
   * Sets the maximum amount of records to return from this Query.
   * @param limit the maximum amount of records to return from this Query.
   * @return this Query
   */
  public Query limit(long limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Sets the fields to select. This is optional; default behaviour is generally
   * to select all fields in the schema.
   * 
   * @param fields
   *          the fields to select.
   * @return this Query
   */
  public Query only(String... fields) {
    this.selectFields = fields;
    return this;
  }

  /**
   * @return array of select fields set by only(), null if none.
   */
  public String[] getSelectFields() {
    return selectFields;
  }

  /**
   * Sets this Query to sort field in ascending order.
   * 
   * @param field
   *          the field to sort in ascending order.
   * @return this Query
   */
  public Query sortAsc(String field) {
    sorts.add(field + ":asc");
    return this;
  }

  /**
   * Sets this Query to sort field in descending order.
   * 
   * @param field
   *          the field to sort in descending order.
   * @return this Query
   */
  public Query sortDesc(String field) {
    sorts.add(field + ":desc");
    return this;
  }

  /**
   * Sets how many records in to start getting results (i.e., the page offset)
   * for this Query.
   * 
   * @param offset
   *          the page offset for this Query.
   * @return this Query
   */
  public Query offset(long offset) {
    this.offset = offset;
    return this;
  }

  /**
   * The response will include a count of the total number of rows in the table
   * that conform to the request based on included filters. This will increase
   * the time required to return a response. The default behavior is to NOT
   * include a row count.
   * 
   * @return this Query, marked to return total row count when run.
   */
  public Query includeRowCount() {
    return includeRowCount(true);
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

  /**
   * Begins construction of a new row filter.
   * 
   * @param field
   *          the name of the field on which to filter.
   * @return A partial representation of the new row filter.
   */
  public QueryBuilder criteria(String field) {
    return new QueryBuilder(this, field);
  }

  /**
   * Begins construction of a new row filter for this Query.
   * 
   * @param field
   *          the name of the field on which to filter.
   * @return A partial representation of the new row filter.
   */
  public QueryBuilder field(String field) {
    return new QueryBuilder(this, field);
  }

  public Query near(String text, int meters) {
    Coord coord = new DataScienceToolkit().streetToCoord(text);
    if(coord != null) {
      return within(new Circle(coord, meters));
    } else {
      throw new FactualApiException("Could not locate place based on text: '" + text + "'");
    }
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

  /**
   * Used to nest AND'ed predicates.
   */
  public Query and(Query... queries) {
    return popFilters("$and", queries);
  }

  /**
   * Used to nest OR'ed predicates.
   */
  public Query or(Query... queries) {
    return popFilters("$or", queries);
  }

  /**
   * Adds <tt>filter</tt> to this Query.
   */
  public void add(Filter filter) {
    rowFilters.add(filter);
  }

  /**
   * Builds and returns the query string to represent this Query when talking to
   * Factual's API. Provides proper URL encoding and escaping.
   * <p>
   * Example output:
   * <pre>
   * filters=%7B%22%24and%22%3A%5B%7B%22region%22%3A%7B%22%24in%22%3A%22MA%2CVT%2CNH%22%7D%7D%2C%7B%22%24or%22%3A%5B%7B%22first_name%22%3A%7B%22%24eq%22%3A%22Chun%22%7D%7D%2C%7B%22last_name%22%3A%7B%22%24eq%22%3A%22Kok%22%7D%7D%5D%7D%5D%7D
   * </pre>
   * <p>
   * (After decoding, the above example would be used by the server as:)
   * <pre>
   * filters={"$and":[{"region":{"$in":"MA,VT,NH"}},{"$or":[{"first_name":{"$eq":"Chun"}},{"last_name":{"$eq":"Kok"}}]}]}
   * </pre>
   * 
   * @return the query string to represent this Query when talking to Factual's
   *         API.
   */
  protected String toUrlQuery() {
    return Joiner.on("&").skipNulls().join(
        urlPair("select", fieldsJsonOrNull()),
        urlPair("q", fullTextSearch),
        urlPair("sort", sortsJsonOrNull()),
        (limit > 0 ? urlPair("limit", limit) : null),
        (offset > 0 ? urlPair("offset", offset) : null),
        (includeRowCount ? urlPair("include_count", true) : null),
        urlPair("filters", rowFiltersJsonOrNull()),
        urlPair("geo", geoBoundsJsonOrNull()));
  }

  @Override
  public String toString() {
    try {
      return URLDecoder.decode(toUrlQuery(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
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

  private String fieldsJsonOrNull() {
    if(selectFields != null) {
      return Joiner.on(",").join(selectFields);
    } else {
      return null;
    }
  }

  private String sortsJsonOrNull() {
    if(!sorts.isEmpty()) {
      return Joiner.on(",").join(sorts);
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
    } else if(rowFilters.size() == 1) {
      return rowFilters.get(0).toJsonStr();
    } else {
      return new FilterGroup(rowFilters).toJsonStr();
    }
  }

  /**
   * Pops the newest Filter from each of <tt>queries</tt>,
   * grouping each popped Filter into one new FilterGroup.
   * Adds that new FilterGroup as the newest Filter in this
   * Query.
   * <p>
   * The FilterGroup's logic will be determined by <tt>op</tt>.
   */
  private Query popFilters(String op, Query... queries) {
    FilterGroup group = new FilterGroup().op(op);
    for(Query q : queries) {
      group.add(pop(q.rowFilters));
    }
    add(group);
    return this;
  }

  private Filter pop(List<Filter> list) {
    return list.remove(list.size()-1);
  }

}
