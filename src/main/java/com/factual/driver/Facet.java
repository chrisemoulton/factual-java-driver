package com.factual.driver;

import java.util.List;

import com.factual.data_science_toolkit.Coord;
import com.factual.data_science_toolkit.DataScienceToolkit;

/**
 * Represents a top level Factual facet query. Knows how to represent the facet
 * query as URL encoded key value pairs, ready for the query string in a GET
 * request. (See {@link #toUrlQuery()})
 * 
 * @author brandon
 */
public class Facet implements Filterable {

	private boolean includeRowCount;

	public Facet() {
	}

	/**
	 * Holds all parameters for this Query.
	 */
	private final Parameters queryParams = new Parameters();

	public String toUrlQuery() {
		Parameters additional = null;
		if (includeRowCount) {
			additional = new Parameters();
			additional.setParam("include_count",true);
		}
		return queryParams.toUrlQuery(additional, true);
	}

	public Facet addJsonParam(String key, Object value) {
		queryParams.setJsonParam(key, value);
		return this;
	}

	public Facet addParam(String key, Object value) {
		queryParams.setParam(key, value);
		return this;
	}

	/**
	 * For each facet count, the minimum count it must show in order to be returned in the response. Must be zero or greater. The default is 1.
	 * @param minCount for each facet count, the minimum count it must show in order to be returned in the response. Must be zero or greater. The default is 1.
	 * 
	 * @return this Facet
	 */
	public Facet minCount(long minCount) {
		addParam("min_count", minCount);
		return this;
	}

	/**
	 * The maximum number of unique facets that can be returned for a single field. Range is 1-250. The default is 25.
	 * @param facetLimit the maximum number of unique facets that can be returned for a single field. Range is 1-250. The default is 25.
	 * @return this Facet
	 */
	public Facet facetLimit(long facetLimit) {
		addParam("limit", facetLimit);
		return this;
	}

	/**
	 * The fields for which facets should be generated. The response will not be ordered identically to this list, nor will it reflect any nested relationships between fields.
	 * @param fields the fields for which facets should be generated. The response will not be ordered identically to this list, nor will it reflect any nested relationships between fields.
	 * @return this Facet
	 */
	public Facet select(String... fields) {
		for (String field : fields) {
			queryParams.addCommaSeparatedParam("select", field);
		}
		return this;
	}
	
    /**
     * Sets a full text search query. Factual will use this value to perform a
     * full text search against various attributes of the underlying table, such
     * as entity name, address, etc.
     * 
     * @param term
     *          the text for which to perform a full text search.
     * @return this Facet
     */
	public Facet search(String term) {
		addParam("q", term);
		return this;
	}

	/**
	 * Begins construction of a new row filter for this Facet.
	 * 
	 * @param field
	 *            the name of the field on which to filter.
	 * @return A partial representation of the new row filter.
	 */
	public FacetBuilder field(String field) {
		return new FacetBuilder(this, field);
	}

	public Facet near(String text, int meters) {
		Coord coord = new DataScienceToolkit().streetToCoord(text);
		if (coord != null) {
			return within(new Circle(coord, meters));
		} else {
			throw new FactualApiException(
					"Could not locate place based on text: '" + text + "'");
		}
	}

	/**
	 * Adds a filter so that results can only be (roughly) within the specified
	 * geographic circle.
	 * 
	 * @param circle
	 *            The circle within which to bound the results.
	 * @return this Facet.
	 */
	public Facet within(Circle circle) {
		queryParams.setParam("geo", circle);
		return this;
	}

	/**
	 * Used to nest AND'ed predicates.
	 */
	public Facet and(Facet... queries) {
		queryParams.popFilters("$and", queries);
		return this;
	}

	/**
	 * Used to nest OR'ed predicates.
	 */
	public Facet or(Facet... queries) {
		queryParams.popFilters("$or", queries);
		return this;
	}

	/**
	 * Adds <tt>filter</tt> to this Facet.
	 */
	public void add(Filter filter) {
		queryParams.add(filter);
	}

	/**
	 * The response will include a count of the total number of rows in the
	 * table that conform to the request based on included filters. This will
	 * increase the time required to return a response. The default behavior is
	 * to NOT include a row count.
	 * 
	 * @return this Facet, marked to return total row count when run.
	 */
	public Facet includeRowCount() {
		return includeRowCount(true);
	}

	/**
	 * When true, the response will include a count of the total number of rows
	 * in the table that conform to the request based on included filters.
	 * Requesting the row count will increase the time required to return a
	 * response. The default behavior is to NOT include a row count.
	 * 
	 * @param includeRowCount
	 *            true if you want the results to include a count of the total
	 *            number of rows in the table that conform to the request based
	 *            on included filters.
	 * @return this Facet.
	 */
	public Facet includeRowCount(boolean includeRowCount) {
		this.includeRowCount = includeRowCount;
		return this;
	}

	@Override
	public List<Filter> getFilterList() {
		return queryParams.getFilterList();
	}

}
