package com.hiddenoob.space_war_server.packets;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.Math.Polygons.Polygon;
import org.springframework.web.socket.WebSocketMessage;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.lang.reflect.Array;

public class PacketMapper {

    public static Vector2Packet toPacket(Vector2 vector) {
        return new Vector2Packet((float)vector.x, (float)vector.y);
    }

    public static LinePacket toPacket(Line line) {
        Vector2Packet packetA = toPacket(line.getA());
        Vector2Packet packetB = toPacket(line.getB());
        return new LinePacket(packetA, packetB);
    }

    public static BreakableLinePacket toPacket(BreakableLine line) {
        Vector2Packet packetA = toPacket(line.getA());
        Vector2Packet packetB = toPacket(line.getB());
        return new BreakableLinePacket(line.getHealth(),packetA, packetB);
    }

    public static <T extends Line> PolygonPacket toPacket(Polygon<T> polygon) {
        // Polygon içindeki çizgileri LinePacket listesine çeviriyoruz
        List<LinePacket> linePackets = polygon.getLines().stream()
                .map(PacketMapper::toPacket) // Yukarıdaki metodu çağırıyor
                .toList();
        ListPacket listPacket = new UniformListPacket<LinePacket>(linePackets.toArray(new LinePacket[0]));

        return new PolygonPacket(listPacket);
    }

    public static StringPacket toPacket(String message) {
        return new StringPacket(message);
    }

    public static NotificationPacket toPacket(String sender, String message) {
        return new NotificationPacket(toPacket(sender), toPacket(message), toPacket(Instant.now().toString()));
    }

    // Helper method for UniformListPacket
    private static <T, P extends Packet> UniformListPacket<P> toUniformListPacket(List<T> items, Function<T, P> mapper, Class<P> packetClass) {
        @SuppressWarnings("unchecked")
        P[] packetArray = (P[]) Array.newInstance(packetClass, items.size());
        for (int i = 0; i < items.size(); i++) {
            packetArray[i] = mapper.apply(items.get(i));
        }
        return new UniformListPacket<>(packetArray);
    }

    public static UniformListPacket<Vector2Packet> toVector2PacketList(List<Vector2> vectors) {
        return toUniformListPacket(vectors, PacketMapper::toPacket, Vector2Packet.class);
    }

    public static UniformListPacket<LinePacket> toLinePacketList(List<Line> lines) {
        return toUniformListPacket(lines, PacketMapper::toPacket, LinePacket.class);
    }

    public static <T extends Line> UniformListPacket<PolygonPacket> toPolygonPacketList(List<Polygon<T>> polygons) {
        return toUniformListPacket(polygons, PacketMapper::toPacket, PolygonPacket.class);
    }

    public static UniformListPacket<StringPacket> toStringPacketList(List<String> strings) {
        return toUniformListPacket(strings, PacketMapper::toPacket, StringPacket.class);
    }
}