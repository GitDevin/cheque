#!/usr/bin/env bash
export $(cat ../../../../.env)
source ../../../../.env && java -jar ../../../target/cheque-service-*.jar server cheque.yml
