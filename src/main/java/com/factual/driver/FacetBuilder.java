package com.factual.driver;

/**
 * Provides fluent interface to specifying row filter predicate logic on a Facet.
 * 
 * @author brandon
 */
public class FacetBuilder extends FilterBuilder<Facet> {
	public FacetBuilder(Facet query, String fieldName) {
		super(query, fieldName);
	}

	@Override
	protected Facet addFilter(String op, Object arg) {
		query.add(new FieldFilter(op, fieldName, arg));
		return query;
	}
}
