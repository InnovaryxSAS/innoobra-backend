## ChapterLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21 para gestionar capítulos presupuestales. Implementa operaciones CRUD y manejo de estado sobre la entidad `Chapter`.

---

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- **createChapter**: Crea un nuevo capítulo con los datos proporcionados.
- **getChapters**: Lista todos los capítulos existentes.
- **getChapterById**: Obtiene los detalles de un capítulo mediante su ID.
- **updateChapter**: Actualiza los campos editables de un capítulo existente.
- **deleteChapter**: elimina un capítulo por su ID.

---

## 🧾 Estructura de la Entidad `Chapter`

| **Campo**     | **Tipo**          | **Restricciones**                                              | **Descripción**                           |
| ------------- | ----------------- | -------------------------------------------------------------- | ----------------------------------------- |
| idChapter     | String            | Requerido. Máx. 255 caracteres                                 |                                           |
| idBudget      | String            | Requerido. FK a `budgets`                                      |                                           |
| code          | String            | Requerido. 1-100 caracteres, no vacío                          |                                           |
| name          | String            | Requerido. 1-100 caracteres, no vacío                          |                                           |
| description   | String (opcional) | Máx. 200 caracteres                                            |                                           |
| createdAt     | LocalDateTime     | Se asigna automáticamente al crear                             |                                           |
| updatedAt     | LocalDateTime     | Se actualiza automáticamente al modificar                      |                                           |
| status        | String (ENUM)     | Valores permitidos: `active`, `inactive`                       |                                           |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── chapter/
    ├── dto/
    │   ├── request/     <- DTOs para peticiones (CreateChapterRequestDTO, UpdateChapterRequestDTO)
    │   └── response/    <- DTOs para respuestas (ChapterResponseDTO, DeleteResponseDTO)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (Chapter, ChapterStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── validation/      <- Validaciones y grupos de validación
```

---

## ⚙️ Validaciones

El sistema valida:

- **Longitud y formato** de campos según restricciones.
- **Existencia de campos obligatorios**.
- **Unicidad de `code`** dentro de un presupuesto.
- **Formato válido del estado `status`**.
- **Relación válida con presupuesto (`idBudget`)**.
- **Restricciones diferentes para crear o actualizar**.

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`.

- Casos exitosos para cada función.
- Validaciones fallidas.
- Manejo de errores por base de datos.
- Casos de capítulo no encontrado.

---

## 🧰 Tecnologías Utilizadas

- **Java 21**
- **AWS Lambda**
- **PostgreSQL / JDBC**
- **Maven**
- **Jackson**
- **Jakarta Validation**
- **SLF4J Logging**
- **Builder Pattern**
- **Connection Pool Management**

---

## 📥 Ejemplos de Peticiones y Respuestas

### 🔹 createChapter `POST /chapters`

```json
{
  "idChapter": "CHAP001",
  "idBudget": "BUDGET001",
  "code": "CH001",
  "name": "Marketing Chapter",
  "description": "Chapter for marketing expenses and activities",
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "idChapter": "CHAP001",
  "idBudget": "BUDGET001",
  "code": "CH001",
  "name": "Marketing Chapter",
  "description": "Chapter for marketing expenses and activities",
  "createdAt": [2025, 7, 10, 15, 42, 53, 231246757],
  "updatedAt": [2025, 7, 10, 15, 42, 53, 231246757],
  "status": "active"
}
```

### 🔹 getChapters `GET /chapters`

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "idChapter": "CHAP001",
    "idBudget": "BUDGET001",
    "code": "CH001",
    "name": "Marketing Chapter",
    "description": "Chapter for marketing expenses and activities",
    "createdAt": [2025, 7, 10, 15, 42, 53, 231247000],
    "updatedAt": [2025, 7, 10, 15, 42, 53, 231247000],
    "status": "active"
  }
]
```

### 🔹 getChapterById `GET /chapters/{id}`

**Respuesta (`statusCode: 200`):**

```json
{
  "idChapter": "CHAP001",
  "idBudget": "BUDGET001",
  "code": "CH001-UPD",
  "name": "Updated Marketing Chapter",
  "description": "Updated chapter for marketing expenses and promotional activities",
  "createdAt": [2025, 7, 10, 15, 42, 53, 231247000],
  "updatedAt": [2025, 7, 10, 15, 45, 8, 95205000],
  "status": "active"
}
```

### 🔹 updateChapter `PUT /chapters/{id}`

```json
{
  "code": "CH001-UPD",
  "name": "Updated Marketing Chapter",
  "description": "Updated chapter for marketing expenses and promotional activities",
  "status": "active"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "idChapter": "CHAP001",
  "idBudget": "BUDGET001",
  "code": "CH001-UPD",
  "name": "Updated Marketing Chapter",
  "description": "Updated chapter for marketing expenses and promotional activities",
  "createdAt": [2025, 7, 10, 15, 42, 53, 231247000],
  "updatedAt": [2025, 7, 10, 15, 45, 8, 95205413],
  "status": "active"
}
```

### 🔹 deleteChapter `DELETE /chapters/{id}`

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "Chapter successfully deactivated",
  "chapterId": "CHAP001",
  "success": true
}
```

---

## 📌 Consideraciones Finales

- **Soft Delete**: El capítulo no se elimina físicamente; su estado se cambia a `inactive`.
- **Integridad referencial**: El campo `idBudget` tiene clave foránea con `ON DELETE CASCADE`.
- **Logging**: Uso de SLF4J para trazabilidad de peticiones.
- **Validación exhaustiva**: Uso de `Jakarta Validation` con restricciones personalizadas.
- **Actualización automática de timestamps** con cada operación.
- **Builder Pattern** para creación segura de objetos.