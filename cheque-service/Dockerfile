FROM java:openjdk-8

CMD mkdir /var/www/service

ADD target/cheque-service-1.0-SNAPSHOT.jar /var/www/service/

ADD ./src/main/resources/cheque.yml /var/www/service/

CMD java -jar /var/www/service/cheque-service-1.0-SNAPSHOT.jar server /var/www/service/cheque.yml
