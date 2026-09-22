export const CONFIG = {
    SERVER_TICK_RATE: 32,
    SERVER_TICK_INTERVAL: 1000 / 32,
    WS_URL: `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/ws/game`
};

    
