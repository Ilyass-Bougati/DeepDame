#!/usr/bin/bash

eksctl create cluster \
  --name deepdame-prod \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.xlarge \
  --nodes 3 \
  --with-oidc \
  --managed \
  --addons aws-ebs-csi-driver

kubectl apply -f db/
kubectl apply -f cache/
kubectl apply -f app/
