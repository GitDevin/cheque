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

##Contact Information
Source code can be found at [https://github.com/gitdevin/cheque](https://github.com/gitdevin/cheque).

You can contact me by [email](mailto:dev.kun.yu.liu+chequedemo@gmail.com?Subject=Cheque%20demo).

###TODO
1. Javascript input validation
2. Health checks
3. monitoring
4. Explore Docker deployment
