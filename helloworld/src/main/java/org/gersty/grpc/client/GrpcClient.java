package org.gersty.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.gersty.grpc.HelloRequest;
import org.gersty.grpc.HelloResponse;
import org.gersty.grpc.HelloServiceGrpc;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub
                = HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
                .setFirstName("first name")
                .setLastName("last name")
                .build());

        System.out.println(helloResponse.getGreeting());
        channel.shutdown();
    }
}
