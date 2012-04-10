package com.factual.driver;

import java.util.Map;

/**
 * Represents an add or update contribution to a Factual row.
 * 
 * @author brandon
 */
public class Suggest {

	private static final String VALUES = "values";
	
	/**
	 * Holds all parameters for this Suggest.
	 */
	private final Parameters queryParams = new Parameters();

	/**
	 * Constructor for suggest.
	 */
	public Suggest() {
	}

	/**
	 * Constructor for a suggest with values initialized as key value pairs in mapping.
	 * 
	 * @param values values this suggest is initialized with
	 */
	public Suggest(Map<String, Object> values) {
		for (String key : values.keySet())
			setValue(key, values.get(key));
	}

	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

	/**
	 * Set the value for a single field in this suggest request.  
	 * Added to a JSON hash of field names and values to be added to a Factual table.
	 * @param field the field name
	 * @param value the value for the specified field
	 * @return this Suggest
	 */
	public Suggest setValue(String field, Object value) {
		queryParams.setJsonMapParam(VALUES, field, value);
		return this;
	}

	/**
	 * Set the value to null for a single field in this suggest request.
	 * @param field the field to set as empty, or null.
	 * @return this Suggest
	 */
	public Suggest makeBlank(String field) {
		setValue(field, null);
		return this;
	}

	protected Map<String, Object> toMap() {
		return queryParams.toMap();
	}

}
