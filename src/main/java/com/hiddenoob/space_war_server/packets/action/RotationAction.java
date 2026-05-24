package com.hiddenoob.space_war_server.packets.action;

import java.nio.ByteBuffer;

public class RotationAction extends Action {

    public static final ActionDecoder<RotationAction> DECODER = buffer -> {
        float angle = buffer.getFloat();
        return new RotationAction(angle);
    };
    private final float targetAngle; // radyan

    // ── Decode ───────────────────────────────────────────────────────────────

    public RotationAction(float targetAngle) {
        this.targetAngle = targetAngle;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public ActionType getActionType() {
        return ActionType.ROTATION;
    }

    @Override
    public int getBodySize() {
        return Float.BYTES;
    } // 4 byte

    @Override
    protected void writeToBody(ByteBuffer buffer) {
        buffer.putFloat(targetAngle);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public float getTargetAngle() {
        return targetAngle;
    }
}