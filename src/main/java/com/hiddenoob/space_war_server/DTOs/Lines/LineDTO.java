package com.hiddenoob.space_war_server.DTOs.Lines;

import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.space_war_server.DTOs.DTO;
import com.hiddenoob.space_war_server.DTOs.Vector2DTO;

public record LineDTO(Vector2DTO a, Vector2DTO b) implements DTO {
    public LineDTO(Line line) {
        this(line.getA().toDTO(), line.getB().toDTO());
    }
}
