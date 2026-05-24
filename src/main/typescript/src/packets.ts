export enum PacketType {
    UNKNOWN = 0,
    VECTOR2 = 1,
    LINE = 2,
    BREAKABLE_LINE = 3,
    POLYGON = 4,
    NOTIFICATION = 5,
    STRING = 6,
    ARRAY = 7,
    UNIFORM_ARRAY = 8,
    ACTION = 9
}

export enum ActionType {
    FORCE = 1,
    ROTATION = 2,
    ATTACK = 4,
    BOOST = 8
}

export interface Vector2Packet {
    x: number;
    y: number;
}

export interface LinePacket {
    a: Vector2Packet;
    b: Vector2Packet;
}

export interface BreakableLinePacket {
    health: number;
    a: Vector2Packet;
    b: Vector2Packet;
}

export interface PolygonPacket {
    lines: LinePacket[];
}

export interface StringPacket {
    text: string;
}

export interface NotificationPacket {
    sender: string;
    message: string;
    datetime: string;
}

export interface ListPacket {
    items: any[];
}

export interface UniformListPacket {
    itemType: PacketType;
    items: any[];
}

export interface ForceActionData {
    dx: number;
    dy: number;
}

export interface RotationActionData {
    targetAngle: number;
}

export class ActionPacket {
    readonly packetType: PacketType = PacketType.ACTION;
    force: ForceActionData | null;
    rotation: RotationActionData | null;
    attack: boolean;
    boost: boolean;

    constructor(
        force: ForceActionData | null = null,
        rotation: RotationActionData | null = null,
        attack: boolean = false,
        boost: boolean = false
    ) {
        this.force = force;
        this.rotation = rotation;
        this.attack = attack;
        this.boost = boost;
    }
}
