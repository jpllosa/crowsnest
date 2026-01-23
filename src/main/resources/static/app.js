const envsStompClient = new StompJs.Client({
    brokerURL: 'ws://' + wsHostname + '/envs-websocket'
});

envsStompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    envsStompClient.subscribe('/topic/environments', (environments) => {
        showEnvironments(JSON.parse(environments.body));
    });

    envsStompClient.publish({
        destination: "/app/environment",
        body: "init"
    });
};

envsStompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

envsStompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    envsStompClient.activate();
}

function disconnect() {
    envsStompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function showEnvironments(environments) {
    console.log(environments);

    $environments = $("#environments");
    $environments.empty();

    $alerts = $("#alerts");
    $alerts.empty();

    body = "";

    for (const env of environments) {
        body = body + "<div class=\"panel panel-default\">";
        body = body + "<div class=\"panel-heading\"><h3 class=\"panel-title\">"
            + env.name
            + "</h3></div><div class=\"panel-body\">";
        for (const app of env.apps) {
            if (app.status === "up") {
                body = body + "<div class=\"alert alert-success\" role=\"alert\">";
                body = body + "<p><span class=\"label label-success\">" + app.name
                    + "</span> <span class=\"glyphicon glyphicon-thumbs-up\" aria-hidden=\"true\"></span> "
                    + "<a href=\"" + app.url + "\">" + app.url + "</a>"
                    + "</p>";
                body = body + "</div>";
            } else {
                alert = "<div class=\"alert alert-danger\" role=\"alert\">"
                    + "<p><span class=\"label label-danger\">" + app.name
                    + "</span> <span class=\"glyphicon glyphicon-thumbs-down\" aria-hidden=\"true\"></span> "
                    + "<a href=\"" + app.url + "\">" + app.url + "</a>"
                    + "</p></div>";

                body = body + alert;

                $alerts.append(alert);
            }
        }
        body = body + "</div></div>";
    }

    $environments.append(body);
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
});