# Cache Error Handler
In Spring, by default, [SimpleCacheErrorHandler](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/interceptor/SimpleCacheErrorHandler.html) is used and simply throws the exception back at the client.

To change this behavior, e.g. not throwing any error during cache failure, we can use [CachingConfigurerSupport](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/CachingConfigurerSupport.html) to override the [CacheErrorHandler](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/interceptor/CacheErrorHandler.html).

	@Configuration
	@EnableCaching
	public class AppConfig extends CachingConfigurerSupport {
		@Bean
		@Override
		public CacheErrorHandler errorHandler() {
			// configure and return CacheErrorHandler instance
		}
		// ...
	}

 