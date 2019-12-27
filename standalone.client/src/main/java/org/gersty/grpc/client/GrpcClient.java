package org.gersty.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.gersty.grpc.HelloRequest;
import org.gersty.grpc.HelloResponse;
import org.gersty.grpc.HelloServiceGrpc;
import org.gersty.grpcspring.helloworld.HelloWorldServiceGrpc;
import org.gersty.grpcspring.helloworld.Person;


public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channelSpringBootService = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();


        ManagedChannel channelStandAloneService = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub = HelloWorldServiceGrpc.newBlockingStub(channelSpringBootService);

        Person person = Person.newBuilder().setFirstName("First Name").setLastName("Last Name").build();

        System.out.println(stub.sayHello(person).getMesssage());

        HelloServiceGrpc.HelloServiceBlockingStub standAloneStub = HelloServiceGrpc.newBlockingStub(channelStandAloneService);

        HelloResponse helloResponse = standAloneStub.hello(HelloRequest.newBuilder()
                .setFirstName("first name")
                .setLastName("last name")
                .build());

        System.out.println(helloResponse.getGreeting());



        channelSpringBootService.shutdown();
    }
}
