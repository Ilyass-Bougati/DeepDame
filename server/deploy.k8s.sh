#!/usr/bin/bash

APP_DIR=/home/ilyass/DeepDame/server/
cd $APP_DIR

# pulling main
git pull origin main

# renaming logback-spring
# mv ./DeepDame/src/main/resources/slogback-spring.xml ./DeepDame/src/main/resources/logback-spring.xml

# building the image
eval $(minikube docker-env)
docker build -t deepdame .

# applying the deployment
kubectl apply -R -f k8s/app/
kubectl apply -R -f k8s/cache/
kubectl apply -R -f k8s/db/ 
