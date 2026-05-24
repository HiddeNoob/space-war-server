package com.hiddenoob.space_war_server.packets.action;

import java.nio.ByteBuffer;

public class ForceAction extends Action {

    public static final ActionDecoder<ForceAction> DECODER = buffer -> {
        float dx = buffer.getFloat();
        float dy = buffer.getFloat();
        return new ForceAction(dx, dy);
    };
    private final float dx;
    private final float dy;

    // ── Decode ───────────────────────────────────────────────────────────────

    public ForceAction(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public ActionType getActionType() {
        return ActionType.FORCE;
    }

    @Override
    public int getBodySize() {
        return Float.BYTES * 2;
    } // 8 byte

    @Override
    protected void writeToBody(ByteBuffer buffer) {
        buffer.putFloat(dx);
        buffer.putFloat(dy);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }
}