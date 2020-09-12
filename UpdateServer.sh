#!/usr/bin/env sh

git pull
/root/QN_Server/gradlew shadowJar
java -jar /root/QN_Server/build/libs/QN_Server-0.0.1.jar