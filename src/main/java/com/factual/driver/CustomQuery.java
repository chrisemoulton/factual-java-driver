package com.factual.driver;

/**
 * Represents a Factual custom query.  Use CustomQuery to build queries with a custom path and custom parameters, primarily for 
 * API calls not yet supported using other convenience classes.
 * @author brandon
 *
 */
public class CustomQuery {
	/**
	 * Holds all parameters for this CustomQuery.
	 */
	private final Parameters queryParams = new Parameters();

	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

	public CustomQuery addJsonParam(String key, Object value) {
		queryParams.setJsonParam(key, value);
		return this;
	}

	public CustomQuery addParam(String key, Object value) {
		queryParams.setParam(key, value);
		return this;
	}
	
}
