#Cheque
Cheque is a simple application to demonstrate my ability to implement a micro-service and have it running on AWS.

Cheque allows users to create a cheque in the database. Then users can search for a single cheque by its ID or view all cheques paid to a particular recipient.

##Technology Stack
Cheque is implemented in **Groovy** with **Dropwizard** framework. Dependency management is handled by **Maven**. All instances on AWS share a common **MySQL** database on **AWS**. Cheque is compiled to be a single jar file and deployed by using **Boxfuse**.

Front-end of cheque is consist of **HTML** webpages using **Bootstrap** to get the desired look and feel. **jQuery** is used for input validation and making HTTP requests to back-end to create entries in the database.

Back-end of cheque is a REST server handles request made by the front-end or any other client. It's the only part of Cheque modifies **MySQL** database.

Cheque is designed to be a micro-service. More instances can be started according to demand. 

##Design
Cheque has two parts, front-end and backend.

Front-end is the webpages provide web interface allows users to enter require information to create a cheque and gives users a platform to view cheques. 

Back-end provides all functionality of Cheque without any UI. Information passed to and from Cheque is in JSON format. Back-end is designed to have front-ends on different platform as well. There can be an iOS app or an Android app to provide Cheque on different platforms.

##Test
`Cheque` is tested extensively to prove its code quality, to ensure further changes don't break existing API and behavior. There are kinds of tests implemented.

* **POJO unit tests**. POJO unit tests use `fixture` built-in `Dropwizard` to test de/serialization from/to JSON
* **DAO unit test**. DAO unit tests use `Flyway` database migration framework to setup pre-defined database and test queries against it.
* **REST endpoint unit test**. REST endpoint unit test use `ResourceTestRule` built-in `Dropwizard` and `Mockito` to test REST endpoint behaviour by created an instance of in-memory `Jersey` server with given resource to test against.
* **Integration test**. Integration test uses `DropwizardAppRule` built-in `Dropwizard` and `Flyway` to create an instance of the `Cheque` instance with pre-defined database to test against. 

##Docker (Running Locally)
`Cheque` uses `docker-compose` to run locally.

1. `mvn clean install` will build and package `Cheque` into a single jar file
2. `docker-compose up` will pull latest `MySQL` 5.7 image as database with log-in credential and port number defined in `.env` file. Then jar file will copy the jar to latest `openjdk` 8 image.
3. go to [http://localhost:`[SERVICE_PORT]`](http://localhost:8080/) where `SERVICE_PORT` is `8080` by default. It's defined in `.env` file.

##AWS (Deployment)
`Cheque` use [Boxfuse](https://boxfuse.com/) to deploy to AWS. 

##Contact Information
Source code can be found at [https://github.com/gitdevin/cheque](https://github.com/gitdevin/cheque).

You can contact me by [email](mailto:dev.kun.yu.liu+chequedemo@gmail.com?Subject=Cheque%20demo).

###TODO
1. Javascript input validation
2. Health checks
3. monitoring
4. Explore Docker deployment
