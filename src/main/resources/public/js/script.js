const chat = document.getElementById('chat');
const chatInput = document.getElementById('chatInput');
const messageButton = document.getElementById('sendMessageBtn');

const ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/websocket");
ws.onmessage = msg => updateChat(msg);
ws.onclose = () => alert("WebSocket connection closed");

// Add event listeners to button and input field
messageButton.addEventListener("click", () => sendAndClear(chatInput.value));
chatInput.addEventListener("keypress", function (e) {
    if (e.key === 'Enter') { // Send message if enter is pressed in input field
        sendAndClear(e.target.value);
    }
});

function sendAndClear(message) {
    if (message !== "") {
        ws.send(message);
        chatInput.value = "";
    }
}

function updateChat(msg) { // Update chat-panel and list of connected users
    const data = JSON.parse(msg.data);
    chat.insertAdjacentHTML("afterbegin", data.userMessage);
}