package com.hiddenoob.space_war_server.DTO;

import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.space_war_server.DTOs.Lines.LineDTO;
import com.hiddenoob.Math.Vector2;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LineDTOTest {

    @Test
    void testLineDTOConversion() {
        Line mockLine = Mockito.mock(Line.class);
        Vector2 p1 = new Vector2(1, 2);
        Vector2 p2 = new Vector2(3, 4);
        
        when(mockLine.getA()).thenReturn(p1);
        when(mockLine.getB()).thenReturn(p2);

        LineDTO dto = new LineDTO(mockLine);

        assertNotNull(dto.a());
        assertNotNull(dto.b());
        assertEquals(1.0, dto.a().x1());
        assertEquals(2.0, dto.a().x2());
        assertEquals(3.0, dto.b().x1());
        assertEquals(4.0, dto.b().x2());
        
        assertDoesNotThrow(dto::serialize);
    }
}