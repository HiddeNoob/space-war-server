package com.hiddenoob.space_war_server.packets;

import org.junit.jupiter.api.Test;


public class PacketTest {

    @Test
    void testListPacketBinarySerialization() {
        // Test edilecek paket yapısının oluşturulması
        ListPacket allAstreoidsPacket = new ListPacket(new Packet[]{
                new LinePacket(new Vector2Packet(1,1), new Vector2Packet(2,2)),
        });

        // Verinin binary (byte array) formatına dönüştürülmesi
        byte[] binaryOutput = allAstreoidsPacket.toArray();

        // assertArrayEquals(expectedBytes, binaryOutput);
    }
}
