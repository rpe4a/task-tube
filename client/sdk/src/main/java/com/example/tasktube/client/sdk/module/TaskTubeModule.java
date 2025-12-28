package com.example.tasktube.client.sdk.module;

import com.example.tasktube.client.sdk.task.argument.Argument;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class TaskTubeModule extends SimpleModule {
    @Override
    public void setupModule(final SetupContext context) {
        super.setupModule(context);
        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Argument.class, new ArgumentDeserializer());
        context.addDeserializers(deserializers);
    }
}
