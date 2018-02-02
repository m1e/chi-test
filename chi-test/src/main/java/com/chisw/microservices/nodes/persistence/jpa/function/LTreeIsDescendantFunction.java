package com.chisw.microservices.nodes.persistence.jpa.function;

import org.hibernate.QueryException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import java.util.List;

import static java.lang.String.format;

/**
 * Hibernate support for {@code ltree ~ ltree} operator
 * (@link https://www.postgresql.org/docs/9.1/static/ltree.html)
 **/
public final class LTreeIsDescendantFunction extends LTreeFunction {
	private static final String FORMAT = "%s ~ %s::lquery";

	@Override
	public String render(Type firstArgumentType, @SuppressWarnings("rawtypes") List args,
			SessionFactoryImplementor factory) throws QueryException {
		this.prepare(args);
		String firstParam = this.prepareParam(this.getFirstParam());
		String secondParam = this.prepareParam(this.getSecondParam());
		return format(FORMAT, firstParam, secondParam);
	}
}
