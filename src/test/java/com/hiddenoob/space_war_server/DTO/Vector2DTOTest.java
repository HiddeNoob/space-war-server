package com.hiddenoob.space_war_server.DTO;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.DTOs.Vector2DTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2DTOTest {

    @Test
    void testVector2DTOConversion() {
        // Vector2 sınıfı x,y alanlarını public sunduğu için doğrudan nesne ile test ediyoruz
        Vector2 vector = new Vector2(10.5, -20.2);
        Vector2DTO dto = new Vector2DTO(vector);

        assertEquals(10.5, dto.x1(), "x1 value should match Vector2.x");
        assertEquals(-20.2, dto.x2(), "x2 value should match Vector2.y");
    }

    @Test
    void testVector2DTOSerialization() {
        Vector2DTO dto = new Vector2DTO(5.0, 7.5);
        String json = assertDoesNotThrow(dto::serialize);
        
        assertTrue(json.contains("\"x1\":5.0"));
        assertTrue(json.contains("\"x2\":7.5"));
    }
}
