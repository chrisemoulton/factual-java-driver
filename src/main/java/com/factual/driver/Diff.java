package com.factual.driver;

public class Diff {

	public Diff before(long timestamp) {
		addParam("start-date", timestamp);
		return this;
	}

	public Diff after(long timestamp) {
		addParam("end-date", timestamp);
		return this;
	}
	
	/**
	 * Holds all parameters for this Query.
	 */
	private final Parameters queryParams = new Parameters();

	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

	public Diff addJsonParam(String key, Object value) {
		queryParams.setJsonParam(key, value);
		return this;
	}

	public Diff addParam(String key, Object value) {
		queryParams.setParam(key, value);
		return this;
	}
}
