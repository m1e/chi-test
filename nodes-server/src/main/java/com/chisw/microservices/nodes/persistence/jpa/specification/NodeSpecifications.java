package com.chisw.microservices.nodes.persistence.jpa.specification;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.persistence.jpa.function.Functions;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import static com.chisw.microservices.nodes.persistence.jpa.entity.Node_.id;
import static com.chisw.microservices.nodes.persistence.jpa.entity.Node_.path;
import static com.chisw.microservices.nodes.persistence.jpa.utils.NodeExpressionUtils.ltreeIsDescendant;
import static com.chisw.microservices.nodes.persistence.jpa.utils.NodeExpressionUtils.ltreeIsRoot;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Represents a bunch of Spring JPA specifications that allow to search for
 * ancestors and descendants of a given {@link Node}
 */
public final class NodeSpecifications {

    private NodeSpecifications() {

    }

    public static Specification<Node> nodeDescendants(
            final String value
    ) {

        checkNotNull(value);

        return (root, query, cb) -> cb.isTrue(ltreeIsDescendant(cb, root.get(path), value));
    }

    public static Specification<Node> root() {

        return (root, query, cb) -> cb.isTrue(ltreeIsRoot(cb, root.get(id), path.getName()));
    }

    public static Specification<Node> nodeAncestors(
            final String value
    ) {

        checkNotNull(value);

        return (root, query, cb) -> {
            Subquery<Node> relationshipSubQuery = query.subquery(Node.class);
            Root<Node> relationshipSubQueryRoot = relationshipSubQuery.from(Node.class);
            relationshipSubQuery.select(relationshipSubQueryRoot.get(path.getName()));
            relationshipSubQuery.where(cb.equal(relationshipSubQueryRoot.get(id.getName()), value));
            return cb.isTrue(cb.function(
                    Functions.LTREE_IS_ANCESTOR,
                    Boolean.class,
                    root.get(path.getName()),
                    relationshipSubQuery
            ));
        };
    }


}
