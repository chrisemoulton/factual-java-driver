package com.factual.driver;

import java.util.Map;

/**
 * Represents metadata to be sent with suggest and report requests
 * @author brandon
 */
public class Metadata {
	
	private Parameters queryParams = new Parameters();

	/**
	 * Constructor.  Create metadata to associate with suggest and report requests
	 * @param username the name of the user associated with this request
	 */
	public Metadata(String username) {
		queryParams.setParam("user", username);
	}
	
	private Metadata(Parameters queryParams) {
		this.queryParams = queryParams;
	}

	/**
	 * The request will only be a test query and no actual data will be written
	 * @return new Metadata, marked as a debug request
	 */
	public Metadata debug() {
		return debug(true);
	}
	
	/**
	 * When true, the request will only be a test query and no actual data will be written.
	 * The default behavior is to NOT include debug.
	 * @param debug true if you want this to be a test query where no actual date is written
	 * @return new Metadata, marked with whether or not this is a debug request
	 */
	public Metadata debug(boolean debug) {
		Parameters params = queryParams.copy();
		params.setParam("debug", debug);
		return new Metadata(params);
	}

	/**
	 * Set a comment that will help to explain your corrections
	 * @param comment the comment that may help explain your corrections
	 * @return new Metadata, with a comment set
	 */
	public Metadata comment(String comment) {
		Parameters params = queryParams.copy();
		params.setParam("comment", comment);
		return new Metadata(params);
	}

	/**
	 * Set a reference to a URL, title, person, etc. that is the source of this data
	 * @param reference a reference to a URL, title, person, etc. that is the source of this data
	 * @return new Metadata, with a reference set
	 */
	public Metadata reference(String reference) {
		Parameters params = queryParams.copy();
		params.setParam("reference", reference);
		return new Metadata(params);
	}

	protected String toUrlQuery() {
		return queryParams.toUrlQuery(true);
	}

	public Map<String, Object> toMap() {
		return queryParams.toMap();
	}
	
}
