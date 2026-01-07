#!/usr/bin/bash

# building the image
eval $(minikube docker-env)
docker build -t deepdame .

# applying the deployment
kubectl apply -R -f k8s/