# Threat detection API

Simple application based on SpringBoot and Java 17 which exposes a REST API.
This application takes threat detections and save them to the DB for the first time.

At the moment a previous detection is resolved, it is updated on the DB.

A device can also have 'fake detections'. These are periodical checks made on device and
do not have any business logic, only for statistics. 

We can retrieve a list of all detection, even the periodical ones based on criterias, such as deviceId,
date retrieved, detection status etc.

## Getting Started

Checkout the git repository

### Prerequisites

-- JAVA 17
-- MAVEN


### Installing

After checking out the project, you may need to install the dependencies

    mvn clean install

That should be all. After that we may start the application via our IDE or using cmd:

    mvn spring-boot:run


## Running the app and tests

You may test the rest functionalities via [swagger ui](http://localhost:8080/swagger-ui)



Unit and Integrations tests are located under src/test/java/org/thfabric/threatmanagement


## License

This project is licensed under the [CC0 1.0 Universal](LICENSE.md)
Creative Commons License - see the [LICENSE.md](LICENSE.md) file for
details
