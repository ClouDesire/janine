# janine [![Build Status](https://travis-ci.org/ClouDesire/janine.svg)](https://travis-ci.org/ClouDesire/janine)
Janine is your sexy generator and archiver of PDF invoices.

## API

###validate

  Validates an invoice body

* **URL:**

  `/{prefix}/validate`

* **Method:**

  `POST`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

* **Request Body**

  ```
  {
      "header": "ClouDesire.com",
      "holder": {
          "address": {
              "address": "address",
              "city": "city",
              "country": "country",
              "state": "state",
              "zip": "zip"
          },
          "companyName": "ClouDesire",
          "email": "antanio@divani.me",
          "firstName": "Antanio",
          "lastName": "Divani",
          "taxCode": "123CLOUDESIRE"
      },
      "lines": [
          {
              "description": "Custom vendor fee",
              "price": {
                  "currency": "EUR",
                  "price": "100.0000",
                  "vat": 22.0
              },
              "quantity": 2.0,
              "unit": "un"
          },
          {
              "description": "Another custom vendor fee",
              "price": {
                  "currency": "EUR",
                  "price": "23.4567",
                  "vat": 22.0
              },
              "quantity": 1.0,
              "unit": "sm"
          }
      ],
      "notes": "Lorem invoice",
      "recipient": {
          "address": {
              "address": "address",
              "city": "city",
              "country": "country",
              "state": "state",
              "zip": "zip"
          },
          "email": "brebuzio@sfanti.me",
          "firstName": "Brebuzio",
          "lastName": "Sfanti"
      }
  }
  ```

* **Success Response:**

  * **Code:** 200 OK
  * **Content:**
    
    ```
    {
        "date": 1451488888354,
        "header": "ClouDesire.com",
        "holder": {
            "address": {
                "address": "address",
                "city": "city",
                "country": "country",
                "state": "state",
                "zip": "zip"
            },
            "companyName": "ClouDesire",
            "email": "antanio@divani.me",
            "firstName": "Antanio",
            "lastName": "Divani",
            "phoneNumber": null,
            "taxCode": "123CLOUDESIRE"
        },
        "lines": [
            {
                "description": "Custom vendor fee",
                "price": {
                    "currency": "EUR",
                    "price": 100.0,
                    "total": 122.0,
                    "vat": 22.0
                },
                "quantity": 2.0,
                "unit": "un"
            },
            {
                "description": "Another custom vendor fee",
                "price": {
                    "currency": "EUR",
                    "price": 23.4567,
                    "total": 28.6172,
                    "vat": 22.0
                },
                "quantity": 1.0,
                "unit": "sm"
            }
        ],
        "notes": "Lorem invoice",
        "number": "TEST1",
        "recipient": {
            "address": {
                "address": "address",
                "city": "city",
                "country": "country",
                "state": "state",
                "zip": "zip"
            },
            "companyName": null,
            "email": "brebuzio@sfanti.me",
            "firstName": "Brebuzio",
            "lastName": "Sfanti",
            "phoneNumber": null,
            "taxCode": null
        },
        "total": 272.62
    }
  ```

* **Error Response:**

  If a validation error occurs:

  * **Code:** 400 Bad Request
  * **Content:**
  
  ```
    {
        "error": "Bad Request",
        "errors": [
            {
                "arguments": [
                    {
                        "arguments": null,
                        "code": "holder.taxCode",
                        "codes": [
                            "invoice.holder.taxCode",
                            "holder.taxCode"
                        ],
                        "defaultMessage": "holder.taxCode"
                    }
                ],
                "bindingFailure": false,
                "code": "NotEmpty",
                "codes": [
                    "NotEmpty.invoice.holder.taxCode",
                    "NotEmpty.holder.taxCode",
                    "NotEmpty.taxCode",
                    "NotEmpty.java.lang.String",
                    "NotEmpty"
                ],
                "defaultMessage": "may not be empty",
                "field": "holder.taxCode",
                "objectName": "invoice",
                "rejectedValue": null
            },
            {
                "arguments": [
                    {
                        "arguments": null,
                        "code": "holder.companyName",
                        "codes": [
                            "invoice.holder.companyName",
                            "holder.companyName"
                        ],
                        "defaultMessage": "holder.companyName"
                    }
                ],
                "bindingFailure": false,
                "code": "NotEmpty",
                "codes": [
                    "NotEmpty.invoice.holder.companyName",
                    "NotEmpty.holder.companyName",
                    "NotEmpty.companyName",
                    "NotEmpty.java.lang.String",
                    "NotEmpty"
                ],
                "defaultMessage": "may not be empty",
                "field": "holder.companyName",
                "objectName": "invoice",
                "rejectedValue": null
            }
        ],
        "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
        "message": "Validation failed for object='invoice'. Error count: 2",
        "path": "/TEST/validate",
        "status": 400,
        "timestamp": 1451488346664
    }
  ```

* **Sample Call:**

  `$ http POST :8080/TEST/validate < invoice.json`


## server
A spring boot application that expose a REST API, use Redis to maintain counters of the generated invoices, and upload them to a jclouds-supported object storage (tested with Rackspace CloudFiles, but AWS S3, Azure Blob, OpenStack Swift, Atmos should work too).

```
docker run \
  -e BLOB_PROVIDER=rackspace-cloudfiles-uk \
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
