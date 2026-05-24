package com.hiddenoob.space_war_server.packets.action;

import java.nio.ByteBuffer;

public class BoostAction extends Action {

    // ── Decode ───────────────────────────────────────────────────────────────

    /**
     * Body yok — mask'teki bit varlığı yeterli.
     */
    public static final ActionDecoder<BoostAction> DECODER =
            buffer -> new BoostAction();

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public ActionType getActionType() {
        return ActionType.BOOST;
    }

    @Override
    public int getBodySize() {
        return 0;
    }

    @Override
    protected void writeToBody(ByteBuffer buffer) { /* no-op */ }
}