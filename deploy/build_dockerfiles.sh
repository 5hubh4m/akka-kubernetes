#!/usr/bin/env bash

export APP_VERSION=0.1
export DOCKER_USERNAME=$1

echo "=== Please execute this from the main project directory!!! ==="

echo '=== Building jar ==='
sbt assembly

echo '=== Copying jars ==='
cp target/scala-2.12/K8STest-assembly-"$APP_VERSION".jar deploy/Client/

echo '=== Building docker images ==='
docker build -t "$DOCKER_USERNAME"/k8s-client deploy/Client/
docker build -t "$DOCKER_USERNAME"/k8stest-manager deploy/Manager/

echo '=== Cleaning up ==='
rm deploy/Client/K8STest-assembly-"$APP_VERSION".jar

echo '=== Pushing docker images ==='
docker push "$DOCKER_USERNAME"/k8s-client
docker push "$DOCKER_USERNAME"/k8stest-manager
