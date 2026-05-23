
const CONFIG = {
    SERVER_TICK_RATE: 64,
    SERVER_TICK_INTERVAL: 1000 / 64
};

// Alt sistemleri ve modülleri başlatıyoruz
const packetHandler = new PacketHandler();
const renderer = new Renderer('gameCanvas');
const camera = new Camera(renderer.canvas.width, renderer.canvas.height);

// WebSocket yöneticisini ayağa kaldır
const wsUrl = `ws://${window.location.host}/ws/game`;
const network = new NetworkManager(wsUrl, packetHandler);
network.connect();

// Mouse tekerlek olayını dinle (Zoom)
window.addEventListener("wheel", (event) => {
    event.preventDefault();
    camera.zoom(event.deltaY);
}, { passive: false });

let lastTime = performance.now();

function gameLoop(currentTime) {
    const dt = currentTime - lastTime;
    lastTime = currentTime;

    // Güncel poligon verilerini ağ yöneticisinden çek
    const activePolygons = network.getPolygons();

    // İlk oyuncu (index 0) her zaman client'ın kendisidir
        let localPlayerPos = null;
        if (activePolygons.length > 0 && Array.isArray(activePolygons[0]) && activePolygons[0].length > 0) {
            const playerLines = activePolygons[0];
            let sumX = 0;
            let sumY = 0;

            // Poligonu oluşturan tüm çizgilerin (hem A hem B noktalarının) X ve Y'lerini topla
            playerLines.forEach(line => {
                sumX += line.a.x + line.b.x;
                sumY += line.a.y + line.b.y;
            });

            // Toplam nokta sayısına (çizgi sayısı * 2) bölerek tam merkezi bul
            const totalPoints = playerLines.length * 2;
            localPlayerPos = {
                x: sumX / totalPoints,
                y: sumY / totalPoints
            };
        }

    // Kamerayı yerel oyuncunun pozisyonuna ve zamana (dt) göre yumuşatıp güncelle
    camera.update(dt, localPlayerPos);

    // Renderer'a güncel poligonları ve kamerayı verip çizimi tetikle
    renderer.drawScene(activePolygons, camera);

    requestAnimationFrame(gameLoop);
}


requestAnimationFrame(gameLoop);