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
let localPlayerState: any = null;
let otherObjectsPolygons: any[] = [];

// Connect WebSocket
network.connect();

// Setup Packet Listeners
packetHandler.onPacket(PacketType.WORLD_STATE, (data) => {
    localPlayerState = data.localPlayer;
    otherObjectsPolygons = data.nearObjects;
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

    // Update Camera
    camera.followPlayer(dt, localPlayerState);

    // Draw Scene
    const allPolygons = [];
    if (localPlayerState && localPlayerState.polygon) {
        allPolygons.push(localPlayerState.polygon);
    }
    otherObjectsPolygons.forEach(p => allPolygons.push(p));
    
    renderer.drawScene(allPolygons, camera);

    requestAnimationFrame(gameLoop);
}

// Start Game Loop
requestAnimationFrame(gameLoop);
