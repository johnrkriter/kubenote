package com.example.caching;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableCaching
public class CachingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CachingApplication.class, args);
	}

}

@RestController
class MyController {

	@Autowired
	private MyService service;

	@GetMapping("/cache")
	public String caching(@RequestParam String id) {
		return "Hello, " + service.world(id);
	}

	@GetMapping("/cache/evict")
	public String evictCache() {
		service.evictWorldCache();
		return "Success!";
	}

	@GetMapping("/ehcache")
	public String ehCaching(@RequestParam String id) {
		return "Hello, " + service.cacheWithEh(id);
	}

	@GetMapping("/ehcache/evict")
	public String evictEhCache() {
		service.evictCacheWithEh();
		return "Success!";
	}

}

@Service
@Slf4j
class MyService {

	private Random rand = new Random();

	/**
	 * Caching with EhCache
	 */
	@Cacheable(value = "myEhCache")
	public String cacheWithEh(String id){
		return id + " and " + rand.nextInt(50);
	}

	/**
	 * Evicting EhCache
	 */
	@CacheEvict(value = "myEhCache", allEntries = true)
	public void evictCacheWithEh() {
		log.info("Evicting all!");
	}

	/**
	 * Caching with Redis
	 */
	@Cacheable(value = "worldCache", cacheManager = "redisCacheManager")
	public String world(String id){
		return id + " and " + rand.nextInt(50);
	}

	/**
	 * Evicting Redis Cache
	 */
	@CacheEvict(value = "worldCache", cacheManager = "redisCacheManager", allEntries = true)
	public void evictWorldCache() {
		log.info("Evicting all!");
	}
}