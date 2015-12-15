#!/bin/bash -ex
if [ -z $BUILD_NUMBER ]; then
    echo This should run under jenkins only
    exit 1
fi
docker login --email="jenkins@cloudesire.com" --password=$REGISTRY_PASSWORD --username=$REGISTRY_USERNAME $REGISTRY_HOST

mvn -e -U clean package

BASE_NAME=$REGISTRY_HOST"/invoice-api"
BUILD_VERSION=$BASE_NAME:$BUILD_NUMBER
BUILD_LATEST=$BASE_NAME:latest

docker build --pull --no-cache --force-rm -t $BUILD_VERSION .
docker push $BUILD_VERSION
docker tag -f $BUILD_VERSION $BUILD_LATEST
docker push $BUILD_LATEST
docker rmi $BUILD_VERSION $BUILD_LATEST
exit 0
