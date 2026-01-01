mkdir -p ./DeepDame/src/main/resources/certs
openssl genrsa -out ./DeepDame/src/main/resources/certs/keypair.pem 2048
openssl rsa -in ./DeepDame/src/main/resources/certs/keypair.pem -pubout -out ./DeepDame/src/main/resources/certs/public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in ./DeepDame/src/main/resources/certs/keypair.pem -out ./DeepDame/src/main/resources/certs/private.pem