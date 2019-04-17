# Caching
This is a sample microservice showing how caching can be configured for Spring Boot 2 application.

## Features
* Local caching using EhCache
* Centralized caching using Redis

## Try it out locally
* By default, Redis cache is enabled and configured to connect to `localhost:6379`. To disable Redis cache, set `redis.cache.enabled=false`.
* Run the example microservice with `local` profile.
* Call `GET http://localhost:8080/cache?id={id}` to do Redis caching.
* Call `GET http://localhost:8080/cache/evict` to evict Redis cache.
* Call `GET http://localhost:8080/ehcache?id={id}` to do EhCache.
* Call `GET http://localhost:8080/ehcache/evict` to evict EhCache. 
