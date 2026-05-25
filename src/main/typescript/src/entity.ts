import { Vector2Packet, EntityState, PlayerState } from "./packets.js";

export class GameEntity {
    id: number;
    // Server/Predicted State (The "Ground Truth")
    position: { x: number; y: number };
    velocity: { x: number; y: number };
    rotation: number;
    polygon: any[];

    // Visual State (The "Smooth Rendered" state)
    private visualPosition: { x: number; y: number };
    private visualRotation: number;
    private basePolygon: any[] | null = null; // Un-rotated, local-space polygon
    
    // Constants
    private lerpSpeed: number = 7.0; 
    private frictionRate: number = 0.4;
    private acceleration: number = 100.0;

    constructor(state: EntityState | PlayerState) {
        this.id = state.id;
        this.position = { ...state.position };
        this.velocity = { ...state.velocity };
        this.rotation = state.rotation;
        this.polygon = state.polygon;

        this.visualPosition = { ...this.position };
        this.visualRotation = this.rotation;

        // Extract a local-space (unrotated, untranslated) polygon for stable rendering
        this.updateBasePolygon(state.polygon, state.position, state.rotation);
    }

    private updateBasePolygon(worldPolygon: any[], center: {x: number, y: number}, rotation: number) {
        const cos = Math.cos(-rotation);
        const sin = Math.sin(-rotation);

        this.basePolygon = worldPolygon.map(line => {
            const transformPoint = (p: {x: number, y: number}) => {
                // 1. Translate to origin
                const dx = p.x - center.x;
                const dy = p.y - center.y;
                // 2. Rotate to 0
                return {
                    x: dx * cos - dy * sin,
                    y: dx * sin + dy * cos
                };
            };
            return {
                a: transformPoint(line.a),
                b: transformPoint(line.b)
            };
        });
    }

    applyServerUpdate(state: EntityState | PlayerState) {
        this.position = { ...state.position };
        this.velocity = { ...state.velocity };
        this.rotation = state.rotation;
        
        // If the polygon itself changed significantly (e.g. broken), update base
        if (state.polygon.length !== (this.basePolygon?.length || 0)) {
            this.updateBasePolygon(state.polygon, state.position, state.rotation);
        }
        this.polygon = state.polygon;
    }

    update(dtMs: number, input?: { dx: number, dy: number }) {
        const dt = dtMs / 1000;

        // 1. Advance Predicted State
        if (input) {
            this.velocity.x += input.dx * this.acceleration * dt;
            this.velocity.y += input.dy * this.acceleration * dt;
        }

        const frictionMultiplier = Math.pow(1 - this.frictionRate, dt);
        this.velocity.x *= frictionMultiplier;
        this.velocity.y *= frictionMultiplier;

        this.position.x += this.velocity.x * dt;
        this.position.y += this.velocity.y * dt;

        // 2. Interpolate Visual Position/Rotation
        const lerpFactor = 1 - Math.exp(-this.lerpSpeed * dt);
        
        this.visualPosition.x += (this.position.x - this.visualPosition.x) * lerpFactor;
        this.visualPosition.y += (this.position.y - this.visualPosition.y) * lerpFactor;

        let rotationDiff = this.rotation - this.visualRotation;
        while (rotationDiff > Math.PI) rotationDiff -= Math.PI * 2;
        while (rotationDiff < -Math.PI) rotationDiff += Math.PI * 2;
        this.visualRotation += rotationDiff * lerpFactor;
    }

    getVisualPolygon(): any[] {
        if (!this.basePolygon) return [];

        const cos = Math.cos(this.visualRotation);
        const sin = Math.sin(this.visualRotation);

        return this.basePolygon.map(line => {
            const transformPoint = (p: {x: number, y: number}) => {
                // 1. Rotate to visual rotation
                const rx = p.x * cos - p.y * sin;
                const ry = p.x * sin + p.y * cos;
                // 2. Translate to visual position
                return {
                    x: rx + this.visualPosition.x,
                    y: ry + this.visualPosition.y
                };
            };
            return {
                a: transformPoint(line.a),
                b: transformPoint(line.b)
            };
        });
    }

    getVisualPosition() {
        return this.visualPosition;
    }

    getVisualRotation() {
        return this.visualRotation;
    }
}
