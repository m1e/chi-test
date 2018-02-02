package com.chisw.microservices.nodes.persistence.jpa.dialect;


import com.chisw.microservices.nodes.persistence.jpa.function.Functions;
import com.chisw.microservices.nodes.persistence.jpa.function.LTreeIsAncestorFunction;
import com.chisw.microservices.nodes.persistence.jpa.function.LTreeIsDescendantFunction;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;
import org.springframework.stereotype.Component;


/*
 * Extends standard PostgreSQL9Dialect to support ltree (@link https://www.postgresql.org/docs/9.1/static/ltree.html)
 * and related operators
 */
@Component
public class CustomPostgresSQL9Dialect extends PostgreSQL9Dialect {

    public CustomPostgresSQL9Dialect() {

        super();
        registerFunction(Functions.LTREE_IS_ANCESTOR, new LTreeIsAncestorFunction());
        registerFunction(Functions.LTREE_IS_DESCENDANT, new LTreeIsDescendantFunction());
        registerFunction(Functions.LTREE_LQUERY_MATCH, new SQLFunctionTemplate(
                new BooleanType(),
                "(?1 ~ ?2::lquery)"
        ));
        registerFunction(Functions.LQUERY_LTREE_MATCH, new SQLFunctionTemplate(
                new BooleanType(),
                "(?1::lquery ~ ?2)"
        ));

        registerFunction(Functions.LTREE_LXTQUERY_MATCH, new SQLFunctionTemplate(
                new BooleanType(),
                "(?1 @ ?2::ltxtquery)"
        ));
        registerFunction(Functions.LXTQUERY_LTREE_MATCH, new SQLFunctionTemplate(
                new BooleanType(),
                "(?1::ltxtquery @ ?2)"
        ));

        registerFunction("ltree_root", new SQLFunctionTemplate(
                new BooleanType(),
                "(?1::ltree = path)"
        ));
    }
}
