# janine [![Build Status](https://travis-ci.org/ClouDesire/janine.svg)](https://travis-ci.org/ClouDesire/janine)
Janine is your sexy generator and archiver of PDF invoices.

## server
A spring boot application that expose a REST API, use Redis to maintain counters of the generated invoices, and upload them to Rackspace CloudFiles via JClouds.

```
docker run \
  -e BLOB_IDENTITY=username \
  -e BLOB_CREDENTIAL=apiKey \
  -e SPRING_REDIS_HOST=localhost \
  -e SPRING_REDIS_PORT=6379
  -e SERVER_PORT=8080 -p 8080:8080 \
  cloudesire/janine
```

A new version is pushed after each build: [available versions](https://hub.docker.com/r/cloudesire/janine/tags/)

## client
A simple java library to consume the server REST API.
