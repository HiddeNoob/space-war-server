export interface WorldPos {
    x: number;
    y: number;
}

export interface ScreenPos {
    x: number;
    y: number;
}

export class Camera {
    screenWidth: number;
    screenHeight: number;
    x: number;
    y: number;
    offset: { x: number; y: number };
    scale: number;
    targetScale: number;
    minScale: number;
    maxScale: number;

    private hasTarget: boolean = false;

    constructor(screenWidth: number, screenHeight: number) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = 0;
        this.y = 0;
        this.offset = {x: 0, y: 0};
        this.scale = 10;
        this.targetScale = 10;
        this.minScale = 5;
        this.maxScale = 40;
    }

    zoom(deltaY: number): void {
        const zoomSensitivity = 0.1;
        this.targetScale *= (deltaY > 0) ? (1 - zoomSensitivity) : (1 + zoomSensitivity);
        this.targetScale = Math.max(this.minScale, Math.min(this.maxScale, this.targetScale));
    }

    update(dtMs: number, targetWorldPos: WorldPos | null, isLocalPlayer: boolean = false): void {
        const dt = dtMs / 1000;
        const trackK = isLocalPlayer ? 5.0 : 15.0; 
        const zoomK = 5.0;

        const zoomFactor = 1 - Math.exp(-zoomK * dt);
        const trackFactor = 1 - Math.exp(-trackK * dt);

        this.scale += (this.targetScale - this.scale) * zoomFactor;

        if (targetWorldPos) {
            if (!this.hasTarget) {
                this.x = targetWorldPos.x;
                this.y = targetWorldPos.y;
                this.hasTarget = true;
            }
            this.x += (targetWorldPos.x - this.x) * trackFactor;
            this.y += (targetWorldPos.y - this.y) * trackFactor;
        }

        this.offset.x = (this.screenWidth / 2) - (this.x * this.scale);
        this.offset.y = (this.screenHeight / 2) - (this.y * this.scale);
    }

    worldToScreen(worldX: number, worldY: number): ScreenPos {
        return {
            x: (worldX * this.scale) + this.offset.x,
            y: (worldY * this.scale) + this.offset.y,
        };
    }

    followPlayer(dt: number, playerState: any, isLocalPlayer: boolean = false): void {
        if (!playerState) return;

        let targetPos: WorldPos | undefined;

        if (playerState.position) {
            targetPos = playerState.position;
        } else {
            const polygon = playerState.polygon || playerState;
            if (!polygon || polygon.length === 0) return;

            let sumX = 0;
            let sumY = 0;
            polygon.forEach((line: any) => {
                sumX += line.a.x + line.b.x;
                sumY += line.a.y + line.b.y;
            });
            const totalPoints = polygon.length * 2;
            targetPos = {x: sumX / totalPoints, y: sumY / totalPoints};
        }

        this.update(dt, targetPos ?? null, isLocalPlayer);
    }
}