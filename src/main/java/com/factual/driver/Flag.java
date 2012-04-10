package com.factual.driver;

import java.util.Map;

/**
 * Represents a request to flag a Factual row with certain problems.
 * 
 * @author brandon
 */
public class Flag {
	/**
	 * Constructor.  Create a request to flag information on a Factual row.
	 * 
	 * @param type the type of flag that this represents
	 */
	public Flag(Type type) {
		queryParams.setParam("problem", type.toApiString());
	}

	/**
	 * Create a flag for a duplicate row.
	 * @return new Flag instance.
	 */
	public static Flag duplicate() {
		return new Flag(Type.DUPLICATE);
	}

	/**
	 * Create a flag for an inaccurate row.
	 * @return new Flag instance.
	 */
	public static Flag inaccurate() {
		return new Flag(Type.INACCURATE);
	}

	/**
	 * Create a flag for an inappropriate row.
	 * @return new Flag instance.
	 */
	public static Flag inappropriate() {
		return new Flag(Type.INAPPROPRIATE);
	}

	/**
	 * Create a flag for a non-existent row.
	 * @return new Flag instance.
	 */
	public static Flag nonexistent() {
		return new Flag(Type.NONEXISTENT);
	}

	/**
	 * Create a flag that a row is spam.
	 * @return new Flag instance.
	 */
	public static Flag spam() {
		return new Flag(Type.SPAM);
	}

	/**
	 * Create a flag for this row.
	 * @return new Flag instance
	 */
	public static Flag other() {
		return new Flag(Type.OTHER);
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
		 * Convert flag type to an api string
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

	public Flag addJsonParam(String key, Object value) {
		queryParams.setJsonParam(key, value);
		return this;
	}

	public Flag addParam(String key, Object value) {
		queryParams.setParam(key, value);
		return this;
	}

	protected Map<String, Object> toMap() {
		return queryParams.toMap();
	}
}
