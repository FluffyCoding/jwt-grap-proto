//package com.unity.jwtgrapproto.exceptions;
//
//import graphql.GraphQLError;
//import graphql.GraphqlErrorBuilder;
//import graphql.schema.DataFetchingEnvironment;
//import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
//import org.springframework.graphql.execution.ErrorType;
//import org.springframework.stereotype.Component;
//
//import javax.validation.ConstraintViolationException;
//import java.util.Collections;
//import java.util.Map;
//
//@Component
//public class AbsoluteGraphQLError extends DataFetcherExceptionResolverAdapter {
//
//
//    @Override
//    public GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
//        if (ex instanceof SimpleGraphQLException simpleGraphQLException) {
//            return GraphqlErrorBuilder.newError(env)
//                    .message(simpleGraphQLException.getMessage())
//                    .errorType(simpleGraphQLException.getErrorType())
//                    .extensions(Collections.singletonMap("Error Code", 404))
//                    .build();
//        }
//        if (ex instanceof ConstraintViolationException exception) {
//            return GraphqlErrorBuilder.newError(env)
//                    .message(exception.getMessage())
//                    .errorType(ErrorType.BAD_REQUEST)
//                    .extensions(Map.of("Cause", exception.getCause(),
//                            "ErrorCode", 402,
//                            "Validations", exception.getConstraintViolations()))
//                    .build();
//        }
//
//
//
//
//        return null;
//    }
//
//}