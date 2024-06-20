package com.onlinecode.admin.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class GrpcRegister {

    @Value("${server.grpc-port:9001}")
    private int port;

    private Server server;

    @PostConstruct
    public void startGrpcServer() {
        try {
            server = NettyServerBuilder.forPort(port).addService(new ProcessServiceGrpcImpl()).build().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stopGrpcServer() {
        if (server != null) {
            server.shutdown();
        }
    }
}
