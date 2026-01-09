#!/usr/bin/bash

cd $HOME
SERVER_DIR="$HOME/DeepDame/server"

if [ -d "DeepDame" ]; then
    cd $SERVER_DIR
    git pull origin main
else
    git clone git@github.com:Ilyass-Bougati/DeepDame.git
    cd $SERVER_DIR
fi

# building the image
eval $(minikube docker-env)
docker build -t deepdame .

# applying the deployment
kubectl apply -R -f k8s/app/
kubectl apply -R -f k8s/cache/
kubectl apply -R -f k8s/db/ 
