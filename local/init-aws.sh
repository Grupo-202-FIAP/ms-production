#!/bin/bash

set -e

echo "########### Criando filas SQS no LocalStack ###########"

AWS_REGION="us-east-1"
ENDPOINT_URL="http://localhost:4566"
PRODUCTION_QUEUE_NAME="production-queue"
PRODUCTION_CALLBACK_QUEUE_NAME="production-callback-queue"

aws --endpoint-url=${ENDPOINT_URL} sqs create-queue \
    --queue-name ${PRODUCTION_QUEUE_NAME} \
    --region ${AWS_REGION}

echo "Fila criada: ${PRODUCTION_QUEUE_NAME}"

aws --endpoint-url=${ENDPOINT_URL} sqs create-queue \
    --queue-name ${PRODUCTION_CALLBACK_QUEUE_NAME} \
    --region ${AWS_REGION}

echo "Fila criada: ${PRODUCTION_CALLBACK_QUEUE_NAME}"

echo "########### Filas criadas com sucesso ###########"

echo "########### Listando filas SQS ###########"
aws --endpoint-url=http://localhost:4566 sqs list-queues --region us-east-1


