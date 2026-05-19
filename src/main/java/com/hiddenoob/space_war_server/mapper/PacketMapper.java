package com.hiddenoob.space_war_server.mapper;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.space_war_server.packets.*;

import java.util.List;

public class PacketMapper {

    public static Vector2Packet toPacket(Vector2 vector) {
        return new Vector2Packet((float)vector.x, (float)vector.y);
    }

    public static LinePacket toPacket(Line line) {
        Vector2Packet packetA = toPacket(line.getA());
        Vector2Packet packetB = toPacket(line.getB());
        
        return new LinePacket(packetA, packetB);
    }

    public static PolygonPacket toPacket(Polygon<?> polygon) {
        // Polygon içindeki çizgileri LinePacket listesine çeviriyoruz
        List<LinePacket> linePackets = polygon.getLines().stream()
                .map(PacketMapper::toPacket) // Yukarıdaki metodu çağırıyor
                .toList();

        return new PolygonPacket(linePackets);
    }
}