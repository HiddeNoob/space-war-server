package com.hiddenoob.space_war_server.packets.action;

import com.hiddenoob.space_war_server.packets.Packet;
import com.hiddenoob.space_war_server.packets.PacketType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ActionPacket extends Packet {

    private final List<Action> actions;

    /**
     * Verilen action'ları bit değerine göre sıralar.
     * Encode ve decode aynı sırayı kullandığından offset kayması olmaz:
     * FORCE(1) → ROTATION(2) → ATTACK(4) → BOOST(8)
     */
    public ActionPacket(Action... actions) {
        this.actions = Arrays.stream(actions)
                .sorted(Comparator.comparingInt(a -> a.getActionType().getBit()))
                .toList();
    }

    // ── Decode ───────────────────────────────────────────────────────────────

    public static ActionPacket decode(ByteBuffer buffer) {
        int mask = buffer.getInt(); // ActionMask
        buffer.getInt();            // bodySize

        List<Action> actions = new ArrayList<>();

        // Bit sırası encode ile aynı olmalı
        if ((mask & ActionType.FORCE.getBit()) != 0)
            actions.add(ForceAction.DECODER.decode(buffer));
        if ((mask & ActionType.ROTATION.getBit()) != 0)
            actions.add(RotationAction.DECODER.decode(buffer));
        if ((mask & ActionType.ATTACK.getBit()) != 0)
            actions.add(AttackAction.DECODER.decode(buffer));
        if ((mask & ActionType.BOOST.getBit()) != 0)
            actions.add(BoostAction.DECODER.decode(buffer));

        return new ActionPacket(actions.toArray(new Action[0]));
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    /**
     * Header: PacketType(1) + ActionMask(4) + BodySize(4) = 9 byte
     */
    @Override
    protected int getHeaderSize() {
        return 9;
    }

    @Override
    protected void writeToPacketHeader(ByteBuffer buffer) {
        buffer.put(getPacketType().getId()); // PacketType(1)
        buffer.putInt(computeMask());        // ActionMask(4)
        buffer.putInt(getBodySize());        // BodySize(4)
    }

    @Override
    protected int getBodySize() {
        return actions.stream().mapToInt(Action::getBodySize).sum();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        // ATTACK ve BOOST no-op, sıra bozulmaz
        for (Action action : actions) action.writeToBody(buffer);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private int computeMask() {
        return actions.stream()
                .mapToInt(a -> a.getActionType().getBit())
                .reduce(0, (a, b) -> a | b);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.ACTION;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public List<Action> getActions() {
        return actions;
    }

    public boolean has(ActionType type) {
        return actions.stream().anyMatch(a -> a.getActionType() == type);
    }

    @SuppressWarnings("unchecked")
    public <T extends Action> T get(Class<T> type) {
        return (T) actions.stream()
                .filter(a -> a.getClass() == type)
                .findFirst()
                .orElse(null);
    }
}