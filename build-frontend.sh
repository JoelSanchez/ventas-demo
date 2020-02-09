#!/usr/bin/env bash

npm install
shadow-cljs release app
shadow-cljs release admin
LEIN_SNAPSHOTS_IN_RELEASE=true lein uberjar