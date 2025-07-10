## UserLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre los usuarios.

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- **createUser**: Crea un nuevo usuario con los datos proporcionados.
- **getUsers**: Lista todos los usuarios existentes.
- **getUserById**: Obtiene los detalles de un usuario mediante su ID.
- **updateUser**: Actualiza los campos editables de un usuario existente.
- **deleteUser**: Elimina un usuario por su ID .
- **Activaciones y cambios de estado**: Permite activar, desactivar, suspender o marcar como pendiente un usuario.

---

## 🧾 Estructura de la Entidad `User`

| **Campo**    |    **Tipo**    |     **Restricciones**                     | **Descripción** |
| ------------ | -------------- | ----------------------------------------- | --------------- |
| idUser       | String         | Requerido. Máx. 255 caracteres            |                 |
| idCompany    | String         | Requerido. Máx. 255 caracteres            |                 |
| name         | String         | Requerido. 1-100 caracteres               |                 |
| lastName     | String         | Requerido. 1-100 caracteres               |                 |
| address      | String         | Requerido. 1-100 caracteres               |                 |
| phone        | String         | Requerido. 1-20 caracteres                |                 |
| email        | String         | Requerido. Formato email. 1-50 caracteres |                 |
| password     | String         | Requerido. Mínimo 8 caracteres            |                 |
| position     | String         | Requerido. 1-100 caracteres               |                 |
| createdAt    | LocalDateTime  | Se asigna automáticamente al crear        |                 |
| updatedAt    | LocalDateTime  | Se actualiza automáticamente al modificar |                 |
| lastAccess   | LocalDateTime  | Opcional                                  |                 |
| status       | String (ENUM)  | Valores: `active`, `inactive`,
                                                     `pending`, `suspended` |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── user/
    ├── dto/
    │   ├── request/     <- DTOs para peticiones (CreateUserRequestDTO, UpdateUserRequestDTO)
    │   └── response/    <- DTOs para respuestas (UserResponseDTO, DeleteResponseDTO)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    │   ├── CreateUserHandler.java
    │   ├── GetUsersHandler.java
    │   ├── GetUserByIdHandler.java
    │   ├── UpdateUserHandler.java
    │   └── DeleteUserHandler.java
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (User, UserStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── validation/      <- Validaciones y grupos de validación

```

---

## ⚙️ Validaciones

El sistema valida:

- **Formato y longitud de campos** según las restricciones definidas.
- **Existencia de valores obligatorios** en todos los campos requeridos.
- **Unicidad de `idUser`** y `email`.
- **Formato válido del email** usando expresiones regulares.
- **Longitud mínima de contraseña** (8 caracteres).
- **Formato válido del estado** (`status`).
- **Restricciones específicas por operación** (crear vs actualizar).

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`. 

- Casos exitosos de cada operación.
- Validaciones fallidas.
- Excepciones de base de datos.
- Manejo de usuarios no encontrados.

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

### **🔹 createUser "POST /users"**

**JSON de entrada:**

```json
{
  "idUser": "USER001",
  "idCompany": "COMP001",
  "name": "Juan",
  "lastName": "Pérez",
  "address": "Calle 123 #45-67",
  "phone": "+57 300 123 4567",
  "email": "juan.perez@company.com",
  "password": "securePassword123",
  "position": "Software Developer",
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "idUser": "USER001",
  "idCompany": "COMP001",
  "name": "Juan",
  "lastName": "Pérez",
  "address": "Calle 123 #45-67",
  "phone": "+57 300 123 4567",
  "email": "juan.perez@company.com",
  "position": "Software Developer",
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "lastAccess": null,
  "status": "active"
}
```

### **🔹 getUsers "GET /users"**

**JSON de entrada:**

```json
{}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "idUser": "USER001",
    "idCompany": "COMP001",
    "name": "Juan",
    "lastName": "Pérez",
    "address": "Calle 123 #45-67",
    "phone": "+57 300 123 4567",
    "email": "juan.perez@company.com",
    "position": "Software Developer",
    "createdAt": [2025, 7, 3, 15, 30, 0, 0],
    "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
    "lastAccess": null,
    "status": "active"
  }
]
```

### **🔹 getUserById "GET /users/{id}"**

**Parámetros de ruta:**
- `id`: USER001

**Respuesta (`statusCode: 200`):**

```json
{
  "idUser": "USER001",
  "idCompany": "COMP001",
  "name": "Juan",
  "lastName": "Pérez",
  "address": "Calle 123 #45-67",
  "phone": "+57 300 123 4567",
  "email": "juan.perez@company.com",
  "position": "Software Developer",
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 15, 30, 0, 0],
  "lastAccess": null,
  "status": "active"
}
```

### **🔹 updateUser "PUT /users/{id}"**

**Parámetros de ruta:**
- `id`: USER001

**JSON de entrada:**

```json
{
  "name": "Juan Carlos",
  "lastName": "Pérez García",
  "address": "Carrera 15 #123-45",
  "phone": "+57 301 234 5678",
  "email": "juan.perez.updated@company.com",
  "position": "Senior Software Developer",
  "status": "active"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "idUser": "USER001",
  "idCompany": "COMP001",
  "name": "Juan Carlos",
  "lastName": "Pérez García",
  "address": "Carrera 15 #123-45",
  "phone": "+57 301 234 5678",
  "email": "juan.perez.updated@company.com",
  "position": "Senior Software Developer",
  "createdAt": [2025, 7, 3, 15, 30, 0, 0],
  "updatedAt": [2025, 7, 3, 16, 45, 30, 0],
  "lastAccess": null,
  "status": "active"
}
```

### **🔹 deleteUser "DELETE /users/{id}"**

**Parámetros de ruta:**
- `id`: USER001

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "User successfully deactivated",
  "userId": "USER001",
  "success": true
}
```

---

## 📌 Consideraciones Finales

- **Soft Delete**: La operación de eliminación desactiva el usuario (cambia status a `inactive`) en lugar de eliminarlo físicamente.
- **Connection Pool**: El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- **Logging**: Sistema de logging estructurado con contexto de request usando SLF4J.
- **Validación**: Validación robusta usando Jakarta Validation con grupos de validación específicos.
- **Arquitectura**: Separación clara de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- **Manejo de Errores**: Manejo centralizado de excepciones con respuestas HTTP apropiadas.
- **Timestamp Management**: Actualización automática de timestamps en todas las operaciones de modificación.
- **Builder Pattern**: Uso del patrón Builder para construcción flexible de objetos User.
