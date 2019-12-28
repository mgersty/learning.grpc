package org.gersty.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.gersty.grpc.springboot.HelloServiceGrpc;
import org.gersty.grpc.standalone.HelloRequest;
import org.gersty.grpc.standalone.HelloResponse;
import org.gersty.grpc.standalone.StandaloneServiceGrpc;
import org.gersty.grpc.springboot.Person;


public class GrpcClient {
    public static void main(String[] args) {
         ManagedChannel channelSpringBootService = ManagedChannelBuilder.forAddress("localhost", 6565)
                 .usePlaintext()
                 .build();


        ManagedChannel channelStandAloneService = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

         HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channelSpringBootService);

         Person person = Person.newBuilder().setFirstName("First Name").setLastName("Last Name").build();

         System.out.println(stub.sayHello(person).getMesssage());

        StandaloneServiceGrpc.StandaloneServiceBlockingStub standAloneStub = StandaloneServiceGrpc.newBlockingStub(channelStandAloneService);

        HelloResponse helloResponse = standAloneStub.hello(HelloRequest.newBuilder()
                .setFirstName("first name")
                .setLastName("last name")
                .build());

        System.out.println(helloResponse.getGreeting());

        channelStandAloneService.shutdown();
        channelSpringBootService.shutdown();
    }
}
