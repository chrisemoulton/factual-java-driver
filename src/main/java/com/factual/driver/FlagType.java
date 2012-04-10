package com.factual.driver;	/**

/**
 * The types of problems that can be reported for a flag request
 * @author brandon
 */
public enum FlagType {
	DUPLICATE("duplicate"), 
	INACCURATE("inaccurate"),
	INAPPROPRIATE("inappropriate"),
	NONEXISTENT("nonexistent"),
	SPAM("spam"), 
	OTHER("other");
	private String apiString;
	FlagType(String apiString) {
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
