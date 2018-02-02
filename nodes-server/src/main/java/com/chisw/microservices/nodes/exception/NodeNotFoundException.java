package com.chisw.microservices.nodes.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * Allow the web to return a 404 if an node is not found by simply
 * throwing this exception. The @ResponseStatus causes Spring MVC to return a
 * 404 instead of the usual 500.
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NodeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NodeNotFoundException(String nodeId) {
		super("There is no node with id = " + nodeId);
	}
}
