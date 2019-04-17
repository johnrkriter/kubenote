package com.example.tenant;

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
public class TenantApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantApplication.class, args);
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
class TenantController {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${neighbour.greet.url}")
	private String neighbourUrl;

	@GetMapping("neighbour/greet")
	public String greet() {
		return restTemplate.getForObject(neighbourUrl, String.class);
	}

	@GetMapping("hello")
	public String hello() {
		return "Hey!";
	}
}