package com.example.neighbour;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class NeighbourApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeighbourApplication.class, args);
	}

}

@Configuration
class RestConfiguration {
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

@RestController
class NeighbourController {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${tenant.hello.url}")
	private String tenantUrl;

	@GetMapping("tenant/hello")
	public String helloTenant() {
		return restTemplate.getForObject(tenantUrl, String.class);
	}

	@GetMapping("greet")
	public String greetBySomeone() {
		return "Yo!";
	}
}
