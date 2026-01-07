#!/usr/bin/bash

# building the image
eval $(minikube docker-env)
docker build -t deepdame .

# applying the deployment
kubectl apply -R -f k8s/

# port-forwarding the services
kubectl port-forward --address 0.0.0.0 service/redis 6379:6379 &
kubectl port-forward --address 0.0.0.0 service/postgres 5432:5432 &
kubectl port-forward --address 0.0.0.0 service/mongo 27017:27017 &
kubectl port-forward --address 0.0.0.0 service/grafana 3000:3000 &
kubectl port-forward --address 0.0.0.0 service/deepdame 8080:8080 &

#running the dashboard
minikube dashboard --url &
kubectl port-forward --address 0.0.0.0 -n kubernetes-dashboard service/kubernetes-dashboard 9090:80 &