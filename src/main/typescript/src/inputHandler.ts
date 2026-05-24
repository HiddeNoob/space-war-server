import { ActionPacket, ForceActionData, RotationActionData } from "./packets.js";

export interface InputStateShape {
    keys: { w: boolean; a: boolean; s: boolean; d: boolean };
    mouse: { x: number; y: number };
    attack: boolean;
    boost: boolean;
}

export const InputState: InputStateShape = {
    keys: { w: false, a: false, s: false, d: false },
    mouse: { x: window.innerWidth / 2, y: window.innerHeight / 2 },
    attack: false,
    boost: false
};

export function setupInputListeners(): void {
    window.addEventListener("keydown", (e: KeyboardEvent) => {
        const key = e.key.toLowerCase();
        if (Object.prototype.hasOwnProperty.call(InputState.keys, key)) {
            (InputState.keys as Record<string, boolean>)[key] = true;
        }
        if (key === " ") InputState.attack = true;
        if (key === "shift") InputState.boost = true;
    });

    window.addEventListener("keyup", (e: KeyboardEvent) => {
        const key = e.key.toLowerCase();
        if (Object.prototype.hasOwnProperty.call(InputState.keys, key)) {
            (InputState.keys as Record<string, boolean>)[key] = false;
        }
    });

    window.addEventListener("mousemove", (e: MouseEvent) => {
        InputState.mouse.x = e.clientX;
        InputState.mouse.y = e.clientY;
    });

    window.addEventListener("mousedown", (e: MouseEvent) => {
        if (e.button === 0) InputState.attack = true;
    });
}

export function createActionPacket(): ActionPacket {
    let dx = 0;
    let dy = 0;
    if (InputState.keys.w) dy -= 1;
    if (InputState.keys.s) dy += 1;
    if (InputState.keys.a) dx -= 1;
    if (InputState.keys.d) dx += 1;

    const length = Math.sqrt(dx * dx + dy * dy);
    if (length > 0) {
        dx /= length;
        dy /= length;
    }
    const force: ForceActionData | null = (dx !== 0 || dy !== 0) ? { dx, dy } : null;

    const centerX = window.innerWidth / 2;
    const centerY = window.innerHeight / 2;
    const targetAngle = Math.atan2(InputState.mouse.y - centerY, InputState.mouse.x - centerX);
    const rotation: RotationActionData = { targetAngle };

    const actionPayload = new ActionPacket(
        force,
        rotation,
        InputState.attack,
        InputState.boost
    );

    // Reset non-continuous actions
    InputState.attack = false;
    InputState.boost = false;

    return actionPayload;
}
