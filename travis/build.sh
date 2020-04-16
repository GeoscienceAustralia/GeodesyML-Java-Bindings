#!/usr/bin/env bash

#if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    nix-shell --command "mvn --update-snapshots deploy"
#else
#    nix-shell --command "mvn --update-snapshots test"
#fi
