# Dynamic Routing with Spring Cloud Gateway

## Libraries
* Spring Cloud Gateway
* Spring Cloud Consul Config
* [git2consul](https://github.com/breser/git2consul)

## Architecture

                      |-------------------| 
                      |                   | 
                      |    Transfer MS    | 
                      |                   | 
                      |-------------------| 
                                ^
                                |
                                | Normal scenario
                                |
                      |-------------------|                         |-------------------|
                      |                   |                         |                   |
Request ----------->  |     Gateway MS    | --------------------->  |  Availability MS  |
                      |                   |    Downtime enabled     |                   |
                      |-------------------|                         |-------------------|
                                |
                                |
                                |
                      |-------------------|
                      |                   |
                      |      Consul       |
                      |                   |
                      |-------------------|
                                ^
                                |
                                | git2consul
                                |
                      |-------------------|
                      |                   |
                      |        git        |
                      |                   |
                      |-------------------|

* Under normal scenario, a request hitting Gateway MS would be routed to Availability MS (through [Discovery Locator](https://cloud.spring.io/spring-cloud-gateway/multi/multi__configuration.html#_discoveryclient_route_definition_locator)).
* Gateway MS contains route definitions with predicates where if a property (e.g. `downtime.transfer.enabled`) is true, it routes the request to Availability MS instead.
* Availability MS contains endpoints that returns specific downtime error message.
* The predicate property (e.g. `downtime.transfer.enabled`) can be stored in Consul, and retrieved by Gateway MS using Spring Cloud Consul Config.
* Any values changes to the property in Consul is automatically reflected at Gateway MS using [Spring Cloud Consul Config Watch](https://cloud.spring.io/spring-cloud-consul/single/spring-cloud-consul.html#spring-cloud-consul-config-watch).
* For maintainability of properties in Consul, the properties can be stored in a git repository and migrate to Consul using git2consul.

## Sample Implementation Code
### Gateway
#### RouteLocator Bean
Notes:
1. `@RefreshScope` is important here to enable hot route definition refresh.
2. TODO 1: Parameterize between predicates' parameters (fromTime, toTime)
3. TODO 2: Update uri of Availability MS instead of hardcoding if using service discovery + Ribbon
4. For each service (e.g. transfer), one new dedicated route definition needs to be created.
```java
@RefreshScope
@Configuration
public class DowntimeConfig {

	@Value("${downtime.app.enabled:false}")
	private boolean appDowntimeEnabled;

	@Value("${downtime.transfer.enabled:false}")
	private boolean transferDowntimeEnabled;

	/**
	 * TODO: Parameterize between predicate's parameter
	 */
	@Bean
	@RefreshScope
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/anything/{segment}")
						.and().predicate(x -> appDowntimeEnabled)
						.and().between(ZonedDateTime.parse("1999-01-01T00:00:00.000+08:00[Asia/Singapore]"), ZonedDateTime.parse("1999-01-01T23:59:59.999+08:00[Asia/Singapore]"))
						.filters(f -> f.rewritePath("/(?<segment>.*)", "/downtime"))
						.uri("http://localhost:7001")
				)
				.route(r -> r.path("/api/transfer/{segment}")
						.and().predicate(x -> transferDowntimeEnabled)
						.and().between(ZonedDateTime.parse("1999-01-01T00:00:00.000+08:00[Asia/Singapore]"), ZonedDateTime.parse("2099-01-01T23:59:59.999+08:00[Asia/Singapore]"))
						.filters(f -> f.rewritePath("/(?<segment>.*)", "/v1/availability/downtime/transfer"))
						.uri("http://localhost:8080") // TODO: Change to "http://availability" when using Service Discovery + Ribbon
				)
				.build();
	}

}
```

#### bootstrap.properties
Notes:
1. MUST put these in bootstrap.properties.
2. Configure your Consul connection as well.
```properties
spring.cloud.consul.config.profile-separator=-
spring.cloud.consul.config.format=FILES
```

### git2consul
* Sample git to store properties: https://github.com/vxavictor513/git2consul-data
* To use git2consul, install using `npm` following the guide [here](https://github.com/breser/git2consul), then execute `git2consul --config-file git2consul.json`.
#### Sample git2consul.json File
```json
{
  "version": "1.0",
  "repos" : [{
    "name" : "config",
    "url" : "https://github.com/vxavictor513/git2consul-data.git",
    "include_branch_name" : false,
    "branches" : ["master"],
    "hooks": [{
      "type" : "polling",
      "interval" : "1"
    }]
  }]
}
```
