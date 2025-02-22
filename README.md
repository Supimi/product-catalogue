# Product Catalogue API

## Overview
This project includes a product catalogue API that manages products in different categories along with their details.

The application is integrated with **Keycloak** for authentication and authorization, implementing **Role-Based Access Control (RBAC)**:
- **Admin Role:** Can invoke any API without restriction.
- **User Role:** Can only view products and product catalogs.

## Services
The application consists of the following services:

1. **catalogue-service** - Manages product-related operations and publishes product creation events to the Kafka topic `product-topic`.
2. **notification-service** - Consumes Kafka product creation events and sends notifications to relevant authorities (currently logs messages to the console).
3. **edge-service** - A **Spring Cloud Gateway** configured with Keycloak as the OAuth2 client.

The required **database**, **Kafka cluster**, and **Keycloak instance** are initialized using a **Docker Compose** file. The `notification-service` is also configured to run inside a **Docker container** for testing purposes.

## API Endpoints
### Swagger Documentation
- URL: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

### Sample Requests
#### 1. Create Product
```sh
curl --location 'http://localhost:8081/api/products' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <token>' \
--data '{
  "name": "hand bag - 2",
  "description": "bag in different colours - red, black, brown, white",
  "price": 2500.0,
  "category": "fashion"
}'
```

#### 2. Update Existing Product
```sh
curl --location --request PATCH 'http://localhost:8081/api/products/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <token>' \
--data '{
  "name":"cross body bag",
  "description":"cross body bag in multiple colours - red, black, pink",
  "price": 2500.0
}'
```

#### 3. Delete Product
```sh
curl --location --request DELETE 'http://localhost:8081/api/products/1' \
--header 'Authorization: Bearer <token>'
```

#### 4. Retrieve All Products in Given Category
```sh
curl --location --request GET 'http://localhost:8081/api/products/category/fashion' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <token>'
```

#### 5. Retrieve All Premium Products
```sh
curl --location 'http://localhost:8081/api/products/premium' \
--header 'Authorization: Bearer <token>'
```

These endpoints can also be accessed via the gateway: `http://localhost:9000`

## Start the API
### Steps to Run the Application
1. **Build the project** (Run this command in the project's root directory):
   ```sh
   sh build.sh
   ```
2. **Start required services** using Docker Compose:
   ```sh
   docker-compose up -d
   ```
3. **Start catalogue-service:**
   ```sh
   cd catalogue-service
   mvn spring-boot:run
   ```
4. **Start edge-service:**
   ```sh
   cd edge-service
   mvn spring-boot:run
   ```

