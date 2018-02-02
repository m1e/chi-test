package com.chisw.microservices.nodes.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Allow the web to return a 409 if root already exists
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class RootAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RootAlreadyExistsException() {

        super("Root already exists");
    }
}
