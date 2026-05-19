package com.hiddenoob.space_war_server.DTOs.Polygons;

import java.util.List;

import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.space_war_server.DTOs.DTO;
import com.hiddenoob.space_war_server.DTOs.Lines.LineDTO;

public record PolygonDTO(List<LineDTO> lines) implements DTO {
    public PolygonDTO(Polygon<?> p) {
        this(p.getLines().stream().map(l -> l.toDTO()).toList());
    }
}
