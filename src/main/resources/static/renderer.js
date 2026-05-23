// renderer.js
class Renderer {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');
    }

    clear() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }

    drawScene(polygons, camera) {
        this.clear();

        polygons.forEach((polygonLines) => {
            if (Array.isArray(polygonLines)) {
                this.drawSinglePolygon(polygonLines, camera);
            }
        });
    }

    drawSinglePolygon(lines, camera) {
        if (!lines || lines.length === 0) return;

        this.ctx.beginPath();

        // İlk noktanın başlangıcını kameranın worldToScreen fonksiyonuyla ekrana dönüştür
        const startScreenPos = camera.worldToScreen(lines[0].a.x, lines[0].a.y);
        this.ctx.moveTo(startScreenPos.x, startScreenPos.y);

        lines.forEach(line => {
            // Her bir çizginin bitiş noktasını kamera matrisine göre ekrana taşı
            const screenPos = camera.worldToScreen(line.b.x, line.b.y);
            this.ctx.lineTo(screenPos.x, screenPos.y);
        });

        this.ctx.closePath();
        this.ctx.strokeStyle = 'white';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
    }
}