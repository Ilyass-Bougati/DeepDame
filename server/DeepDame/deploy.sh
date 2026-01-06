#!/bin/bash

cd /home/ilyass/DeepDame/server/DeepDame

echo "Pulling main"
git pull origin main

echo "Deploying..."
nohup mvn spring-boot:run -DskipTests > logs 2>&1 &
