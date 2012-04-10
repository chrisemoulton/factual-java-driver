package com.factual.driver;


public class ResolveQuery {
	
	/**
	 * Values parameter field
	 */
	private static final String VALUES = "values";
	
	/**
	 * Holds all parameters for this ResolveQuery.
	 */
	private final Parameters queryParams = new Parameters();

	public ResolveQuery add(String key, Object val) {
		queryParams.setJsonMapParam(VALUES, key, val);
		return this;
	}
	
	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

}
