#!/bin/bash

# Define your port here
PORT=8080
APP_DIR="/home/ilyass/DeepDame/server/DeepDame"

# 1. Kill the existing process
echo "Checking for running application on port $PORT..."
PID=$(lsof -t -i:$PORT)

if [ -z "$PID" ]; then
  echo "No application running on port $PORT."
else
  echo "Killing process $PID..."
  kill -9 $PID
fi

# 2. Go to directory
cd $APP_DIR

# 3. Pull code
echo "Pulling main..."
git pull origin main

# 4. Run
echo "Deploying..."
nohup mvn spring-boot:run -DskipTests > logs 2>&1 &
echo "Deployment command issued."
