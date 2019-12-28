package org.gersty.grpc.springboot.server;


import io.grpc.stub.StreamObserver;
import org.gersty.grpc.springboot.Greeting;
import org.gersty.grpc.springboot.Person;
import org.lognet.springboot.grpc.GRpcService;
import org.gersty.grpc.springboot.HelloServiceGrpc;
import org.springframework.stereotype.Component;

@Component
@GRpcService
public class HelloWorldServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(Person request, StreamObserver<Greeting> responseObserver) {

        String message = "Hello from Spring Boot " + request.getFirstName() + " "
                + request.getLastName() + "!";
        Greeting greeting = Greeting.newBuilder().setMesssage(message).build();

        responseObserver.onNext(greeting);
        responseObserver.onCompleted();
    }

}

