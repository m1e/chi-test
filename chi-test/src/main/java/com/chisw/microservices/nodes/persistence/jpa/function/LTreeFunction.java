package com.chisw.microservices.nodes.persistence.jpa.function;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Base class function to support ltree (@link https://www.postgresql.org/docs/9.1/static/ltree.html)
 */
public abstract class LTreeFunction implements SQLFunction {

	private String firstParam;
	private String secondParam;

	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return false;
	}

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
		return new BooleanType();
	}

	public String getFirstParam() {
		return this.firstParam;
	}

	public String getSecondParam() {
		return this.secondParam;
	}

	protected void prepare(List<?> args) throws QueryException {

		checkArgument(args.size() == 2, "The function accepts 2 arguments");

		this.firstParam = (String) args.get(0);
		this.secondParam = (String) args.get(1);

		if (isEmpty(firstParam) || isEmpty(secondParam)) {
			throw new IllegalArgumentException("Function arguments must not be empty");
		}

	}

	protected String prepareParam(String param) {
		StringBuilder builder = new StringBuilder();
		// TODO: MAJOR | Other possible input params?
		if (param.equals("!")) {
			// If this is some text value, convert to ltree
			builder.append("text2ltree(");
			builder.append(param);
			builder.append(")");
		} else {
			builder.append(param);
		}
		return builder.toString();
	}

}
