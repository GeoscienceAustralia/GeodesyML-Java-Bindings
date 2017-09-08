#!/usr/bin/env bash

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    nix-shell --command "mvn --update-snapshots --settings travis/maven-settings.xml deploy"
else
    nix-shell --command "mvn --update-snapshots --settings travis/maven-settings.xml test"
fi
