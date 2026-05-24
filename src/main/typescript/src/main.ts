import { CONFIG } from "./config.js";
import { packetHandler } from "./packetHandler.js";
import { network } from "./network.js";
import { Camera } from "./camera.js";
import { Renderer } from "./renderer.js";
import { PacketType } from "./packets.js";
import { setupInputListeners, createActionPacket } from "./inputHandler.js";

// Initialize Camera and Renderer
const renderer = new Renderer('gameCanvas');
const camera = new Camera(renderer.canvas.width, renderer.canvas.height);

// Game State
let activePolygons: any[] = [];

// Connect WebSocket
network.connect();

// Setup Packet Listeners
packetHandler.onPacket(PacketType.UNIFORM_ARRAY, (data) => {
    activePolygons = data;
});

packetHandler.onPacket(PacketType.ARRAY, (data) => {
    activePolygons = data;
});

packetHandler.onPacket(PacketType.NOTIFICATION, (data) => {
    console.log(`Notification: ${data.sender}: ${data.message}`);
});

// Setup Input
setupInputListeners();

// Handle window resize dynamically to prevent screen stretching
window.addEventListener("resize", () => {
    renderer.canvas.width = window.innerWidth;
    renderer.canvas.height = window.innerHeight;
    camera.screenWidth = window.innerWidth;
    camera.screenHeight = window.innerHeight;
});

// Capture mouse wheel zoom events
window.addEventListener("wheel", (event: WheelEvent) => {
    event.preventDefault();
    camera.zoom(event.deltaY);
}, { passive: false });

// Periodic action sending loop
setInterval(() => {
    const actionPayload = createActionPacket();
    const encodedBuffer = packetHandler.encode(PacketType.ACTION, actionPayload);
    if (encodedBuffer) {
        network.send(encodedBuffer);
    }
}, CONFIG.SERVER_TICK_INTERVAL);

// GAME LOOP
let lastTime = performance.now();

function gameLoop(currentTime: number): void {
    const dt = currentTime - lastTime;
    lastTime = currentTime;

    // Index 0 represents the local player
    let localPlayerPos = null;
    if (activePolygons.length > 0 && Array.isArray(activePolygons[0]) && activePolygons[0].length > 0) {
        const playerLines = activePolygons[0];
        let sumX = 0;
        let sumY = 0;

        // Sum lines start/end vectors to calculate centroid
        playerLines.forEach((line: any) => {
            sumX += line.a.x + line.b.x;
            sumY += line.a.y + line.b.y;
        });

        const totalPoints = playerLines.length * 2;
        localPlayerPos = {
            x: sumX / totalPoints,
            y: sumY / totalPoints
        };
    }

    // Update Camera
    camera.update(dt, localPlayerPos);

    // Draw Scene
    renderer.drawScene(activePolygons, camera);

    requestAnimationFrame(gameLoop);
}

// Start Game Loop
requestAnimationFrame(gameLoop);
