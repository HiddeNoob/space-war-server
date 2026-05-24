import pako from "pako";
import { CONFIG } from "./config.js";

export type DataCallback = (buffer: ArrayBuffer) => void;

export class NetworkManager {
    url: string;
    socket: WebSocket | null;
    private onDataCallbacks: DataCallback[] = [];

    constructor(url: string) {
        this.url = url;
        this.socket = null;
    }

    onData(callback: DataCallback): void {
        this.onDataCallbacks.push(callback);
    }

    connect(): void {
        this.socket = new WebSocket(this.url);
        this.socket.binaryType = 'arraybuffer';

        this.socket.onopen = () => console.log("Sunucuya başarıyla bağlanıldı.");

        this.socket.onmessage = (event: MessageEvent) => {
            if (event.data instanceof ArrayBuffer) {
                const buffer = event.data;
                const dataView = new DataView(buffer);
                const isCompressed = dataView.getUint8(0);
                let processedBuffer: ArrayBuffer;

                if (isCompressed === 1) {
                    const compressedData = new Uint8Array(buffer, 1);
                    try {
                        const decompressedData = pako.inflateRaw(compressedData);
                        processedBuffer = decompressedData.buffer;
                    } catch (e) {
                        console.error("Error decompressing message:", e);
                        return;
                    }
                } else {
                    processedBuffer = buffer.slice(1);
                }

                this.onDataCallbacks.forEach(cb => cb(processedBuffer));
            } else {
                console.log("Metin veri alındı:", event.data);
            }
        };

        this.socket.onerror = (error: Event) => console.error("WebSocket Hatası:", error);
        this.socket.onclose = () => console.log("Bağlantı kapandı. Yeniden bağlanmayı deneyin.");
    }

    send(buffer: ArrayBuffer): void {
        if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
            return;
        }

        try {
            // Compress using raw deflate
            const compressedData = pako.deflateRaw(new Uint8Array(buffer));

            const outBuffer = new Uint8Array(1 + compressedData.length);
            outBuffer[0] = 1; // Compressed flag = 1
            outBuffer.set(compressedData, 1);

            this.socket.send(outBuffer.buffer);
        } catch (e) {
            console.error("Paket sıkıştırılırken veya gönderilirken hata oluştu:", e);
        }
    }
}

export const network = new NetworkManager(CONFIG.WS_URL);
