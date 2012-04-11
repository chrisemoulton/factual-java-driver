package com.factual.driver;


public class ResolveQuery {
	
	/**
	 * Holds all parameters for this ResolveQuery.
	 */
	private final Parameters queryParams = new Parameters();

	public ResolveQuery add(String key, Object val) {
		queryParams.setJsonMapParam(Constants.RESOLVE_VALUES, key, val);
		return this;
	}
	
	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

}
