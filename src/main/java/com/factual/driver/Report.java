package com.factual.driver;

import java.util.Map;

/**
 * Represents a request to flag a Factual row with certain problems.
 * 
 * @author brandon
 */
public class Report {
	public Report(Type type) {
		queryParams.setParam("problem", type.toApiString());
	}

	public static Report duplicate() {
		return new Report(Type.DUPLICATE);
	}

	public static Report inaccurate() {
		return new Report(Type.INACCURATE);
	}

	public static Report inappropriate() {
		return new Report(Type.INAPPROPRIATE);
	}

	public static Report nonexistent() {
		return new Report(Type.NONEXISTENT);
	}

	public static Report spam() {
		return new Report(Type.SPAM);
	}

	public static Report other() {
		return new Report(Type.OTHER);
	}

	/**
	 * The types of problems that can be reported
	 * @author brandon
	 *
	 */
	public enum Type {
		DUPLICATE("duplicate"), 
		INACCURATE("inaccurate"),
		INAPPROPRIATE("inappropriate"),
		NONEXISTENT("nonexistent"),
		SPAM("spam"), 
		OTHER("other");
		private String apiString;
		Type(String apiString) {
			this.apiString = apiString;
		}
		/**
		 * Convert report type to an api string
		 * @return the api string
		 */
		public String toApiString() {
			return apiString;
		}
	}
	
	/**
	 * Holds all parameters for this Query.
	 */
	private final Parameters queryParams = new Parameters();

	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

	public Report addJsonParam(String key, Object value) {
		queryParams.setJsonParam(key, value);
		return this;
	}

	public Report addParam(String key, Object value) {
		queryParams.setParam(key, value);
		return this;
	}

	protected Map<String, Object> toMap() {
		return queryParams.toMap();
	}
}
