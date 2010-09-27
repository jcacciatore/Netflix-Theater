#!/home/rckenned/local/bin/node

var sys = require("sys"),
    ws = require("websocket-server");

var server = ws.createServer();

server.addListener("listening", function() {
    sys.log("Theater server is listening for connections...");
});

server.addListener("connection", function(conn) {
    sys.log(conn);

    conn.addListener("message", function(message) {
        server.broadcast(message);
    });
});

server.addListener("close", function(conn) {
    sys.log("User has left the theater");
});

server.listen("7001");
