# mini-search-engine
Mini Search Engine - Advanced Programming Techniques Course Project - Cairo Uni.

# Requirements

| Binary | Version  |
|--------|----------|
| maven  | `3.6.0`  |
| java   | `11.0.3` |
| javac  | `11.0.3` |
| nodejs | `8.11.4` |
| yarn   | `1.21.1` |

# Install Other Requirement

``` 
$ mvn clean install
$ yarn install
```

# Build Frontend

`$ yarn build` 

# Run Server

`$ mvn` 

Default port `server.port` is set in `src/main/resources/application.properties` .

> To choose the port, run: 
> 1. `$ mvn -Dspring-boot.run.arguments="--server.port=<PORT>"` 
> 1. `$ npx webpack --env.BASE_PATH=localhost:<PORT>` 
>  
> where `<PORT>` is the port to bind to.

# Run Tests

`$ mvn test` 

