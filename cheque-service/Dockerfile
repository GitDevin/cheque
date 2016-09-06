FROM openjdk:8

CMD mkdir /var/www/service

ADD target/cheque-service-0.1.1-SNAPSHOT.jar /var/www/service/

ADD ./src/main/resources/cheque.yml /var/www/service/

CMD java -jar /var/www/service/cheque-service-0.1.1-SNAPSHOT.jar server /var/www/service/cheque.yml
