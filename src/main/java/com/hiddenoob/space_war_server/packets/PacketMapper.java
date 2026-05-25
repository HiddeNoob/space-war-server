package com.hiddenoob.space_war_server.packets;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.gameObjects.Asteroid;
import com.hiddenoob.space_war_server.packets.action.ActionPacket;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

public class PacketMapper {

    // =========================================================================
    // Decode
    // =========================================================================

    public static Packet fromBuffer(ByteBuffer buffer) {
        byte typeId = buffer.get();
        PacketType type = resolvePacketType(typeId);
        return switch (type) {
            case VECTOR2 -> Vector2Packet.decode(buffer);
            case LINE -> LinePacket.decode(buffer);
            case BREAKABLE_LINE -> BreakableLinePacket.decode(buffer);
            case STRING -> StringPacket.decode(buffer);
            case NOTIFICATION -> NotificationPacket.decode(buffer);
            case ARRAY -> ListPacket.decode(buffer);
            case UNIFORM_ARRAY -> UniformListPacket.decode(buffer);
            case POLYGON -> PolygonPacket.decode(buffer);
            case ACTION -> ActionPacket.decode(buffer);
            case WORLD_STATE -> WorldStatePacket.decode(buffer);
            case PLAYER_STATE -> PlayerStatePacket.decode(buffer);
            case ENTITY_STATE -> EntityStatePacket.decode(buffer);
            default ->
                    throw new IllegalArgumentException("Unknown packet type: "
                            + type);
        };
    }


    public static PacketType resolvePacketType(byte id) {
        for (PacketType type : PacketType.values()) {
            if (type.getId() == id) return type;
        }
        return PacketType.UNKNOWN;
    }


    public static Vector2Packet toPacket(Vector2 vector) {
        return new Vector2Packet((float) vector.x, (float) vector.y);
    }

    public static EntityStatePacket toEntityStatePacket(Asteroid asteroid) {
        return new EntityStatePacket(
                asteroid.getId(),
                toPacket(asteroid.getPosition()),
                toPacket(asteroid.getPhysics().getVelocity()),
                (float) asteroid.getPhysics().getRotation(),
                toPacket(asteroid.getActualPolygon())
        );
    }

    public static LinePacket toPacket(Line line) {
        Vector2Packet packetA = toPacket(line.getStart());
        Vector2Packet packetB = toPacket(line.getEnd());
        return new LinePacket(packetA, packetB);
    }

    public static BreakableLinePacket toPacket(BreakableLine line) {
        Vector2Packet packetA = toPacket(line.getStart());
        Vector2Packet packetB = toPacket(line.getEnd());
        return new BreakableLinePacket(line.getHealth(), packetA, packetB);
    }

    public static <T extends Line> PolygonPacket toPacket(Polygon<T> polygon) {
        List<LinePacket> linePackets = polygon.getLines().stream()
                .map(PacketMapper::toPacket)
                .toList();
        ListPacket listPacket =
                new UniformListPacket<>(linePackets.toArray(new LinePacket[0]));

        return new PolygonPacket(listPacket);
    }

    public static StringPacket toPacket(String message) {
        return new StringPacket(message);
    }

    public static NotificationPacket toPacket(String sender, String message) {
        return new NotificationPacket(
                toPacket(sender),
                toPacket(message),
                toPacket(Instant.now().toString())
        );
    }


    public static UniformListPacket<Vector2Packet> toVector2PacketList(List<Vector2> vectors) {
        return toUniformListPacket(vectors, PacketMapper::toPacket,
                Vector2Packet.class);
    }

    public static UniformListPacket<LinePacket> toLinePacketList(List<Line> lines) {
        return toUniformListPacket(lines, PacketMapper::toPacket,
                LinePacket.class);
    }

    public static <T extends Line> ListPacket toPolygonPacketList(
            List<Polygon<T>> polygons) {
        List<PolygonPacket> packets = polygons.stream()
                .map(PacketMapper::toPacket)
                .toList();
        return new ListPacket(packets.toArray(new PolygonPacket[0]));
    }

    public static ListPacket toEntityStatePacketList(
            List<Asteroid> asteroids) {
        List<EntityStatePacket> packets = asteroids.stream()
                .map(PacketMapper::toEntityStatePacket)
                .toList();
        return new ListPacket(packets.toArray(new EntityStatePacket[0]));
    }

    public static ListPacket toStringPacketList(List<String> strings) {
        List<StringPacket> packets = strings.stream()
                .map(PacketMapper::toPacket)
                .toList();
        return new ListPacket(packets.toArray(new StringPacket[0]));
    }

    @SuppressWarnings("unchecked")
    private static <T, P extends Packet> UniformListPacket<P> toUniformListPacket(
            List<T> items, Function<T, P> mapper, Class<P> packetClass) {
        P[] packetArray = (P[]) Array.newInstance(packetClass, items.size());
        for (int i = 0; i < items.size(); i++)
            packetArray[i] = mapper.apply(items.get(i));
        return new UniformListPacket<>(packetArray);
    }

    public static Packet decodeBody(PacketType type, ByteBuffer buffer) {
        return switch (type) {
            case VECTOR2 -> Vector2Packet.decodeBody(buffer);
            case LINE -> LinePacket.decodeBody(buffer);
            case BREAKABLE_LINE -> BreakableLinePacket.decodeBody(buffer);
            default ->
                    throw new IllegalArgumentException(type + " has no body " +
                            "decoder");
        };
    }
}
