const PacketType = {
    UNKNOWN: 0,
    VECTOR2: 1,
    LINE: 2,
    BREAKABLE_LINE: 3,
    POLYGON: 4,
    NOTIFICATION: 5,
    STRING: 6,
    ARRAY: 7,
    UNIFORM_ARRAY: 8
};

class PacketHandler {
    constructor() {
        this.decoders = new Map();
        this.registerDecoders();
    }

    // İleride yeni paket tipi geldiğinde tek yapman gereken buraya eklemek
    registerDecoders() {
        this.decoders.set(PacketType.VECTOR2, this.decodeVector2Packet.bind(this));
        this.decoders.set(PacketType.LINE, this.decodeLinePacket.bind(this));
        this.decoders.set(PacketType.POLYGON, this.decodePolygonPacket.bind(this));
        this.decoders.set(PacketType.UNIFORM_ARRAY, this.decodeUniformListPacket.bind(this));
        this.decoders.set(PacketType.ARRAY, this.decodeListPacket.bind(this));
        this.decoders.set(PacketType.NOTIFICATION, this.decodeNotificationPacket.bind(this));
        this.decoders.set(PacketType.STRING, this.decodeStringPacket.bind(this));
    }

    // WebSocket'ten gelen ArrayBuffer giriş noktası
    decodeAndDecompressPacket(arrayBuffer) {
        const dataView = new DataView(arrayBuffer);
        const isCompressed = dataView.getUint8(0);
        let processedBuffer;
        let initialOffset = 1;

        if (isCompressed === 1) {
            const compressedData = new Uint8Array(arrayBuffer, initialOffset);
            try {
                const decompressedData = pako.inflateRaw(compressedData);
                processedBuffer = decompressedData.buffer;
                initialOffset = 0;
            } catch (e) {
                console.error("Error decompressing message:", e);
                return null;
            }
        } else {
            processedBuffer = arrayBuffer;
        }

        const { decodedObject } = this._decodePacketInternal(new DataView(processedBuffer), initialOffset);
        return decodedObject;
    }

    _decodePacketInternal(dataView, offset) {
        const startOffset = offset;
        const packetTypeId = dataView.getUint8(offset);
        offset += 1;
        const bodySize = dataView.getInt32(offset, false); // big-endian
        offset += 4;

        const decoder = this.decoders.get(packetTypeId);
        let decodedObject = null;
        let newOffset = offset;

        if (decoder) {
            // Fonksiyon imzalarına göre parametreleri gönderiyoruz
            ({ decodedObject, newOffset } = decoder(dataView, offset, bodySize));
        } else {
            console.warn(`Unknown packet type ID: ${packetTypeId} at offset ${startOffset}`);
            newOffset = offset + bodySize; // Bilinmeyen gövdeyi atla
        }

        return { decodedObject, newOffset };
    }

    decodeVector2Packet(dataView, offset) {
        const x = dataView.getFloat32(offset, false);
        offset += 4;
        const y = dataView.getFloat32(offset, false);
        offset += 4;
        return { decodedObject: { x, y }, newOffset: offset };
    }

    decodeLinePacket(dataView, offset) {
        const { decodedObject: a, newOffset: offsetA } = this.decodeVector2Packet(dataView, offset);
        const { decodedObject: b, newOffset: offsetB } = this.decodeVector2Packet(dataView, offsetA);
        return { decodedObject: { a, b }, newOffset: offsetB };
    }

    decodeUniformListPacket(dataView, offset, bodySize) {
        const itemTypeId = dataView.getUint8(offset);
        offset += 1;

        const items = [];
        let currentOffset = offset;
        const endOfUniformListBody = offset + bodySize;

        if (itemTypeId === PacketType.POLYGON) {
            while (currentOffset < endOfUniformListBody) {
                const { decodedObject: polygonLines, newOffset: nextOffset } = this._decodePacketInternal(dataView, currentOffset);
                if (polygonLines) {
                    items.push(polygonLines);
                }
                currentOffset = nextOffset;
            }
            return { decodedObject: items, newOffset: currentOffset };
        }

        let itemBodySize;
        switch (itemTypeId) {
            case PacketType.VECTOR2: itemBodySize = 8; break;
            case PacketType.LINE: itemBodySize = 16; break;
            case PacketType.STRING:
                return { decodedObject: [], newOffset: offset + bodySize };
            default:
                return { decodedObject: [], newOffset: offset + bodySize };
        }

        const itemCount = bodySize / itemBodySize;
        for (let i = 0; i < itemCount; i++) {
            let decodedItem;
            let itemNewOffset;
            if (itemTypeId === PacketType.VECTOR2) {
                ({ decodedObject: decodedItem, newOffset: itemNewOffset } = this.decodeVector2Packet(dataView, currentOffset));
            } else if (itemTypeId === PacketType.LINE) {
                ({ decodedObject: decodedItem, newOffset: itemNewOffset } = this.decodeLinePacket(dataView, currentOffset));
            }
            items.push(decodedItem);
            currentOffset = itemNewOffset;
        }
        return { decodedObject: items, newOffset: currentOffset };
    }

    decodeListPacket(dataView, offset, listBodySize) {
        const items = [];
        const endOffset = offset + listBodySize;
        let currentOffset = offset;

        while (currentOffset < endOffset) {
            const { decodedObject, newOffset } = this._decodePacketInternal(dataView, currentOffset);
            items.push(decodedObject);
            currentOffset = newOffset;
        }
        return { decodedObject: items, newOffset: currentOffset };
    }

    decodePolygonPacket(dataView, offset) {
        const { decodedObject: lines, newOffset } = this._decodePacketInternal(dataView, offset);
        return { decodedObject: lines, newOffset };
    }

    decodeStringPacket(dataView, offset, bodySize) {
        const decoder = new TextDecoder('UTF-8');
        const stringBytes = new Uint8Array(dataView.buffer, offset, bodySize);
        const decodedString = decoder.decode(stringBytes);
        return { decodedObject: decodedString, newOffset: offset + bodySize };
    }

    decodeNotificationPacket(dataView, offset) {
        let currentOffset = offset;
        const { decodedObject: sender, newOffset: senderOffset } = this._decodePacketInternal(dataView, currentOffset);
        currentOffset = senderOffset;
        const { decodedObject: datetime, newOffset: datetimeOffset } = this._decodePacketInternal(dataView, currentOffset);
        currentOffset = datetimeOffset;
        const { decodedObject: message, newOffset: messageOffset } = this._decodePacketInternal(dataView, currentOffset);
        currentOffset = messageOffset;

        return { decodedObject: { sender, message, datetime }, newOffset: currentOffset };
    }
}