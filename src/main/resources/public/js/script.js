const openChatBtn = document.getElementById('openChatBtn');

openChatBtn.addEventListener('click', () => {
    window.open("../templates/customerChat.html", "_blank", "popup=yes");
});

