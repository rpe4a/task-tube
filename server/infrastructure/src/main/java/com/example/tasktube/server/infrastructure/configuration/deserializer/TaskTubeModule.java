package com.example.tasktube.server.infrastructure.configuration.deserializer;

import com.example.tasktube.server.domain.values.slot.Slot;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class TaskTubeModule extends SimpleModule {
    @Override
    public void setupModule(final SetupContext context) {
        super.setupModule(context);
        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Slot.class, new SlotDeserializer());
        context.addDeserializers(deserializers);
    }
}
