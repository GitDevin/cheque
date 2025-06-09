FROM eclipse-temurin:17

ENV CHEQUE_VERSION=0.1.1-SNAPSHOT

CMD mkdir /var/www/service

ADD target/cheque-service-${CHEQUE_VERSION}.jar /var/www/service/

ADD ./src/main/resources/cheque.yml /var/www/service/

ENTRYPOINT exec java -jar /var/www/service/cheque-service-${CHEQUE_VERSION}.jar db migrate /var/www/service/cheque.yml&&java -jar /var/www/service/cheque-service-${CHEQUE_VERSION}.jar server /var/www/service/cheque.yml
