# API

* [validate](#validate)
* [generate](#generate)
* [generate with id](#generate-with-id)
* [download pdf](#download-pdf)
* [generate and download pdf](#generateanddownload)
* [download json](#download-json)
* [JSON Schema](#json-schema)

### Authentication (lack of)

No authentication or authorization feature is implemented, and never will be.

### validate

  Validates an invoice body

* **URL:**

  `/{prefix}/validate`

* **Method:**

  `POST`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

* **Request Body**

  ```json
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
          "taxCode": "123CLOUDESIRE"
      },
      "currency": "EUR",
      "lines": [
          {
              "description": "Custom vendor fee",
              "price": {
                  "price": "100.0000",
                  "vat": 22.0
              },
              "quantity": 2.0,
              "unit": "un"
          },
          {
              "description": "Another custom vendor fee",
              "price": {
                  "price": "23.4567",
                  "vat": 22.0
              },
              "quantity": 1.0,
              "unit": "sm"
          }
      ],
      "notes": "Lorem invoice",
      "recipient": {
          "email": "antanio@divani.me",
          "firstName": "Antanio",
          "lastName": "Divani",
          "address": {
              "address": "address",
              "city": "city",
              "country": "country",
              "state": "state",
              "zip": "zip"
          }
      }
  }
  ```

* **Success Response:**

  * **Code:** 200 OK
  * **Content:**

    ```json
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
            "taxCode": "123CLOUDESIRE"
        },
        "currency": "EUR",
        "lines": [
            {
                "description": "Custom vendor fee",
                "price": {
                    "price": 100.0,
                    "total": 122.0,
                    "vat": 22,
                    "vatTotal": 22.0
                },
                "quantity": 2.0,
                "unit": "un"
            },
            {
                "description": "Another custom vendor fee",
                "price": {
                    "price": 23.4567,
                    "total": 28.6172,
                    "vat": 22,
                    "vatTotal": 5.1612
                },
                "quantity": 1.0,
                "unit": "sm"
            }
        ],
        "notes": "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\nMauris ultricies arcu in risus iaculis porttitor.",
        "number": "TEST1",
        "recipient": {
            "address": {
                "address": "address",
                "city": "city",
                "country": "country",
                "state": "state",
                "zip": "zip"
            },
            "email": "antanio@divani.me",
            "firstName": "Antanio",
            "lastName": "Divani"
        },
        "subTotal": 223.46,
        "total": 272.62,
        "vatPercentage": 49.16,
        "vatPercentageNumber": 22.0
    }
  ```

* **Error Response:**

  If a validation error occurs:

  * **Code:** 400 Bad Request
  * **Content:**

  ```json
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

### generate

  Generates a PDF for the given invoice, and upload it to a remote object storage

* **URL:**

  `/{prefix}`

* **Method:**

  `POST`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

* **Request Body**

  *See validate request body*

* **Success Response:**

  Returns the ID of the generated invoice in the response body and the URL of the generated
  PDF in the Location header.


  * **Code:** 201 Created
  * **Headers:** Location: http://localhost:8080/TEST/1.pdf
  * **Content:** `1`

* **Sample Call:**

  `$ http POST :8080/TEST < invoice.json`

### generate with id

  Generates a PDF for the given invoice and upload it to a remote object storage providing your own id

* **URL:**

  `/{prefix}/{id}`

* **Method:**

  `POST`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

   `id=[integer]` - The ID of the invoice to be generated

   **Optional:**

   `regenerate=[boolean]` - whether to override an eventually already generated invoice (defaults to false)

* **Request Body**

  *See validate request body*

* **Success Response:**

  Returns the ID of the generated invoice in the response body and the URL of the generated
  PDF in the Location header.


  * **Code:** 201 Created
  * **Headers:** Location: http://localhost:8080/TEST/1.pdf
  * **Content:** `1`

* **Error Response:**

  When providing the ID of an already generated invoice, if not regenerating:

  * **Code:** 409 Conflict
  * **Content:**

  ```json
  {
    "error": "Conflict",
    "exception": "com.liberologico.janine.exceptions.InvoiceExistingException",
    "message": "Invoice TEST1 already exists",
    "path": "/TEST/1",
    "status": 409,
    "timestamp": 1455721477374
  }
  ```

* **Sample Call:**

  `$ http POST :8080/TEST/1 < invoice.json`

### download pdf

  Downloads an already generated invoice

* **URL:**

  `/{prefix}/{id}.pdf`

* **Method:**

  `GET`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

   `id=[integer]` - The ID of the genereated invoice

* **Success Response:**

  * **Code:** 200 OK
  * **Content:** `***BINARY DATA***`

* **Error Response:**

  When providing a wrong ID:

  * **Code:** 404 Not Found
  * **Content:**

  ```json
  {
    "error": "Not Found",
    "exception": "com.liberologico.janine.exceptions.InvoiceMissingException",
    "message": "No message available",
    "path": "/TEST/999.json",
    "status": 404,
    "timestamp": 1451492908671
  }
  ```

* **Sample Call:**

  `$ http :8080/TEST/1.pdf`

### generateAndDownload

  Downloads an already generated invoice

* **URL:**

  `/{prefix}/download`

* **Method:**

  `POST`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

* **Request Body**

  *See validate request body*

* **Success Response:**

  * **Code:** 200 OK
  * **Content:** `***BINARY DATA***`

* **Sample Call:**

  `$ http POST :8080/TEST/download < invoice.json`

### download json

  Downloads an already generated invoice in JSON format

* **URL:**

  `/{prefix}/{id}.json`

* **Method:**

  `GET`

*  **URL Params**

   **Required:**

   `prefix=[string]` - The invoice numeration prefix

   `id=[integer]` - The ID of the genereated invoice

* **Success Response:**

  *See validate response*

* **Error Response:**

  When providing a wrong ID:

  * **Code:** 404 Not Found
  * **Content:**

  ```json
  {
    "error": "Not Found",
    "exception": "com.liberologico.janine.exceptions.InvoiceMissingException",
    "message": "No message available",
    "path": "/TEST/999.json",
    "status": 404,
    "timestamp": 1451492908671
  }
  ```

* **Sample Call:**

  `$ http GET :8080/TEST/1.json`

### JSON Schema

  Gets a [JSON Schema](http://json-schema.org/) of the invoice object

* **URL:**

  `/schema`

* **Method:**

  `GET`

* **Success Response:**

  ```json
  {
    "id": "urn:jsonschema:com:liberologico:janine:entities:Invoice",
    "properties": {
        "currency": {
            "pattern": "[\\w]{3}",
            "required": true,
            "type": "string"
        },
        "date": {
            "format": "UTC_MILLISEC",
            "type": "integer"
        },
        "header": {
            "required": true,
            "type": "string"
        },
        "holder": {
            "id": "urn:jsonschema:com:liberologico:janine:entities:Holder",
            "properties": {
                "address": {
                    "id": "urn:jsonschema:com:liberologico:janine:entities:Address",
                    "properties": {
                        "address": {
                            "required": true,
                            "type": "string"
                        },
                        "city": {
                            "required": true,
                            "type": "string"
                        },
                        "country": {
                            "required": true,
                            "type": "string"
                        },
                        "state": {
                            "required": true,
                            "type": "string"
                        },
                        "zip": {
                            "required": true,
                            "type": "string"
                        }
                    },
                    "required": true,
                    "type": "object"
                },
                "companyName": {
                    "required": true,
                    "type": "string"
                },
                "email": {
                    "type": "string"
                },
                "firstName": {
                    "type": "string"
                },
                "lastName": {
                    "type": "string"
                },
                "phoneNumber": {
                    "type": "string"
                },
                "taxCode": {
                    "required": true,
                    "type": "string"
                }
            },
            "required": true,
            "type": "object"
        },
        "lines": {
            "items": {
                "id": "urn:jsonschema:com:liberologico:janine:entities:Line",
                "properties": {
                    "description": {
                        "required": true,
                        "type": "string"
                    },
                    "price": {
                        "id": "urn:jsonschema:com:liberologico:janine:entities:Price",
                        "properties": {
                            "VAT": {
                                "maximum": 99.99,
                                "required": true,
                                "type": "number"
                            },
                            "price": {
                                "required": true,
                                "type": "number"
                            },
                            "total": {
                                "type": "number"
                            },
                            "vat": {
                                "type": "number"
                            }
                        },
                        "required": true,
                        "type": "object"
                    },
                    "quantity": {
                        "minimum": 0.00,
                        "required": true,
                        "type": "number"
                    },
                    "unit": {
                        "required": true,
                        "type": "string"
                    }
                },
                "type": "object"
            },
            "required": true,
            "type": "array"
        },
        "notes": {
            "required": true,
            "type": "string"
        },
        "number": {
            "type": "string"
        },
        "recipient": {
            "id": "urn:jsonschema:com:liberologico:janine:entities:Recipient",
            "properties": {
                "address": {
                    "$ref": "urn:jsonschema:com:liberologico:janine:entities:Address",
                    "required": true,
                    "type": "object"
                },
                "companyName": {
                    "type": "string"
                },
                "email": {
                    "type": "string"
                },
                "firstName": {
                    "type": "string"
                },
                "lastName": {
                    "type": "string"
                },
                "phoneNumber": {
                    "type": "string"
                },
                "taxCode": {
                    "type": "string"
                }
            },
            "required": true,
            "type": "object"
        }
    },
    "type": "object"
  }
  ```

* **Sample Call:**

  `$ http GET :8080/schema`

