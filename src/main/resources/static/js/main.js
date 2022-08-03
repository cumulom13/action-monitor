$(document).ready(function () {
    let messageList = $("#messages");

    let getCreatedMessageString = function (message) {
        let date = new Date(message.lastInteraction);
        return "<li>Message created at: " + date + " with id: " + message.id + " and content: <b>" + message.messageContent + "</b></li>";
    };

    let getUpdatedMessageString = function (message) {
        let date = new Date(message.lastInteraction);
        return "<li>Message with id: " + message.id + " updated at: " + date + " with content: <b>" + message.messageContent + "</b></li>";
    }

    let socket = new SockJS('/actionMonitor');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // subscribe to the /topic/entries endpoint which feeds newly added messages
        stompClient.subscribe('/topic/entries', function (data) {
            // when a message is received add it to the end of the list
            let body = data.body;
            let message = JSON.parse(body);
            if (message.updated) {
                messageList.append(getUpdatedMessageString(message));
            } else {
                messageList.append(getCreatedMessageString(message));
            }
        });
    });
});