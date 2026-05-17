package com.hiddenoob.space_war_server.server;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {

    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonProperty("datetime")
    @JsonSerialize(using = ToStringSerializer.class)
    private Instant datetime;

    @JsonProperty("message")
    private String message;

    @JsonProperty("sender")
    private Object sender;

    public Notification(Object sender, String message){
        this.sender = sender;
        this.message = message;
        this.datetime = Instant.now();
    }

    public Instant getDatetime() { return datetime; }
    public String getMessage() { return message; }
    public Object getSender() { return sender; }

    public byte[] toByteArray(){
        try {
            return mapper.writeValueAsBytes(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Notification", e);
        }
    }
}
