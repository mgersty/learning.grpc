package org.gersty.grpc.standalone.server;

import io.grpc.stub.StreamObserver;
import org.gersty.grpc.standalone.HelloRequest;
import org.gersty.grpc.standalone.HelloResponse;
import org.gersty.grpc.standalone.StandaloneServiceGrpc.StandaloneServiceImplBase;

public class StandaloneService extends StandaloneServiceImplBase {

    @Override
    public void hello(
            HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
                .append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
