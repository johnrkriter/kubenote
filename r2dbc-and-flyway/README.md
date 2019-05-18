# R2DBC 

## Dependencies
* Maven
* Java 11
* Spring Boot 2.2.0 (SNAPSHOT)
* [Spring Data R2DBC 1.0 M2](https://spring.io/blog/2019/05/15/spring-data-r2dbc-1-0-m2-and-spring-boot-starter-released)
* [R2DBC 0.8.0](https://r2dbc.io/2019/05/13/r2dbc-0-8-milestone-8-released)
* Flyway

## Prerequisites
### Prepare a database
You can use either MySQL or PostgreSQL.

The code is tested against MySQL 8 and PostgreSQL 11.

#### MySQL
* MacOS: `brew install mysql`
* Windows: https://dev.mysql.com/downloads/installer/
```properties
spring.r2dbc.url=r2dbc:pool:mysql://127.0.0.1:3306/demo
spring.r2dbc.username=user
spring.r2dbc.password=password
```

#### PostgreSQL
* MacOS: `brew install postgresql`. See also [here](#PostgreSQL on MacOS).
* Windows: https://www.postgresql.org/download/windows/
> Note: If password is empty, set `spring.r2dbc.password=""`.
```properties
#spring.r2dbc.url=r2dbc:pool:postgresql://127.0.0.1:5432/demo
#spring.r2dbc.username=user
#spring.r2dbc.password=""
```

### Flyway
R2DBC and Flyway are not compatible yet. Workaround is to execute Flyway migration using Maven Flyway plugin.

Sample command: 
```
mvn flyway:migrate -Dflyway.url=jdbc:mysql://127.0.0.1:3306/demo -Dflyway.user=user -Dflyway.password=password
```

The Flyway scripts prepared in this demo project includes:
* Creating a `pets` table.
* Inserting two records to the table: `dog`, `cat`.

## Getting Started
Before proceeding, make sure you have:
* Created empty database schema, name it `demo`.
* Executed Flyway migration using Maven command. See [Flyway](#Flyway).

### Run the application
You can run it using Maven command below or through your IDE (e.g. IntelliJ).

The application will run at port 8080.
 
```
mvn spring-boot:run
```

### Try it out
Call http://localhost:8080/v1/demo/pets/{petName} using cURL or HTTPie to try it out.

You can also manually insert new records to the database and use it in your request. 

#### Using cURL
```
$ curl -H"Accept:application/json" -H"Content-Type:application/json" http://localhost:8080/v1/demo/pets/dog
woof

$ curl -H"Accept:application/json" -H"Content-Type:application/json" http://localhost:8080/v1/demo/pets/cat
meow
```
#### Using HTTPie
```
$ http -j :8080/v1/demo/pets/dog
HTTP/1.1 503 Service Unavailable
Content-Length: 4
Content-Type: application/json

woof

$ http -j :8080/v1/demo/pets/cat
HTTP/1.1 503 Service Unavailable
Content-Length: 4
Content-Type: application/json

meow
```

### Appendix
#### PostgreSQL on MacOS
* Install: `brew install postgresql`
* Run: `brew services start postgresql`
* Initialization: `createdb db_name`

