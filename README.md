# GitHub client app

---

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Launch](#launch)

## General info
This project is a simple web application that provides selected information about GitHub users' repositories that are not forks:
* repository name
* owner login
* for each branch it’s name and last commit sha

Resources can be accessed by the endpoint: `GET /repos` (requires `username` request param). 
The application supports the `Authorization` header (but this header is NOT required).

## Technologies
Project is created with:
* Java 21
* Spring Boot 3.3.2
* Maven
* https://developer.github.com/v3 as a backing API


## Launch
Java 21 is required. To run this project, you can install `maven` and then navigate to the root of the project via command line 
and execute the command `mvn spring-boot:run`. You can also use an IDE (e.g. Intellij). 

Then use the endpoint ```GET http://localhost:8080/repos?username=``` (fill in the parameter value).
