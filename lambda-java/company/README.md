## CompanyLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre las compañías.

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- createCompany: Crea una nueva compañía con los datos proporcionados.
- getCompanies: Lista todas las compañías existentes.
- getCompanyById: Obtiene los detalles de una compañía mediante su ID.
- updateCompany: Actualiza los campos editables de una compañía existente.
- deleteCompany: Elimina una compañía por su ID.
- Activaciones y cambios de estado: Permite activar, desactivar, suspender o marcar como pendiente una compañía.

---

## 🧾 Estructura de la Entidad `Company`

| **Campo**           | **Tipo**      | **Restricciones**                                            | **Descripción** |
| ------------------- | ------------- | ------------------------------------------------------------ | --------------- |
| id                  | UUID          | Generado automáticamente                                     |                 |
| taxId               | String        | Requerido. Máx. 20 caracteres                                |                 |
| name                | String        | Requerido. Entre 1 y 100 caracteres                          |                 |
| businessName        | String        | Requerido. Entre 1 y 100 caracteres                          |                 |
| companyType         | String        | Opcional. Máx. 50 caracteres                                 |                 |
| address             | String        | Opcional. Máx. 150 caracteres                                |                 |
| phoneNumber         | String        | Opcional. Máx. 20 caracteres. Formato internacional          |                 |
| email               | String        | Requerido. Máx. 100 caracteres. Formato email válido         |                 |
| legalRepresentative | String        | Opcional. Máx. 100 caracteres                                |                 |
| city                | String        | Requerido. Entre 1 y 50 caracteres                           |                 |
| state               | String        | Requerido. Entre 1 y 50 caracteres                           |                 |
| country             | String        | Requerido. 2 caracteres mayúsculas (código ISO)              |                 |
| createdAt           | LocalDateTime | Se asigna automáticamente al crear                           |                 |
| updatedAt           | LocalDateTime | Se actualiza automáticamente al modificar                    |                 |
| status              | String (ENUM) | Valores: `active`, `inactive`, `pending`, `suspended`        |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── company/
    ├── dto/
    │   ├── request/     <- DTOs de entrada (CreateCompanyRequestDTO, UpdateCompanyRequestDTO)
    │   └── response/    <- DTOs de salida (CompanyResponseDTO, DeleteResponseDTO)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (Company, CompanyStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── validadation/    <- validaciones
    └── test/            <- Pruebas automatizadas
```

---

## ⚙️ Validaciones

El sistema valida:

### Validaciones de Campos Obligatorios:
- **taxId**: Requerido, máximo 20 caracteres
- **name**: Requerido, entre 1 y 100 caracteres
- **businessName**: Requerido, entre 1 y 100 caracteres
- **email**: Requerido, formato de email válido, máximo 100 caracteres
- **city**: Requerido, entre 1 y 50 caracteres
- **state**: Requerido, entre 1 y 50 caracteres
- **country**: Requerido, 2 caracteres mayúsculas (código ISO)

### Validaciones de Formato:
- **phoneNumber**: Formato internacional (+país + número), máximo 20 caracteres
- **email**: Formato estándar de email con validación de dominio
- **country**: Código ISO de 2 caracteres en mayúsculas

### Validaciones de Negocio:
- Unicidad de `taxId` (NIT) por compañía
- Existencia de la compañía antes de actualizaciones
- Estados válidos según el enum `CompanyStatus`
- Restricciones específicas por operación (crear vs actualizar)

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`.

- Casos exitosos de cada operación CRUD
- Validaciones fallidas con diferentes escenarios
- Excepciones de base de datos y conectividad
- Casos límite en longitud de campos
- Validaciones de formato (email, teléfono, país)

---

## 🧰 Tecnologías Utilizadas

- **Java 21**
- **AWS Lambda**
- **JDBC / PostgreSQL**
- **Maven**
- **Jackson** (JSON serialization/deserialization)
- **Java Time API (`LocalDateTime`)**
- **Patrón Builder + DTOs**
- **Connection Pool Manager**
- **Validación personalizada**

---

## **📥 Ejemplos de Peticiones y Respuestas**

### **🔹 createCompany "POST /company"**

**JSON de entrada:**

```json
{
  "taxId": "900123456-7",
  "name": "TechCorp Solutions",
  "businessName": "TechCorp Solutions S.A.S.",
  "companyType": "SAS",
  "address": "Carrera 15 #93-47",
  "phoneNumber": "+571234567890",
  "email": "info@techcorp.com",
  "legalRepresentative": "Juan Pérez",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "CO",
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "taxId": "900123456-7",
  "name": "TechCorp Solutions",
  "businessName": "TechCorp Solutions S.A.S.",
  "companyType": "SAS",
  "address": "Carrera 15 #93-47",
  "phoneNumber": "+571234567890",
  "email": "info@techcorp.com",
  "legalRepresentative": "Juan Pérez",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "CO",
  "createdAt": "2025-07-17T16:05:41.333603829",
  "updatedAt": "2025-07-17T16:05:41.333603829",
  "status": "active"
}
```

### **🔹 getCompanies "GET /company"**

**JSON de entrada:**

```json
{
}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "taxId": "900123456-7",
    "name": "TechCorp Solutions",
    "businessName": "TechCorp Solutions S.A.S.",
    "companyType": "SAS",
    "address": "Carrera 15 #93-47",
    "phoneNumber": "+571234567890",
    "email": "info@techcorp.com",
    "legalRepresentative": "Juan Pérez",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "CO",
    "createdAt": "2025-07-17T16:05:41.333604000",
    "updatedAt": "2025-07-17T16:05:41.333604000",
    "status": "active"
  }
]
```

### **🔹 getCompanyById "GET /company/{id}"**

**JSON de entrada:**

```json
{
  "pathParameters": {
    "id": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "taxId": "900123456-7",
  "name": "TechCorp Solutions",
  "businessName": "TechCorp Solutions S.A.S.",
  "companyType": "SAS",
  "address": "Carrera 15 #93-47",
  "phoneNumber": "+571234567890",
  "email": "info@techcorp.com",
  "legalRepresentative": "Juan Pérez",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "CO",
  "createdAt": "2025-07-17T16:05:41.333604000",
  "updatedAt": "2025-07-17T16:05:41.333604000",
  "status": "active"
}
```

### **🔹 updateCompany "PUT /company/{id}"**

**JSON de entrada:**

```json
{
  "pathParameters": {
    "id": "550e8400-e29b-41d4-a716-446655440000"
  },
  "body": {
    "taxId": "900123456-7",
    "name": "TechCorp Solutions Pro",
    "businessName": "TechCorp Solutions Professional S.A.S.",
    "companyType": "SAS",
    "address": "Carrera 15 #93-47 Piso 10",
    "phoneNumber": "+571234567891",
    "email": "contact@techcorp.com",
    "legalRepresentative": "María González",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "CO",
    "status": "active"
  }
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "taxId": "900123456-7",
  "name": "TechCorp Solutions Pro",
  "businessName": "TechCorp Solutions Professional S.A.S.",
  "companyType": "SAS",
  "address": "Carrera 15 #93-47 Piso 10",
  "phoneNumber": "+571234567891",
  "email": "contact@techcorp.com",
  "legalRepresentative": "María González",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "CO",
  "createdAt": "2025-07-17T16:05:41.333604000",
  "updatedAt": "2025-07-17T16:12:47.113913334",
  "status": "active"
}
```

### **🔹 deleteCompany "DELETE /company/{id}"**

**JSON de entrada:**

```json
{
  "pathParameters": {
    "id": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "Company successfully deactivated",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "success": true,
  "timestamp": "2025-07-17T16:15:30.123456789"
}
```

---

## 📌 Consideraciones Finales

- El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- La arquitectura favorece separación de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- Se incluyen validaciones robustas tanto a nivel de aplicación como de base de datos.
- El sistema maneja adecuadamente la internacionalización con campos como `country` usando códigos ISO.
- Las operaciones de eliminación son lógicas (cambio de estado) preservando la integridad histórica de los datos.
- Se implementa logging detallado para monitoreo y debugging en el entorno AWS Lambda.
- El pool de conexiones se monitorea continuamente para garantizar el rendimiento y la disponibilidad.
- Los IDs se generan automáticamente como UUID para garantizar unicidad.
- El campo `taxId` se usa como identificador de negocio único (NIT).
- Los timestamps se manejan en formato ISO 8601 para compatibilidad con diferentes sistemas.