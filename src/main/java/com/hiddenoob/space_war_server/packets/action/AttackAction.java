package com.hiddenoob.space_war_server.packets.action;

import java.nio.ByteBuffer;

public class AttackAction extends Action {

    // ── Decode ───────────────────────────────────────────────────────────────

    /**
     * Body yok — mask'teki bit varlığı yeterli.
     */
    public static final ActionDecoder<AttackAction> DECODER =
            buffer -> new AttackAction();

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public ActionType getActionType() {
        return ActionType.ATTACK;
    }

    @Override
    public int getBodySize() {
        return 0;
    }

    @Override
    protected void writeToBody(ByteBuffer buffer) { /* no-op */ }
}