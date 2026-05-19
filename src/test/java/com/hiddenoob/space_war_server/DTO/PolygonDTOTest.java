package com.hiddenoob.space_war_server.DTO;

import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.DTOs.Polygons.PolygonDTO;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PolygonDTOTest {

    @Test
    @SuppressWarnings("unchecked")
    void testPolygonDTOConversion() {
        Polygon<Line> mockPolygon = Mockito.mock(Polygon.class);
        Line line = new Line(new Vector2(0, 0), new Vector2(1, 1));
        ArrayList<Line> lines = new ArrayList<>(List.of(line));
        
        when(mockPolygon.getLines()).thenReturn(lines);

        PolygonDTO dto = new PolygonDTO(mockPolygon);

        assertFalse(dto.lines().isEmpty());
        assertEquals(1, dto.lines().size());
        assertDoesNotThrow(dto::serialize);
    }
}