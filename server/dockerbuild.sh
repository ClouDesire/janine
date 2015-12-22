#!/bin/bash -ex
if [ -z $TRAVIS ]; then
    echo This should run under CI only
    exit 1
fi
docker login --email="dev@cloudesire.com" --password=$REGISTRY_PASSWORD --username=$REGISTRY_USERNAME

BASE_NAME=cloudesire/janine
BUILD_VERSION=$BASE_NAME:$TRAVIS_BUILD_NUMBER
BUILD_LATEST=$BASE_NAME:latest

docker build --pull --no-cache --force-rm -t $BUILD_VERSION .
docker push $BUILD_VERSION
docker tag -f $BUILD_VERSION $BUILD_LATEST
docker push $BUILD_LATEST
docker rmi $BUILD_VERSION $BUILD_LATEST
exit 0
