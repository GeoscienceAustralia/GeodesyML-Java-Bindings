#!/usr/bin/env bash

nix-shell --command "mvn -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true --settings ./maven-settings.xml deploy"
