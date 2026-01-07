#!/usr/bin/bash

APP_DIR=/home/ilyass/DeepDame/server/
cd $APP_DIR

# pulling main
git pull origin main

# building the image
eval $(minikube docker-env)

docker build -t deepdame $APP_DIR

# applying the deployment
kubectl apply -R -f $APP_DIR/k8s/