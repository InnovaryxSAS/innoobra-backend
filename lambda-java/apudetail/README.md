## ApuDetailLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre los detalles de APU (Análisis de Precios Unitarios).

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- **createApuDetail**: Crea un nuevo detalle de APU con los datos proporcionados.
- **getApuDetails**: Lista todos los detalles de APU existentes.
- **getApuDetailById**: Obtiene los detalles de un APU detail mediante su ID.
- **updateApuDetail**: Actualiza los campos editables de un detalle de APU existente.
- **deleteApuDetail**: Elimina un detalle de APU por su ID.
- **Activaciones y cambios de estado**: Permite activar, desactivar un detalle de APU.

---

## 🧾 Estructura de la Entidad `ApuDetail`

| **Campo**        |    **Tipo**    |     **Restricciones**                     | **Descripción** |
| ---------------- | -------------- | ----------------------------------------- | --------------- |
| idApuDetail      | String         | Requerido. Máx. 255 caracteres            |                 |
| idActivity       | String         | Requerido. Máx. 255 caracteres            |                 |
| idAttribute      | String         | Requerido. Máx. 255 caracteres            |                 |
| quantity         | Double         | Requerido. Valor >= 0                     |                 |
| wastePercentage  | Double         | Requerido. Valor entre 0 y 100            |                 |
| createdAt        | LocalDateTime  | Se asigna automáticamente al crear        |                 |
| updatedAt        | LocalDateTime  | Se actualiza automáticamente al modificar |                 |
| status           | String (ENUM)  | Valores: `active`, `inactive`             |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── apudetail/
    ├── dto/
    │   ├── request/     <- DTOs para peticiones (CreateApuDetailRequestDTO, UpdateApuDetailRequestDTO)
    │   └── response/    <- DTOs para respuestas (ApuDetailResponseDTO, DeleteResponseDTO)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (ApuDetail, ApuDetailStatus)
    ├── repository/      <- Acceso a base de datos
    ├── service/         <- Lógica de negocio
    ├── validation/      <- Validaciones y grupos de validación

```

---

## ⚙️ Validaciones

El sistema valida:

- **Formato y longitud de campos** según las restricciones definidas.
- **Existencia de valores obligatorios** en todos los campos requeridos.
- **Unicidad de `idApuDetail`** como clave primaria.
- **Validación de cantidad** (valor >= 0).
- **Validación de porcentaje de desperdicio** (0 <= valor <= 100).
- **Formato válido del estado** (`status`).
- **Existencia de actividad y atributo** referenciados.
- **Restricciones específicas por operación** (crear vs actualizar).

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`. 

- Casos exitosos de cada operación.
- Validaciones fallidas.
- Excepciones de base de datos.
- Manejo de detalles de APU no encontrados.
- Validación de restricciones de integridad referencial.

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

### **🔹 createApuDetail "POST /apu-details"**

**JSON de entrada:**

```json
{
  "idApuDetail": "APU_DETAIL_001",
  "idActivity": "ACT001",
  "idAttribute": "ATTR001",
  "quantity": 25.5,
  "wastePercentage": 5.0,
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "idApuDetail": "APU_DETAIL_001",
  "idActivity": "ACT001",
  "idAttribute": "ATTR001",
  "quantity": 25.5,
  "wastePercentage": 5.0,
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "status": "active"
}
```

### **🔹 getApuDetails "GET /apu-details"**

**JSON de entrada:**

```json
{}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "idApuDetail": "APU_DETAIL_001",
    "idActivity": "ACT001",
    "idAttribute": "ATTR001",
    "quantity": 25.5,
    "wastePercentage": 5.0,
    "createdAt": [2025, 7, 3, 15, 30, 0, 0],
    "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
    "status": "active"
  }
]
```

### **🔹 getApuDetailById "GET /apu-details/{id}"**

**Parámetros de ruta:**
- `id`: APU_DETAIL_001

**Respuesta (`statusCode: 200`):**

```json
{
  "idApuDetail": "APU_DETAIL_001",
  "idActivity": "ACT001",
  "idAttribute": "ATTR001",
  "quantity": 25.5,
  "wastePercentage": 5.0,
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "status": "active"
}
```

### **🔹 updateApuDetail "PUT /apu-details/{id}"**

**Parámetros de ruta:**
- `id`: APU_DETAIL_001

**JSON de entrada:**

```json
{
  "quantity": 30.75,
  "wastePercentage": 7.5,
  "status": "active"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "idApuDetail": "APU_DETAIL_001",
  "idActivity": "ACT001",
  "idAttribute": "ATTR001",
  "quantity": 30.75,
  "wastePercentage": 7.5,
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 16, 45, 30, 0],
  "status": "active"
}
```

### **🔹 deleteApuDetail "DELETE /apu-details/{id}"**

**Parámetros de ruta:**
- `id`: APU_DETAIL_001

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "APU Detail successfully deactivated",
  "apuDetailId": "APU_DETAIL_001",
  "success": true
}
```

---

## 📌 Consideraciones Finales

- **Soft Delete**: La operación de eliminación desactiva el detalle de APU (cambia status a `inactive`) en lugar de eliminarlo físicamente.
- **Integridad Referencial**: El sistema valida que las actividades y atributos referenciados existan mediante claves foráneas.
- **Validaciones de Dominio**: Se validan rangos específicos para cantidad (>=0) y porcentaje de desperdicio (0-100).
- **Connection Pool**: El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- **Logging**: Sistema de logging estructurado con contexto de request usando SLF4J.
- **Validación**: Validación robusta usando Jakarta Validation con grupos de validación específicos.
- **Arquitectura**: Separación clara de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- **Manejo de Errores**: Manejo centralizado de excepciones con respuestas HTTP apropiadas.
- **Timestamp Management**: Actualización automática de timestamps en todas las operaciones de modificación.
- **Builder Pattern**: Uso del patrón Builder para construcción flexible de objetos ApuDetail.
- **Restricciones de Negocio**: Implementación de reglas de negocio específicas para análisis de precios unitarios.
- **Cascading Operations**: Las operaciones en cascada protegen la integridad de los datos cuando se eliminan actividades o atributos relacionados.