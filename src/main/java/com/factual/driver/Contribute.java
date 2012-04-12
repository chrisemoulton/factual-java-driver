package com.factual.driver;

import java.util.Map;

/**
 * Represents an add or update contribution to a Factual row.
 * 
 * @author brandon
 */
public class Contribute {

	/**
	 * Holds all parameters for this Contribute.
	 */
	private final Parameters queryParams = new Parameters();

	/**
	 * Constructor for contribute.
	 */
	public Contribute() {
	}

	/**
	 * Constructor for a contribute with values initialized as key value pairs in mapping.
	 * 
	 * @param values values this contribute is initialized with
	 */
	public Contribute(Map<String, Object> values) {
		for (String key : values.keySet())
			setValue(key, values.get(key));
	}

	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

	/**
	 * Set the value for a single field in this contribute request.  
	 * Added to a JSON hash of field names and values to be added to a Factual table.
	 * @param field the field name
	 * @param value the value for the specified field
	 * @return this Contribute
	 */
	public Contribute setValue(String field, Object value) {
		queryParams.setJsonMapParam(Constants.CONTRIBUTE_VALUES, field, value);
		return this;
	}

	/**
	 * Set the value to null for a single field in this contribute request.
	 * @param field the field to set as empty, or null.
	 * @return this Contribute
	 */
	public Contribute removeValue(String field) {
		setValue(field, null);
		return this;
	}

	protected Map<String, String> toParamMap() {
		return queryParams.toParamMap();
	}

}
