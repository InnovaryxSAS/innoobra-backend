## AttributeLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre los atributos de productos.

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- **createAttribute**: Crea un nuevo atributo con los datos proporcionados.
- **getAttributes**: Lista todos los atributos existentes.
- **getAttributeById**: Obtiene los detalles de un atributo mediante su ID.
- **updateAttribute**: Actualiza los campos editables de un atributo existente.
- **deleteAttribute**: Elimina un atributo por su ID.
- **Activaciones y cambios de estado**: Permite activar, desactivar un atributo.

---

## 🧾 Estructura de la Entidad `Attribute`

| **Campo**    |    **Tipo**    |     **Restricciones**                     | **Descripción** |
| ------------ | -------------- | ----------------------------------------- | --------------- |
| idAttribute  | String         | Requerido. Máx. 255 caracteres            |                 |
| idCompany    | String         | Requerido. Máx. 255 caracteres            |                 |
| code         | String         | Requerido. 1-50 caracteres                |                 |
| name         | String         | Requerido. 1-100 caracteres               |                 |
| description  | String         | Opcional. Máx. 500 caracteres             |                 |
| unit         | String         | Opcional. 1-20 caracteres                 |                 |
| createdAt    | LocalDateTime  | Se asigna automáticamente al crear        |                 |
| updatedAt    | LocalDateTime  | Se actualiza automáticamente al modificar |                 |
| status       | String (ENUM)  | Valores: `active`, `inactive`,            |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── attribute/
    ├── dto/
    │   ├── request/     <- DTOs para peticiones (CreateAttributeRequestDTO, UpdateAttributeRequestDTO)
    │   └── response/    <- DTOs para respuestas (AttributeResponseDTO, DeleteResponseDTO)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (Attribute, AttributeStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── validation/      <- Validaciones y grupos de validación

```

---

## ⚙️ Validaciones

El sistema valida:

- **Formato y longitud de campos** según las restricciones definidas.
- **Existencia de valores obligatorios** en todos los campos requeridos.
- **Unicidad de `idAttribute`** por compañía.
- **Longitud máxima de descripción** (500 caracteres).
- **Formato válido del estado** (`status`).
- **Restricciones específicas por operación** (crear vs actualizar).

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`. 

- Casos exitosos de cada operación.
- Validaciones fallidas.
- Excepciones de base de datos.
- Manejo de atributos no encontrados.

---

## 🧰 Tecnologías Utilizadas

- **Java 21**
- **AWS Lambda**
- **JDBC / PostgreSQL**
- **Maven**
- **Jackson (JSON processing)**
- **Jakarta Validation**
- **Java Time API (`LocalDateTime`)**
- **Patrón Builder + DTOs**
- **Connection Pool Management**
- **SLF4J Logging**

---

## **📥 Ejemplos de Peticiones y Respuestas**

### **🔹 createAttribute "POST /attributes"**

**JSON de entrada:**

```json
{
  "idAttribute": "ATTR001",
  "idCompany": "COMP001",
  "code": "PESO",
  "name": "Peso",
  "description": "Peso del producto en kilogramos",
  "unit": "kg",
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "idAttribute": "ATTR001",
  "idCompany": "COMP001",
  "code": "PESO",
  "name": "Peso",
  "description": "Peso del producto en kilogramos",
  "unit": "kg",
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "status": "active"
}
```

### **🔹 getAttributes "GET /attributes"**

**JSON de entrada:**

```json
{}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "idAttribute": "ATTR001",
    "idCompany": "COMP001",
    "code": "PESO",
    "name": "Peso",
    "description": "Peso del producto en kilogramos",
    "unit": "kg",
    "createdAt": [2025, 7, 3, 15, 30, 0, 0],
    "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
    "status": "active"
  }
]
```

### **🔹 getAttributeById "GET /attributes/{id}"**

**Parámetros de ruta:**
- `id`: ATTR001

**Respuesta (`statusCode: 200`):**

```json
{
  "idAttribute": "ATTR001",
  "idCompany": "COMP001",
  "code": "PESO",
  "name": "Peso",
  "description": "Peso del producto en kilogramos",
  "unit": "kg",
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "status": "active"
}
```

### **🔹 updateAttribute "PUT /attributes/{id}"**

**Parámetros de ruta:**
- `id`: ATTR001

**JSON de entrada:**

```json
{
  "code": "PESO_KG",
  "name": "Peso en Kilogramos",
  "description": "Peso del producto expresado en kilogramos - Actualizado",
  "unit": "kg",
  "status": "active"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "idAttribute": "ATTR001",
  "idCompany": "COMP001",
  "code": "PESO_KG",
  "name": "Peso en Kilogramos",
  "description": "Peso del producto expresado en kilogramos - Actualizado",
  "unit": "kg",
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 16, 45, 30, 0],
  "status": "active"
}
```

### **🔹 deleteAttribute "DELETE /attributes/{id}"**

**Parámetros de ruta:**
- `id`: ATTR001

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "Attribute successfully deactivated",
  "attributeId": "ATTR001",
  "success": true
}
```

---

## 📌 Consideraciones Finales

- **Soft Delete**: La operación de eliminación desactiva el atributo (cambia status a `inactive`) en lugar de eliminarlo físicamente.
- **Connection Pool**: El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- **Logging**: Sistema de logging estructurado con contexto de request usando SLF4J.
- **Validación**: Validación robusta usando Jakarta Validation con grupos de validación específicos.
- **Arquitectura**: Separación clara de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- **Manejo de Errores**: Manejo centralizado de excepciones con respuestas HTTP apropiadas.
- **Timestamp Management**: Actualización automática de timestamps en todas las operaciones de modificación.
- **Builder Pattern**: Uso del patrón Builder para construcción flexible de objetos Attribute.
- **Unicidad por Compañía**: Los códigos de atributos son únicos dentro del contexto de cada compañía.