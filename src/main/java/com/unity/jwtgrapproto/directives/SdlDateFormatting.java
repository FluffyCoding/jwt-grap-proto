package com.unity.jwtgrapproto.directives;

import graphql.Scalars;
import graphql.schema.DataFetcherFactories;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SdlDateFormatting implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {

        var fieldDefinition = environment.getFieldDefinition();
        var parentType = environment.getFieldsContainer();

        //
        // DataFetcherFactories.wrapDataFetcher is a helper to wrap data fetchers so that CompletionStage is handled correctly
        // along with POJOs
        //
        var originalFetcher = environment.getCodeRegistry().getDataFetcher(parentType, fieldDefinition);
        var dataFetcher = DataFetcherFactories.wrapDataFetcher(originalFetcher, ((dataFetchingEnvironment, value) -> {
            DateTimeFormatter dateTimeFormatter = buildFormatter(dataFetchingEnvironment.getArgument("format"));
            if (value instanceof LocalDateTime) {
                return dateTimeFormatter.format((LocalDateTime) value);
            }
            return value;
        }));

        //
        // This will extend the field by adding a new "format" argument to it for the date formatting
        // which allows clients to opt into that as well as wrapping the base data fetcher so it
        // performs the formatting over top of the base values.
        //
        FieldCoordinates coordinates = FieldCoordinates.coordinates(parentType, fieldDefinition);
        environment.getCodeRegistry().dataFetcher(coordinates, dataFetcher);

        return fieldDefinition.transform(builder -> builder
                .argument(GraphQLArgument
                        .newArgument()
                        .name("format")
                        .type(Scalars.GraphQLString)
                        .defaultValueProgrammatic("dd-MM-YYYY")
                )
        );
    }


    private DateTimeFormatter buildFormatter(String format) {
        String dtFormat = format != null ? format : "dd-MM-YYYY";
        return DateTimeFormatter.ofPattern(dtFormat);
    }
}

