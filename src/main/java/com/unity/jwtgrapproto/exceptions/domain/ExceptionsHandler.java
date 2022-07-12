package com.unity.jwtgrapproto.exceptions.domain;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionsHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

        System.out.println(ex.getMessage());


        if (ex instanceof ConstraintViolationException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(Collections.singletonMap("Error code", 402))
                    .build();
        }
        if (ex instanceof EntityExistsException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(Collections.singletonMap("Error code", 404))
                    .build();
        }
        return null;
    }


}
