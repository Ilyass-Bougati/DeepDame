#!/usr/bin/bash

# port-forwarding the services
echo "Starting port forwards..."

nohup kubectl port-forward --address 0.0.0.0 service/redis 6379:6379 > logs/redis.log 2>&1 &
PID_REDIS=$!

nohup kubectl port-forward --address 0.0.0.0 service/postgres 5432:5432 > logs/postgres.log 2>&1 &
PID_PG=$!

nohup kubectl port-forward --address 0.0.0.0 service/mongo 27017:27017 > logs/mongo.log 2>&1 &
PID_MONGO=$!

nohup kubectl port-forward --address 0.0.0.0 service/grafana 3000:3000 > logs/grafana.log 2>&1 &
PID_GRAF=$!

nohup kubectl port-forward --address 0.0.0.0 service/deepdame 8080:8080 > logs/deepdame.log 2>&1 &
PID_APP=$!

minikube addons enable dashboard
nohup kubectl port-forward --address 0.0.0.0 -n kubernetes-dashboard service/kubernetes-dashboard 9090:80 > logs/dashboard.log 2>&1 &
PID_DASH=$!

echo "All services forwarded! Check logs/ folder for output."