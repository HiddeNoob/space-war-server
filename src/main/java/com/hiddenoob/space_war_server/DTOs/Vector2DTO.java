package com.hiddenoob.space_war_server.DTOs;

import com.hiddenoob.Math.Vector2;

public record Vector2DTO(double x1, double x2) implements DTO {
    public Vector2DTO(Vector2 v){
        this(v.x, v.y);
    }
}
