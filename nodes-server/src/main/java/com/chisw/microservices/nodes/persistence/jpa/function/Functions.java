package com.chisw.microservices.nodes.persistence.jpa.function;

/**
 * Supported ltree (@link https://www.postgresql.org/docs/9.1/static/ltree.html) functions shortcuts.
 *
 */
public interface Functions {
	/**
	 * Alternative to operation: {@code ltree <@ ltree}
	 */
	String LTREE_IS_DESCENDANT = "ltree_is_descendant";
	/**
	 * Alternative to operation: {@code ltree @> ltree}
	 */
	String LTREE_IS_ANCESTOR = "ltree_is_ancestor";
	/**
	 * Finds root of ltree based on path column
	 */
	String LTREE_PATH_IS_ROOT = "ltree_path_is_root";
	/**
	 * Alternative to operation: {@code ltree ~ lquery}
	 */
	String LTREE_LQUERY_MATCH = "ltree_ltree_lquery_match";
	/**
	 * Alternative to operation: {@code lquery ~ ltree}
	 */
	String LQUERY_LTREE_MATCH = "ltree_lquery_ltree_match";
	/**
	 * Alternative to operation: {@code lquery ~ ltree}
	 */
	String LTREE_LXTQUERY_MATCH = "ltree_ltree_ltxtquery_match";
	/**
	 * Alternative to operation: {@code lquery ~ ltree}
	 */
	String LXTQUERY_LTREE_MATCH = "ltree_ltxtquery_ltree_match";


}
