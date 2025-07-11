## ActivityLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre las actividades.

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- **createActivity**: Crea una nueva actividad con los datos proporcionados.
- **getActivities**: Lista todas las actividades existentes.
- **getActivityById**: Obtiene los detalles de una actividad mediante su ID.
- **updateActivity**: Actualiza los campos editables de una actividad existente.
- **deleteActivity**: Elimina una actividad por su ID.
- **Activaciones y cambios de estado**: Permite activar o desactivar una actividad.

---

## 🧾 Estructura de la Entidad `Activity`

| **Campo**    |    **Tipo**    |     **Restricciones**                     | **Descripción** |
| ------------ | -------------- | ----------------------------------------- | --------------- |
| idActivity   | String         | Requerido. Máx. 255 caracteres            |                 |
| idChapter    | String         | Requerido. Máx. 255 caracteres            |                 |
| code         | String         | Requerido. 1-100 caracteres               |                 |
| name         | String         | Requerido. 1-100 caracteres               |                 |
| description  | String         | Opcional. Máx. 200 caracteres             |                 |
| unit         | String         | Requerido. 1-50 caracteres                |                 |
| quantity     | Double         | Requerido. Debe ser >= 0                  |                 |
| createdAt    | LocalDateTime  | Se asigna automáticamente al crear        |                 |
| updatedAt    | LocalDateTime  | Se actualiza automáticamente al modificar |                 |
| status       | String (ENUM)  | Valores: `active`, `inactive`             |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── activity/
    ├── dto/
    │   ├── request/     <- DTOs para peticiones (CreateActivityRequestDTO, UpdateActivityRequestDTO)
    │   └── response/    <- DTOs para respuestas (ActivityResponseDTO, DeleteResponseDTO)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (Activity, ActivityStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── validation/      <- Validaciones y grupos de validación

```

---

## ⚙️ Validaciones

El sistema valida:

- **Formato y longitud de campos** según las restricciones definidas.
- **Existencia de valores obligatorios** en todos los campos requeridos.
- **Unicidad de `idActivity`** y `code`.
- **Longitud mínima y máxima** de campos de texto.
- **Valor no negativo** para quantity.
- **Formato válido del estado** (`status`).
- **Restricciones específicas por operación** (crear vs actualizar).
- **Existencia del capítulo** referenciado por `idChapter`.

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`. 

- Casos exitosos de cada operación.
- Validaciones fallidas.
- Excepciones de base de datos.
- Manejo de actividades no encontradas.

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

### **🔹 createActivity "POST /activities"**

**JSON de entrada:**

```json
{
  "idActivity": "ACT001",
  "idChapter": "CHAP001",
  "code": "COD001",
  "name": "Excavación manual",
  "description": "Excavación manual en terreno tipo II",
  "unit": "m³",
  "quantity": 150.5,
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "idActivity": "ACT001",
  "idChapter": "CHAP001",
  "code": "COD001",
  "name": "Excavación manual",
  "description": "Excavación manual en terreno tipo II",
  "unit": "m³",
  "quantity": 150.5,
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "status": "active"
}
```

### **🔹 getActivities "GET /activities"**

**JSON de entrada:**

```json
{}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "idActivity": "ACT001",
    "idChapter": "CHAP001",
    "code": "COD001",
    "name": "Excavación manual",
    "description": "Excavación manual en terreno tipo II",
    "unit": "m³",
    "quantity": 150.5,
    "createdAt": [2025, 7, 3, 15, 30, 0, 0],
    "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
    "status": "active"
  }
]
```

### **🔹 getActivityById "GET /activities/{id}"**

**Parámetros de ruta:**
- `id`: ACT001

**Respuesta (`statusCode: 200`):**

```json
{
  "idActivity": "ACT001",
  "idChapter": "CHAP001",
  "code": "COD001",
  "name": "Excavación manual",
  "description": "Excavación manual en terreno tipo II",
  "unit": "m³",
  "quantity": 150.5,
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "status": "active"
}
```

### **🔹 updateActivity "PUT /activities/{id}"**

**Parámetros de ruta:**
- `id`: ACT001

**JSON de entrada:**

```json
{
  "code": "COD001-UPD",
  "name": "Excavación manual actualizada",
  "description": "Excavación manual en terreno tipo III actualizada",
  "unit": "m³",
  "quantity": 200.75,
  "status": "active"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "idActivity": "ACT001",
  "idChapter": "CHAP001",
  "code": "COD001-UPD",
  "name": "Excavación manual actualizada",
  "description": "Excavación manual en terreno tipo III actualizada",
  "unit": "m³",
  "quantity": 200.75,
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 16, 45, 30, 0],
  "status": "active"
}
```

### **🔹 deleteActivity "DELETE /activities/{id}"**

**Parámetros de ruta:**
- `id`: ACT001

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "Activity successfully deactivated",
  "activityId": "ACT001",
  "success": true
}
```

---

## 📌 Consideraciones Finales

- **Soft Delete**: La operación de eliminación desactiva la actividad (cambia status a `inactive`) en lugar de eliminarla físicamente.
- **Connection Pool**: El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- **Logging**: Sistema de logging estructurado con contexto de request usando SLF4J.
- **Validación**: Validación robusta usando Jakarta Validation con grupos de validación específicos.
- **Arquitectura**: Separación clara de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- **Manejo de Errores**: Manejo centralizado de excepciones con respuestas HTTP apropiadas.
- **Timestamp Management**: Actualización automática de timestamps en todas las operaciones de modificación.
- **Builder Pattern**: Uso del patrón Builder para construcción flexible de objetos Activity.
- **Relaciones**: Manejo de relaciones con capítulos mediante claves foráneas con eliminación en cascada.
- **Índices**: Optimización de consultas mediante índices en campos frecuentemente consultados (code, status, createdAt, idChapter).