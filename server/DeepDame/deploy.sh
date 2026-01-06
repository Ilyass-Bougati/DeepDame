#!/bin/bash

# Define your port here
PORT=8080
APP_DIR="/home/ilyass/DeepDame/server/DeepDame"

# 2. Go to directory
cd $APP_DIR

# 3. Pull code
echo "Pulling main..."
git pull origin main

# 4. Run
echo "Deploying..."
docker compose up -d
echo "Deployment command issued."

