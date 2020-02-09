#!/usr/bin/env bash

set -e

echo "Building..."
lein clean
LEIN_SNAPSHOTS_IN_RELEASE=true lein uberjar

echo "Creating and pushing docker images"
cp target/uberjar/ventas.jar deploy
docker build -t registry.gitlab.com/joelsanchez/ventas-demo/ventas deploy
docker push registry.gitlab.com/joelsanchez/ventas-demo/ventas

echo "Deploying"
kubectl delete -f deploy/k8s/ventas-deployment.yaml
kubectl create -f deploy/k8s/ventas-deployment.yaml
