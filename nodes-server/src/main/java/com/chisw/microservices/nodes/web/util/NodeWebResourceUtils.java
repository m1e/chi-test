package com.chisw.microservices.nodes.web.util;


import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.web.controller.NodeController;
import com.chisw.microservices.nodes.web.dto.NodeDto;
import com.chisw.microservices.nodes.web.dto.NodeResource;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

final public class NodeWebResourceUtils {

    public static NodeResource toResourceWithSelfLink(Node node) {

        return toResourceWithLink(node, selfLink(node.getId()));
    }

    public static Link selfLink(String id) {

        return linkTo(methodOn(NodeController.class).node(id)).withSelfRel();
    }

    public static NodeResource toResourceWithLink(Node node, Link link) {

        NodeResource nodeResource = new NodeResource();
        nodeResource.setNode(toDto(node));
        nodeResource.add(link);
        return nodeResource;
    }


    private static NodeDto toDto(Node node) {

        NodeDto dto = new NodeDto();
        dto.setId(node.getId());
        dto.setPath(node.getPath());
        return dto;
    }

}
