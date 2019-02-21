#!/usr/bin/env bash

set -e

echo "Installing artifacts..."
cd ../ventas-core
lein install
cd ../ventas-stripe-plugin
lein install
cd ../ventas-slider-plugin
lein install
cd ../ventas-clothing-theme
lein install
cd ../ventas-demo

echo "Building..."
lein clean
shadow-cljs release app
shadow-cljs release admin
lein uberjar

echo "Creating and pushing docker images"
cp target/uberjar/ventas.jar deploy
docker build -t registry.gitlab.com/joelsanchez/ventas-demo/ventas .
docker push registry.gitlab.com/joelsanchez/ventas-demo/ventas

echo "Deploying"
kubectl delete -f deploy/k8s/ventas-deployment.yaml
kubectl create -f deploy/k8s/ventas-deployment.yaml
