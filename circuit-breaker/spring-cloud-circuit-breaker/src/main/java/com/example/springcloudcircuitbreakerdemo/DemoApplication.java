package com.example.springcloudcircuitbreakerdemo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

/**
 * Note: The method annotated with @HystrixCommand cannot be in the same class as the invoking class (e.g. {@link MyController} here),
 * otherwise Hystrix will not be working.
 */
@RestController
class MyController {
	@Autowired
	private MyService myService;

	@GetMapping("apple")
	public String apple() {
		return myService.getApple();
	}

	@GetMapping("orange")
	public String orange() {
		return myService.getOrange();
	}

}

@Service
class MyService {

	private static final String SERVER_TIMEOUT_URL = "http://httpbin.org/status/504";

	/**
	 * Spamming this service, you'll realize the the first 2 errors are HTTP 504,
	 * after that most of the time it will show HTTP 500 because circuit has been opened.
	 */
	public String getApple() {
		return new RestTemplate().getForObject(SERVER_TIMEOUT_URL, String.class); // Exception will be thrown here.
	}

	/**
	 * Exception throw from {@link #getOrange()} will be captured by @HystrixCommand, then invoke {@link #fallbackOrange()} as fallback method
	 */
	public String getOrange() {
		return new RestTemplate().getForObject(SERVER_TIMEOUT_URL, String.class); // Exception will be thrown here.
	}
	private String fallbackOrange() {
		return "fallback response!";
	}
}
