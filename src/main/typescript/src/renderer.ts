import { Camera } from "./camera.js";

export class Renderer {
    canvas: HTMLCanvasElement;
    ctx: CanvasRenderingContext2D;

    constructor(canvasId: string) {
        const canvas = document.getElementById(canvasId) as HTMLCanvasElement | null;
        if (!canvas) {
            throw new Error(`Canvas element with id "${canvasId}" not found.`);
        }
        this.canvas = canvas;
        const ctx = this.canvas.getContext('2d');
        if (!ctx) {
            throw new Error("2D rendering context not available.");
        }
        this.ctx = ctx;
    }

    clear(): void {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }

    drawScene(polygons: any[], camera: Camera): void {
        this.clear();

        polygons.forEach((polygonLines) => {
            if (Array.isArray(polygonLines)) {
                this.drawSinglePolygon(polygonLines, camera);
            }
        });
    }

    drawSinglePolygon(lines: any[], camera: Camera): void {
        if (!lines || lines.length === 0) return;

        this.ctx.beginPath();

        const startScreenPos = camera.worldToScreen(lines[0].a.x, lines[0].a.y);
        this.ctx.moveTo(startScreenPos.x, startScreenPos.y);

        lines.forEach(line => {
            const screenPos = camera.worldToScreen(line.b.x, line.b.y);
            this.ctx.lineTo(screenPos.x, screenPos.y);
        });

        this.ctx.closePath();
        this.ctx.strokeStyle = 'white';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
    }
}
