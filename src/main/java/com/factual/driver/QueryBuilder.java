package com.factual.driver;

/**
 * Provides fluent interface to specifying row filter predicate logic on a Query.
 * 
 * @author aaron
 */
public class QueryBuilder extends FilterBuilder<Query> {
	public QueryBuilder(Query query, String fieldName) {
		super(query, fieldName);
	}

	@Override
	protected Query addFilter(String op, Object arg) {
		query.add(new FieldFilter(op, fieldName, arg));
		return query;
	}
}
