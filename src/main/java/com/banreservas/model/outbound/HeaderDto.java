package com.banreservas.model.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record HeaderDto(
        @JsonProperty("ResponseCode") int ResponseCode,
        @JsonProperty("ResponseMessage") String ResponseMessage) {
}
