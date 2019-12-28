package org.gersty.grpc.springboot.confg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.gersty.grpc")
public class GrpcSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrpcSpringApplication.class, args);
	}

}
