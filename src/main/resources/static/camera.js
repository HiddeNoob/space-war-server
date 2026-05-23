// camera.js
class Camera {
    constructor(screenWidth, screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Kameranın uzaydaki (dünya) odak noktası
        this.x = 0;
        this.y = 0;

        // Ekrana çizerken kullanılacak offset (artık lerplenmeyecek, kesin hesaplanacak)
        this.offset = { x: 0, y: 0 };

        // Zoom özellikleri
        this.scale = 10;
        this.targetScale = 10;
        this.minScale = 2;
        this.maxScale = 40;
    }

    zoom(deltaY) {
        const zoomSensitivity = 0.1;
        if (deltaY > 0) {
            this.targetScale *= (1 - zoomSensitivity);
        } else {
            this.targetScale *= (1 + zoomSensitivity);
        }
        this.targetScale = Math.max(this.minScale, Math.min(this.maxScale, this.targetScale));
    }

    update(dt, targetWorldPos) {
        // 1. Yumuşak Zoom Geçişi (Lerp)
        const zoomLerpSpeed = 0.005;
        this.scale += (this.targetScale - this.scale) * zoomLerpSpeed * dt;

        if (targetWorldPos) {
            // 2. Kameranın "Dünya üzerindeki konumunu" hedefe doğru yumuşakça kaydır (Lerp)
            const moveLerpSpeed = 0.005;
            this.x += (targetWorldPos.x - this.x) * moveLerpSpeed * dt;
            this.y += (targetWorldPos.y - this.y) * moveLerpSpeed * dt;
        }

        // 3. EKRAN OFFSET'İNİ KESİN HESAPLA (Sorunu çözen kısım burası)
        // Offset = (Ekranın Merkezi) - (Kameranın Odak Noktası * Güncel Scale)
        // Bu sayede scale değiştiği an offset de anında uyum sağlar, zoom sadece kameranın x,y merkezine (oyuncuya) atılır.
        this.offset.x = (this.screenWidth / 2) - (this.x * this.scale);
        this.offset.y = (this.screenHeight / 2) - (this.y * this.scale);
    }

    // Dünya koordinatını ekran koordinatına çevirir
    worldToScreen(worldX, worldY) {
        return {
            x: (worldX * this.scale) + this.offset.x,
            y: (worldY * this.scale) + this.offset.y
        };
    }
}