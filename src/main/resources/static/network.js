// network.js
class NetworkManager {
    constructor(url, packetHandler) {
        this.url = url;
        this.packetHandler = packetHandler;
        this.socket = null;
        this.rawPolygons = []; // Sunucudan gelen en güncel poligon verileri
    }

    connect() {
        this.socket = new WebSocket(this.url);
        this.socket.binaryType = 'arraybuffer';

        this.socket.onopen = () => console.log("Sunucuya başarıyla bağlanıldı.");

        this.socket.onmessage = (event) => {
            if (event.data instanceof ArrayBuffer) {
                const decodedPacket = this.packetHandler.decodeAndDecompressPacket(event.data);

                if (Array.isArray(decodedPacket)) {
                    // Gelen poligon verilerini güncelle
                    this.rawPolygons = decodedPacket;
                } else if (decodedPacket && decodedPacket.sender) {
                    console.log(`Notification: ${decodedPacket.sender}: ${decodedPacket.message}`);
                }
            } else {
                console.log("Metin veri alındı:", event.data);
            }
        };

        this.socket.onerror = (error) => console.error("WebSocket Hatası:", error);
        this.socket.onclose = () => console.log("Bağlantı kapandı. Yeniden bağlanmayı deneyin.");
    }

    getPolygons() {
        return this.rawPolygons;
    }
}