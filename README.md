# Products Service

Microservicio encargado de gestionar el catálogo de productos.

Puerto: 8081

Endpoints principales:

POST /products  
GET /products/{id}

Incluye:

- Validación de SKU único
- Logs con Correlation ID
- Health checks con Actuator