package com.chisw.microservices.nodes.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class NodeResource extends ResourceSupport {

    private NodeDto node;

    public NodeDto getNode() {

        return node;
    }

    public void setNode(NodeDto node) {

        this.node = node;
    }


}
