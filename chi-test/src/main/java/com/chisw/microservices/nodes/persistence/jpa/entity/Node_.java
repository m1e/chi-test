package com.chisw.microservices.nodes.persistence.jpa.entity;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/*
This class is needed for {link #com.chisw.microservices.nodes.persistence.jpa.specification.NodeSpecifications}
 */
@StaticMetamodel(Node.class)
public abstract class Node_ {

    public static volatile SingularAttribute<Node, String> id;
    public static volatile SingularAttribute<Node, String> path;

}
