package com.unity.jwtgrapproto.exceptions;

import lombok.Getter;
import org.springframework.graphql.execution.ErrorType;

public class SimpleGraphQLException extends RuntimeException {

    @Getter
    private final ErrorType errorType;

    public SimpleGraphQLException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
}
