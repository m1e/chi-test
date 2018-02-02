package com.chisw.microservices.nodes.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Allow the web to return a 204
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoNodeContentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoNodeContentException() {
        super();
    }
}
