#!/bin/bash

set -e

echo "########### Criando filas SQS no LocalStack ###########"

AWS_REGION="us-east-1"
ENDPOINT_URL="http://localhost:4566"
ORDER_QUEUE_NAME="order-queue"
ORDER_CALLBACK_QUEUE_NAME="order-callback-queue"

aws --endpoint-url=${ENDPOINT_URL} sqs create-queue \
    --queue-name ${ORDER_QUEUE_NAME} \
    --region ${AWS_REGION}

echo "Fila criada: ${ORDER_QUEUE_NAME}"

aws --endpoint-url=${ENDPOINT_URL} sqs create-queue \
    --queue-name ${ORDER_CALLBACK_QUEUE_NAME} \
    --region ${AWS_REGION}

echo "Fila criada: ${ORDER_CALLBACK_QUEUE_NAME}"

echo "########### Filas criadas com sucesso ###########"

echo "########### Listando filas SQS ###########"
aws --endpoint-url=http://localhost:4566 sqs list-queues --region us-east-1


