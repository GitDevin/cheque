FROM mysql:5.6.30

RUN mkdir /db/

ADD init_db.sql privision.sh run_db.sh /db/

RUN /db/privision.sh

EXPOSE 3306

ENTRYPOINT "/db/run_db.sh"
