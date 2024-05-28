const ws = new WebSocket('ws://localhost:7000/websocket');
const sendMessageBtn = document.getElementById('sendMessageBtn');
const messageInput = document.getElementById('messageInput');
const openChatBtn = document.getElementById('openChatBtn');


ws.onopen = () => {
    console.log('WebSocket connection established');
    window.open("../templates/customerChat.html", "", "popup=yes");
};

ws.onmessage = event => {
    const messagesList = document.getElementById('messages');
    const newMessage = document.createElement('li');
    newMessage.textContent = event.data;
    messagesList.appendChild(newMessage);
};

ws.onclose = () => {
    console.log('WebSocket connection closed');
};

ws.onerror = error => {
    console.error('WebSocket error:', error);
};


openChatBtn.addEventListener('click', () => {
    console.log('WebSocket connection established');
    window.open("../templates/customerChat.html", "_blank", "popup=yes");
});

sendMessageBtn.addEventListener('click', () => {
    const message = messageInput.value;
    ws.send(message);
    messageInput.value = '';
});

messageInput.addEventListener('keypress', event => {
    if (event.key === 'Enter') {
        const message = messageInput.value;
        ws.send(message);
        messageInput.value = '';
    }
});