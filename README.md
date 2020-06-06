# mini-search-engine
Mini Search Engine - Advanced Programming Techniques Course Project - Cairo Uni.

# Team #2

| Name                  | Section | B. N |
|-----------------------|---------|------|
| Evram Youssef         | 1       | 9    |
| Remonda Talaat        | 1       | 20   |
| Mahmoud Adas          | 2       | 21   |
| Mohamed Shawky        | 2       | 16   |

# Requirements
Versions listed here are the versions we worked with. 
Sometimes, it's possible to use lower versions (except jdk, it must be 9+), but we don't gurantee this.
In case of running/compiling issues, try to match those versions.

| Binary | Version  |
|--------|----------|
| maven  | `3.6.0`  |
| java   | `11.0.3` |
| javac  | `11.0.3` |
| nodejs | `8.11.4` |
| yarn   | `1.21.1` |

# Install Other Requirements

``` 
$ mvn clean compile
$ yarn install
```

# Build Frontend

`$ yarn build`

> To run dev server of the frontend at port 8081, run:
> `$ yarn start`

# Run Server

`$ mvn` 

Default port `server.port` is set in `src/main/resources/application.properties` .

> To choose the port, run: 
> 1. `$ mvn -Dspring-boot.run.arguments="--server.port=<PORT>"` 
> 1. `$ npx webpack --env.BASE_PATH=localhost:<PORT>` 
>  
> where `<PORT>` is the port to bind to.

> To run server as mock (results are fixed and are not real) for frontend experimenting, run:
> `$ env MOCK=1 mvn`

# Run Performance Analysis Module (PAM)
`$ env PAM=1 mvn`

# Run Tests

`$ mvn test` 

