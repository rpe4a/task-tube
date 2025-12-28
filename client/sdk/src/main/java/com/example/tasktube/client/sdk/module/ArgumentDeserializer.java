package com.example.tasktube.client.sdk.module;

import com.example.tasktube.client.sdk.task.argument.Argument;
import com.example.tasktube.client.sdk.task.argument.ConstantArgument;
import com.example.tasktube.client.sdk.task.argument.ListArgument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ArgumentDeserializer extends JsonDeserializer<Argument> {
    public static final String ARGUMENT_TYPE = "type";

    @Override
    public Argument deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException {

        final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        final JsonNode node = mapper.readTree(parser);

        final Argument.ArgumentType type = Argument.ArgumentType.valueOf(node.get(ARGUMENT_TYPE).asText());

        return switch (type) {
            case CONSTANT -> mapper.treeToValue(node, ConstantArgument.class);
            case LIST -> mapper.treeToValue(node, ListArgument.class);
        };
    }
}
