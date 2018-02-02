package com.chisw.microservices.nodes.service;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NodeService {

    Page<Node> getDescendants(String id, Pageable pageable);

    Page<Node> getAncestors(String id, Pageable pageable);

    Node findOrCreate(String id, String parentId);

    Node update(String id, String newId);

    Node deleteBranch(String parentId);

    Node getById(String id);

}
