package org.gersty.grpcspring;

import org.gersty.grpcspring.client.HelloWorldClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class GrpcSpringApplicationTests {
	@Autowired
	private HelloWorldClient helloWorldClient;

	@Test
	public void testSayHello() {
		assertThat(helloWorldClient.sayHello("First Name", "Last Name"))
				.isEqualTo("Hello from Spring Boot First Name Last Name!");
	}
}

