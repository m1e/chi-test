package com.chisw.microservices.nodes.persistence.jpa.utils;

import com.chisw.microservices.nodes.persistence.jpa.function.Functions;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

/**
 * JPA Helper for using ltree functions in {@link javax.persistence.criteria.CriteriaBuilder}.
 *
 */
public final class NodeExpressionUtils {

	private NodeExpressionUtils() {
	}

	public static Expression<Boolean> ltreeIsDescendant(CriteriaBuilder cb, Path<String> field, String value) {
		return cb.function(Functions.LTREE_IS_DESCENDANT, Boolean.class, field, cb.literal(value));
	}

	public static Expression<Boolean> ltreePathIsRoot(CriteriaBuilder cb, Path<String> field) {
		return cb.function(Functions.LTREE_PATH_IS_ROOT, Boolean.class, field);
	}

	public static Expression<Boolean> ltreeIsDescendant(CriteriaBuilder cb, Path<String> field, Path<String> secondField) {
		return cb.function(Functions.LTREE_IS_DESCENDANT, Boolean.class, field, secondField);
	}

	public static Expression<Boolean> ltreeIsAncestor(CriteriaBuilder cb, Path<String> field, Path<String> secondField) {
		return cb.function(Functions.LTREE_IS_ANCESTOR, Boolean.class, field, secondField);
	}

	public static Expression<Boolean> ltreeIsAncestor(CriteriaBuilder cb, Path<String> field, String value) {
		return cb.function(Functions.LTREE_IS_ANCESTOR, Boolean.class, field, cb.literal(value));
	}

	public static Expression<Boolean> ltreeMatch(CriteriaBuilder cb, Path<String> field, String pattern) {
		return cb.function(Functions.LTREE_LQUERY_MATCH, Boolean.class, field, cb.literal(pattern));
	}
}
