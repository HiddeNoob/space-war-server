import {CONFIG} from "./config.js";
import {packetHandler} from "./packetHandler.js";
import {network} from "./network.js";
import {Camera} from "./camera.js";
import {Renderer} from "./renderer.js";
import {PacketType, WorldState} from "./packets.js";
import {AsteroidField} from "./asteroidField.js";
import {createActionPacket, setupInputListeners, InputState, getMoveDirection} from "./inputHandler.js";
import {GameEntity} from "./entity.js";


const canvas = document.getElementById('gameCanvas') as HTMLCanvasElement;
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

// Initialize Camera and Renderer
const renderer = new Renderer('gameCanvas');
const camera = new Camera(renderer.canvas.width, renderer.canvas.height);
// Initialize Camera and Renderer

// Game State
let localPlayer: GameEntity | null = null;
const otherEntities: Map<number, GameEntity> = new Map();

const asteroidField = new AsteroidField();

// Connect WebSocket
network.connect();

// Setup Packet Listeners
packetHandler.onPacket(PacketType.WORLD_STATE, (data: WorldState) => {
    // Update local player
    if (data.localPlayer) {
        if (!localPlayer || localPlayer.id !== data.localPlayer.id) {
            localPlayer = new GameEntity(data.localPlayer);
        } else {
            localPlayer.applyServerUpdate(data.localPlayer);
        }
    }

    // Update other entities
    const currentIds = new Set<number>();
    data.nearObjects.forEach(obj => {
        currentIds.add(obj.id);
        let entity = otherEntities.get(obj.id);
        if (!entity) {
            entity = new GameEntity(obj);
            otherEntities.set(obj.id, entity);
        } else {
            entity.applyServerUpdate(obj);
        }
    });

    // Remove entities no longer in range
    for (const id of otherEntities.keys()) {
        if (!currentIds.has(id)) {
            otherEntities.delete(id);
        }
    }
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
}, {passive: false});

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

    // Update Entities
    if (localPlayer) {
        localPlayer.update(dt, getMoveDirection());
    }
    otherEntities.forEach(entity => entity.update(dt));

    // Update Camera (follow visual position for smoothness)
    camera.followPlayer(dt, localPlayer ? { 
        position: localPlayer.getVisualPosition(),
        rotation: localPlayer.rotation 
    } : null, true);

    // Draw Scene
    const allPolygons = [];
    if (localPlayer) {
        allPolygons.push(localPlayer.getVisualPolygon());
    }

    asteroidField.getStaticAsteroids(camera.x, camera.y)
        .forEach(p => allPolygons.push(p));

    otherEntities.forEach(entity => {
        allPolygons.push(entity.getVisualPolygon());
    });

    renderer.drawScene(allPolygons, camera);

    requestAnimationFrame(gameLoop);
}

// Start Game Loop
requestAnimationFrame(gameLoop);
