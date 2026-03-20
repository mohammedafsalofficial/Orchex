#!/bin/bash
set -e

echo ">> Initial compile..."
mvn compile -q

echo ">> Starting watcher + Spring Boot..."
mvn fizzed-watcher:run &
mvn spring-boot:run \
  -Dspring-boot.run.fork=false \
  -Dspring-boot.run.jvmArguments="\
    -Dspring.devtools.restart.poll-interval=2000 \
    -Dspring.devtools.restart.quiet-period=1000"