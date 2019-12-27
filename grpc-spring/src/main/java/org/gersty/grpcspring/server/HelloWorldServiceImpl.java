package org.gersty.grpcspring.server;


import io.grpc.stub.StreamObserver;
import org.gersty.grpcspring.helloworld.Greeting;
import org.gersty.grpcspring.helloworld.Person;
import org.lognet.springboot.grpc.GRpcService;
import org.gersty.grpcspring.helloworld.HelloWorldServiceGrpc;


@GRpcService
public class HelloWorldServiceImpl extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

    @Override
    public void sayHello(Person request, StreamObserver<Greeting> responseObserver) {

        String message = "Hello from Spring Boot " + request.getFirstName() + " "
                + request.getLastName() + "!";
        Greeting greeting = Greeting.newBuilder().setMesssage(message).build();

        responseObserver.onNext(greeting);
        responseObserver.onCompleted();
    }

}

