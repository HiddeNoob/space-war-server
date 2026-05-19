package com.hiddenoob.space_war_server.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DTO {

    static final ObjectMapper objectMapper = new ObjectMapper();

    public default String serialize() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }
}
