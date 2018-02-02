package com.chisw.microservices.nodes.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class NodeDto {

    private String id;
    private String parentId;
    private String path;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getParentId() {

        return parentId;
    }

    public void setParentId(String parentId) {

        this.parentId = parentId;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    @Override
    public String toString() {

        return "NodeDto{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
