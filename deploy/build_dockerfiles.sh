#!/usr/bin/env bash

echo '=== Please execute this from the main project directory!!! ==='

echo '=== Building jar ==='
sbt assembly

echo '=== Copying jars ==='
cp target/scala-2.12/K8STest-assembly-0.1.jar deploy/docker/

echo '=== Building docker images ==='
docker build -t "$1"/k8stest deploy/docker/

echo '=== Cleaning up ==='
rm deploy/docker/K8STest-assembly-0.1.jar

echo '=== Pushing docker images ==='
docker push "$1"/k8stest