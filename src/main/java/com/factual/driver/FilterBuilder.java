package com.factual.driver;

import java.util.List;

import com.google.common.base.Joiner;

/**
 * Provides fluent interface to specifying row filter predicate logic.
 * 
 * @author brandon
 */
public abstract class FilterBuilder<T> {
	protected final T query;
	protected final String fieldName;

	/**
	 * Constructor. Specifies the name of the field for which to build filter
	 * logic. Instance methods are used to specify the desired logic.
	 */
	public FilterBuilder(T query, String fieldName) {
		this.query = query;
		this.fieldName = fieldName;
	}

	/**
	 * Specifies a full text search.
	 * 
	 * @param arg
	 *            the term(s) for which to full text search against.
	 * @return the represented query, with the specified full text search added
	 *         in.
	 */
	public T search(Object arg) {
		return addFilter("$search", arg);
	}

	public T equal(Object arg) {
		return addFilter("$eq", arg);
	}

	public T notEqual(Object arg) {
		return addFilter("$neq", arg);
	}

	public T in(List<Object> args) {
		return addFilter("$in", Joiner.on(",").join(args));
	}

	public T in(Object... args) {
		return addFilter("$in", args);
	}

	public T notIn(List<Object> args) {
		return addFilter("$nin", Joiner.on(",").join(args));
	}

	public T notIn(Object... args) {
		return addFilter("$nin", Joiner.on(",").join(args));
	}

	public T beginsWith(String arg) {
		return addFilter("$bw", arg);
	}

	public T notBeginsWith(String arg) {
		return addFilter("$nbw", arg);
	}

	public T beginsWithAny(Object... args) {
		return addFilter("$bwin", Joiner.on(",").join(args));
	}

	public T notBeginsWithAny(Object... args) {
		return addFilter("$nbwin", Joiner.on(",").join(args));
	}

	public T blank() {
		return addFilter("$blank", true);
	}

	public T notBlank() {
		return addFilter("$blank", false);
	}

	public T greaterThan(Object arg) {
		return addFilter("$gt", arg);
	}

	public T greaterThanOrEqual(Object arg) {
		return addFilter("$gte", arg);
	}

	public T lessThan(Object arg) {
		return addFilter("$lt", arg);
	}

	public T lessThanOrEqual(Object arg) {
		return addFilter("$lte", arg);
	}

	protected abstract T addFilter(String op, Object arg);
}
