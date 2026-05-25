// Hand-crafted asteroid polygon templates (unit scale, relative vertices)
const TEMPLATES: { x: number; y: number }[][] = [
    [
        {x: -2.0, y: -0.8},
        {x: -0.5, y: -1.4},
        {x: 1.2, y: -1.0},
        {x: 2.0, y: 0.2},
        {x: 1.0, y: 1.1},
        {x: -0.8, y: 1.3},
        {x: -2.0, y: 0.5},
    ],
    [
        {x: -1.0, y: -2.0},
        {x: 0.8, y: -1.5},
        {x: 1.8, y: -0.3},
        {x: 1.2, y: 1.4},
        {x: -0.3, y: 1.8},
        {x: -1.5, y: 0.6},
        {x: -1.8, y: -0.8},
    ],
    [
        {x: 0.0, y: -1.5},
        {x: 1.0, y: -0.5},
        {x: 1.3, y: 0.8},
        {x: 0.0, y: 1.5},
        {x: -1.3, y: 0.8},
        {x: -1.0, y: -0.5},
    ],
    [
        {x: -2.2, y: -0.5},
        {x: -1.0, y: -1.5},
        {x: 1.0, y: -1.5},
        {x: 2.2, y: -0.3},
        {x: 2.0, y: 0.8},
        {x: 0.5, y: 1.5},
        {x: -1.2, y: 1.2},
        {x: -2.2, y: 0.3},
    ],
];

type Line = { a: { x: number; y: number }; b: { x: number; y: number } };

function templateToLines(template: { x: number; y: number }[], scale: number, ox: number, oy: number): Line[] {
    const lines: Line[] = [];
    const n = template.length;
    for (let i = 0; i < n; i++) {
        const curr = template[i];
        const next = template[(i + 1) % n];
        lines.push({
            a: {x: ox + curr.x * scale, y: oy + curr.y * scale},
            b: {x: ox + next.x * scale, y: oy + next.y * scale},
        });
    }
    return lines;
}

interface AsteroidInstance {
    wx: number;  // world-space x (seed position)
    wy: number;  // world-space y (seed position)
    z: number;   // 0 = far/slow, 1 = near/fast
    scale: number;
    templateIndex: number;
}

export class AsteroidField {
    private instances: AsteroidInstance[] = [];
    private viewRadius: number;

    /**
     * @param count        Toplam asteroid sayısı
     * @param spread       Dünya uzayında yayılım alanı (birim)
     * @param viewRadius   getStaticAsteroids'de görüntülenecek max dünya mesafesi
     */
    constructor(count = 250, spread = 500, viewRadius = 80) {
        this.viewRadius = viewRadius;

        for (let i = 0; i < count; i++) {
            const z = Math.random();
            this.instances.push({
                wx: (Math.random() - 0.5) * spread,
                wy: (Math.random() - 0.5) * spread,
                z,
                scale: 0.4 + z * 1.6,
                templateIndex: Math.floor(Math.random() * TEMPLATES.length),
            });
        }
    }

    /**
     * Kamera konumuna göre ekranda görünen asteroitleri polygon (Line[]) olarak döner.
     * Dönen diziyi doğrudan allPolygons'a push edebilirsin.
     */
    getStaticAsteroids(camX: number, camY: number): Line[][] {
        const result: Line[][] = [];

        for (const a of this.instances) {
            // Parallax: uzak asteroitler (z≈0) neredeyse hiç hareket etmez
            const parallaxFactor = 0.05 + a.z * 0.75;
            const worldX = a.wx + camX * (1 - parallaxFactor);
            const worldY = a.wy + camY * (1 - parallaxFactor);

            // Görüş alanı dışındakileri atla (dünya biriminde kaba cull)
            const dx = worldX - camX;
            const dy = worldY - camY;
            if (dx * dx + dy * dy > this.viewRadius * this.viewRadius) continue;

            const template = TEMPLATES[a.templateIndex];
            result.push(templateToLines(template, a.scale, worldX, worldY));
        }

        return result;
    }
}