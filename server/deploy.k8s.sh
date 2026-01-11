#!/usr/bin/bash

# defining the colors
YELLOW='\e[33m'
BLUE='\e[34m'
RED='\e[31m'
NC='\e[0m'

cd $HOME
SERVER_DIR="$HOME/DeepDame/server"

if [[ " $* " == *" --observability "* ]]; then
    echo -e "$\n\n${RED}Observability mode enabled${NC}"
    echo -e "${YELLOW}NOTICE: ${NC} this shit takes too much resources"
    mv ./DeepDame/src/main/resources/slogback-spring.xml ./DeepDame/src/main/resources/logback-spring.xml
else
    echo -e "${RED}No, observability${NC}"
fi

if [[ " $* " == *" --ai "* ]]; then
    echo -e "$\n\n${RED}AI mode enabled${NC}"
    echo -e "${YELLOW}NOTICE: ${NC} this shit takes too much fucking resources"
fi

if [ -d "DeepDame" ]; then
    echo -e "\n\nPulling ${BLUE}DeepDame${NC}"
    cd $SERVER_DIR
    git stash
    git pull origin main
else
    echo -e "\n\nCloning ${BLUE}DeepDame${NC}"
    git clone git@github.com:Ilyass-Bougati/DeepDame.git
    cd $SERVER_DIR
fi

# building the image
echo -e "$\n\n${BLUE}Building docker image${NC}"
eval $(minikube docker-env)
docker build -t deepdame .

# applying the deployment
echo -e "$\n\n${BLUE}Apply k8s deployment${NC}"
kubectl apply -R -f k8s/app/
kubectl apply -R -f k8s/cache/
kubectl apply -R -f k8s/db/

if [[ " $* " == *" --observability "* ]]; then
    kubectl apply -R -f k8s/observability/
fi

if [[ " $* " == *" --ai "* ]]; then
    kubectl apply -R -f k8s/ai/
fi

# forwarding the ports
echo -e "$\n\n${BLUE}Port forwarding${NC}"
./port.forward.sh
