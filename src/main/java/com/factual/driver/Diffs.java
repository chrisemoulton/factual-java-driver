package com.factual.driver;

/**
 * Represents a Factual Diffs query.
 * 
 * @author brandon
 *
 */
public class Diffs {

	/**
	 * Constructor.  Create a request to find diffs on a Factual table between two times.
	 * @param before the before time to create this diff against.
	 */
	public Diffs(long before) {
		before(before);
	}
	
	/**
	 * The before time to create this diff against.
	 * @param timestamp before time for this diff.
	 * @return this Diffs
	 */
	public Diffs before(long timestamp) {
		addParam("start-date", timestamp);
		return this;
	}

	/**
	 * The after time to create this diff against.
	 * @param timestamp after time for this diff.
	 * @return this Diffs
	 */
	public Diffs after(long timestamp) {
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

	/**
	 * Set a parameter and value pair for specifying url parameters, specifically those not yet available as convenience methods.
	 * @param key the field name of the parameter to add
	 * @param value the field value that will be serialized using value.toString()
	 * @return this Diffs
	 */
	private Diffs addParam(String key, Object value) {
		queryParams.setParam(key, value);
		return this;
	}
}
