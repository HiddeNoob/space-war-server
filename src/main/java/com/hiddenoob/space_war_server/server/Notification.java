package com.hiddenoob.space_war_server.server;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {

    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonSerialize(using = ToStringSerializer.class)
    private Instant datetime;

    private String message;

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
