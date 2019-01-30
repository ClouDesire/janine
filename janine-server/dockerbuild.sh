#!/bin/bash -e
if [ -z "$CIRCLECI" ]; then
    echo This should run under CI only
    exit 1
fi
echo "$REGISTRY_PASSWORD" | docker login \
    --username "$REGISTRY_USERNAME" \
    --password-stdin "$REGISTRY_HOST"

BUILD_NUMBER=$((200 + CIRCLE_BUILD_NUM))

BASE_NAME=cloudesire/janine
BUILD_VERSION=$BASE_NAME:$BUILD_NUMBER
BUILD_LATEST=$BASE_NAME:latest

docker build --pull --no-cache --force-rm -t $BUILD_VERSION .
docker push $BUILD_VERSION
docker tag $BUILD_VERSION $BUILD_LATEST
docker push $BUILD_LATEST
docker rmi $BUILD_VERSION $BUILD_LATEST
