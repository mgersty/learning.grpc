package org.gersty.grpcspring.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.gersty.grpcspring.helloworld.Greeting;
import org.gersty.grpcspring.helloworld.HelloWorldServiceGrpc;
import org.gersty.grpcspring.helloworld.Person;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HelloWorldClient {

    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceBlockingStub;

    @PostConstruct
    private void init() {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565).usePlaintext().build();

        helloWorldServiceBlockingStub =
                HelloWorldServiceGrpc.newBlockingStub(managedChannel);
    }

    public String sayHello(String firstName, String lastName) {
        Person person = Person.newBuilder().setFirstName(firstName)
                .setLastName(lastName).build();

        Greeting greeting =
                helloWorldServiceBlockingStub.sayHello(person);


        return greeting.getMesssage();
    }

}
