import pako from "pako";
import { PacketType, ActionType, ActionPacket } from "./packets.js";
import { network } from "./network.js";

export type PacketCallback = (data: any) => void;

export class PacketHandler {
    decoders: Map<PacketType, Function>;
    private packetListeners: Map<PacketType, PacketCallback[]> = new Map();

    constructor() {
        this.decoders = new Map();
        this.registerDecoders();
        
        network.onData((buffer) => {
            this.handleIncomingData(buffer);
        });
    }

    onPacket(type: PacketType, callback: PacketCallback): void {
        if (!this.packetListeners.has(type)) {
            this.packetListeners.set(type, []);
        }
        this.packetListeners.get(type)!.push(callback);
    }

    private registerDecoders(): void {
        this.decoders.set(PacketType.VECTOR2, this.decodeVector2Packet.bind(this));
        this.decoders.set(PacketType.LINE, this.decodeLinePacket.bind(this));
        this.decoders.set(PacketType.BREAKABLE_LINE, this.decodeBreakableLinePacket.bind(this));
        this.decoders.set(PacketType.POLYGON, this.decodePolygonPacket.bind(this));
        this.decoders.set(PacketType.UNIFORM_ARRAY, this.decodeUniformListPacket.bind(this));
        this.decoders.set(PacketType.ARRAY, this.decodeListPacket.bind(this));
        this.decoders.set(PacketType.NOTIFICATION, this.decodeNotificationPacket.bind(this));
        this.decoders.set(PacketType.STRING, this.decodeStringPacket.bind(this));
        this.decoders.set(PacketType.WORLD_STATE, this.decodeWorldStatePacket.bind(this));
        this.decoders.set(PacketType.PLAYER_STATE, this.decodePlayerStatePacket.bind(this));
        this.decoders.set(PacketType.ENTITY_STATE, this.decodeEntityStatePacket.bind(this));
    }

    private handleIncomingData(buffer: ArrayBuffer): void {
        const dataView = new DataView(buffer);
        const { decodedObject, type } = this._decodePacketInternal(dataView, 0);
        
        if (type !== undefined && this.packetListeners.has(type)) {
            this.packetListeners.get(type)!.forEach(cb => cb(decodedObject));
        }
    }

    _decodePacketInternal(dataView: DataView, offset: number): { decodedObject: any; newOffset: number; type?: PacketType } {
        const startOffset = offset;
        const packetTypeId = dataView.getUint8(offset) as PacketType;
        offset += 1;
        const bodySize = dataView.getInt32(offset, false);
        offset += 4;

        const decoder = this.decoders.get(packetTypeId);
        let decodedObject: any = null;
        let newOffset = offset;

        if (decoder) {
            ({ decodedObject, newOffset } = decoder(dataView, offset, bodySize));
        } else {
            console.warn(`Unknown packet type ID: ${packetTypeId} at offset ${startOffset}`);
            newOffset = offset + bodySize;
        }

        return { decodedObject, newOffset, type: packetTypeId };
    }

    decodeVector2Packet(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        const x = dataView.getFloat32(offset, false);
        offset += 4;
        const y = dataView.getFloat32(offset, false);
        offset += 4;
        return { decodedObject: { x, y }, newOffset: offset };
    }

    decodeLinePacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        const { decodedObject: a, newOffset: offsetA } = this.decodeVector2Packet(dataView, offset);
        const { decodedObject: b, newOffset: offsetB } = this.decodeVector2Packet(dataView, offsetA);
        return { decodedObject: { a, b }, newOffset: offsetB };
    }

    decodeBreakableLinePacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        const health = dataView.getInt32(offset, false);
        offset += 4;
        const { decodedObject: a, newOffset: offsetA } = this.decodeVector2Packet(dataView, offset);
        const { decodedObject: b, newOffset: offsetB } = this.decodeVector2Packet(dataView, offsetA);
        return { decodedObject: { health, a, b }, newOffset: offsetB };
    }

    decodeUniformListPacket(dataView: DataView, offset: number, bodySize: number): { decodedObject: any; newOffset: number } {
        const itemTypeId = dataView.getUint8(offset) as PacketType;
        offset += 1;
        const itemCount = dataView.getInt32(offset, false);
        offset += 4;

        const items: any[] = [];
        let currentOffset = offset;

        if (itemTypeId === PacketType.POLYGON) {
            for (let i = 0; i < itemCount; i++) {
                const { decodedObject: polygonLines, newOffset: nextOffset } = this._decodePacketInternal(dataView, currentOffset);
                if (polygonLines) items.push(polygonLines);
                currentOffset = nextOffset;
            }
            return { decodedObject: items, newOffset: currentOffset };
        }

        for (let i = 0; i < itemCount; i++) {
            let decodedItem: any;
            let itemNewOffset: number;
            if (itemTypeId === PacketType.VECTOR2) ({ decodedObject: decodedItem, newOffset: itemNewOffset } = this.decodeVector2Packet(dataView, currentOffset));
            else if (itemTypeId === PacketType.LINE) ({ decodedObject: decodedItem, newOffset: itemNewOffset } = this.decodeLinePacket(dataView, currentOffset));
            else if (itemTypeId === PacketType.BREAKABLE_LINE) ({ decodedObject: decodedItem, newOffset: itemNewOffset } = this.decodeBreakableLinePacket(dataView, currentOffset));
            else break;
            items.push(decodedItem);
            currentOffset = itemNewOffset;
        }
        return { decodedObject: items, newOffset: currentOffset };
    }

    decodeListPacket(dataView: DataView, offset: number, listBodySize: number): { decodedObject: any; newOffset: number } {
        const items: any[] = [];
        const endOffset = offset + listBodySize;
        let currentOffset = offset;
        while (currentOffset < endOffset) {
            const { decodedObject, newOffset } = this._decodePacketInternal(dataView, currentOffset);
            items.push(decodedObject);
            currentOffset = newOffset;
        }
        return { decodedObject: items, newOffset: currentOffset };
    }

    decodePolygonPacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        const { decodedObject: lines, newOffset } = this._decodePacketInternal(dataView, offset);
        return { decodedObject: lines, newOffset };
    }

    decodeStringPacket(dataView: DataView, offset: number, bodySize: number): { decodedObject: any; newOffset: number } {
        const decoder = new TextDecoder('UTF-8');
        const stringBytes = new Uint8Array(dataView.buffer, dataView.byteOffset + offset, bodySize);
        return { decodedObject: decoder.decode(stringBytes), newOffset: offset + bodySize };
    }

    decodeNotificationPacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        let currentOffset = offset;
        const { decodedObject: sender, newOffset: sOffset } = this._decodePacketInternal(dataView, currentOffset);
        currentOffset = sOffset;
        const { decodedObject: datetime, newOffset: dOffset } = this._decodePacketInternal(dataView, currentOffset);
        currentOffset = dOffset;
        const { decodedObject: message, newOffset: mOffset } = this._decodePacketInternal(dataView, currentOffset);
        return { decodedObject: { sender, message, datetime }, newOffset: mOffset };
    }

    decodePlayerStatePacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        const id = Number(dataView.getBigInt64(offset, false));
        offset += 8;
        const { decodedObject: position, newOffset: offset1 } = this._decodePacketInternal(dataView, offset);
        const { decodedObject: velocity, newOffset: offset2 } = this._decodePacketInternal(dataView, offset1);
        const rotation = dataView.getFloat32(offset2, false);
        const { decodedObject: polygon, newOffset: offset3 } = this._decodePacketInternal(dataView, offset2 + 4);
        return { decodedObject: { id, position, velocity, rotation, polygon }, newOffset: offset3 };
    }

    decodeEntityStatePacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        return this.decodePlayerStatePacket(dataView, offset);
    }

    decodeWorldStatePacket(dataView: DataView, offset: number): { decodedObject: any; newOffset: number } {
        const { decodedObject: localPlayer, newOffset: offset1 } = this._decodePacketInternal(dataView, offset);
        const { decodedObject: nearObjects, newOffset: offset2 } = this._decodePacketInternal(dataView, offset1);
        return { decodedObject: { localPlayer, nearObjects }, newOffset: offset2 };
    }

    encode(packetType: PacketType, payload: ActionPacket): ArrayBuffer | null {
        if (packetType !== PacketType.ACTION) return null;
        let mask = 0;
        let bodySize = 0;
        if (payload.force) { mask |= ActionType.FORCE; bodySize += 8; }
        if (payload.rotation) { mask |= ActionType.ROTATION; bodySize += 4; }
        if (payload.attack) mask |= ActionType.ATTACK;
        if (payload.boost) mask |= ActionType.BOOST;

        const buffer = new ArrayBuffer(9 + bodySize);
        const dataView = new DataView(buffer);
        dataView.setUint8(0, PacketType.ACTION);
        dataView.setInt32(1, mask, false);
        dataView.setInt32(5, bodySize, false);
        let offset = 9;
        if (payload.force) { dataView.setFloat32(offset, payload.force.dx, false); dataView.setFloat32(offset + 4, payload.force.dy, false); offset += 8; }
        if (payload.rotation) { dataView.setFloat32(offset, payload.rotation.targetAngle, false); }
        return buffer;
    }
}

export const packetHandler = new PacketHandler();
