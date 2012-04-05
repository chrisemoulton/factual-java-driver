package com.factual.driver;

/**
 * Interface for objects convertible to an object representation that can be serialized as json
 * @author brandon
 *
 */
public interface Convertible {
	/**
	 *  Construct an object representation that can be serialized as json
	 * 
	 * @return an object representation that can be serialized as json
	 */
	public Object toJsonObject();
}
