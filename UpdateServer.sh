#!/usr/bin/env sh

git pull
/root/QN_Server/gradlew shadowJar
systemctl restart QN_Server

