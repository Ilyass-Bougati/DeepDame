#!/usr/bin/bash

# defining the colors
BLUE='\e[34m'
NC='\e[0m'

cd $HOME
SERVER_DIR="$HOME/DeepDame/server"

if [ -d "DeepDame" ]; then
    echo -e "Pulling ${BLUE}DeepDame${NC}"
    cd $SERVER_DIR
    git stash
    git pull origin main
else
    echo -e "Cloning ${BLUE}DeepDame${NC}"
    git clone git@github.com:Ilyass-Bougati/DeepDame.git
    cd $SERVER_DIR
fi

# building the image
echo -e "${BLUE}Building docker image${NC}"
eval $(minikube docker-env)
docker build -t deepdame .

# applying the deployment
echo -e "${BLUE}Apply k8s deployment${NC}"
kubectl apply -R -f k8s/app/
kubectl apply -R -f k8s/cache/
kubectl apply -R -f k8s/db/

# forwarding the ports
echo -e "${BLUE}Port forwarding${NC}"
./port.forward.sh
