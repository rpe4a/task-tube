package com.example.tasktube.server.infrastructure.configuration.deserializer;

import com.example.tasktube.server.domain.values.slot.ConstantSlot;
import com.example.tasktube.server.domain.values.slot.Slot;
import com.example.tasktube.server.domain.values.slot.ListSlot;
import com.example.tasktube.server.domain.values.slot.TaskSlot;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SlotDeserializer extends JsonDeserializer<Slot> {
    public static final String SLOT_TYPE = "type";

    @Override
    public Slot deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException {

        final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        final JsonNode node = mapper.readTree(parser);

        final Slot.SlotType type = Slot.SlotType.valueOf(node.get(SLOT_TYPE).asText());

        return switch (type) {
            case CONSTANT -> mapper.treeToValue(node, ConstantSlot.class);
            case TASK -> mapper.treeToValue(node, TaskSlot.class);
            case LIST -> mapper.treeToValue(node, ListSlot.class);
        };
    }
}
