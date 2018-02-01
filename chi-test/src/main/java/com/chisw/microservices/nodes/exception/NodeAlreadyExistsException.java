package com.chisw.microservices.nodes.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

/**
 * Allow the web to return a 409 if node already exists
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class NodeAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NodeAlreadyExistsException(String nodeId) {
        super(format("Node with such id %s already exists", nodeId));
    }
}
