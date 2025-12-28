package com.example.tasktube.client.sandbox.tube.regress.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = RegressInterfaceImpl.class)
public interface IRegressInterface {

    String getValue();
}
