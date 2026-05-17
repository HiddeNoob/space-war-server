const socket = new WebSocket("ws://localhost:8080/ws/game");

socket.addEventListener("open", () => {
    socket.send(JSON.stringify({ type: "join", gameId: "1234" }));
});

socket.addEventListener("message", (event) => {
    console.log("Sunucudan:", event.data);
});

socket.addEventListener("close", (event) => {
    console.log("Bağlantı kesildi:", event);
});