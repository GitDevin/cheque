#!/bin/bash

#curl -v -H "Content-Type: application/json" -X PUT -d @cheque.json "http://localhost:8080/cheque/service/put"

#curl -v -X GET "http://localhost:8080/cheques/service/all"

#curl -v -X GET "http://localhost:8080/cheque/service/id/2"

#curl -v -X GET "http://localhost:8080/cheque/service/recipient/Sam"

#curl -v -X GET "http://localhost:8080/cheque/service/id/1"

curl -v -H "Content-Type: application/json" -X POST -d @money.json "http://localhost:8080/money/service/format"
