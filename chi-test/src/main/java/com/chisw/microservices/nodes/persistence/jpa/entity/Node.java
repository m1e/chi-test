package com.chisw.microservices.nodes.persistence.jpa.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Persistent node entity with JPA markup.
 *
 */
@Entity
@Table(name = "nodes")
public class Node implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;


    @Column(name = "path", columnDefinition = "ltree")
    @Type(type = "com.chisw.microservices.nodes.persistence.jpa.type.LTreeType")
    private String path;


    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

}
