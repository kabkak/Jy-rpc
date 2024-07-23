package com.jiangying.Jyrpc.server.Impl;

import com.jiangying.Jyrpc.server.HttpServer;
import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {

            System.out.println("收到请求" + request.method() + request.uri());
            request.response()
                    .putHeader("content-type", "text/html;charset=utf-8")
                    .end("<h1>你好你好你好你好你好</h1>");
        });
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server listening at " + port);
            } else {
                System.out.println("Failed to bind!");
            }
        });

    }
}
