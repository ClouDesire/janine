# API

### Authentication (lack of)

Any authentication or authorization feature is implemented, and never will be.

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

### download

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

### download

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
