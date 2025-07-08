## RoleLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre los roles.

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- createRole: Crea un nuevo rol con los datos proporcionados.
- getRoles: Lista todos los roles existentes.
- getRoleById: Obtiene los detalles de un rol mediante su ID.
- updateRole: Actualiza los campos editables de un rol existente.
- deleteRole: Elimina un rol por su ID.
- Activaciones y cambios de estado: Permite activar, desactivar, suspender o marcar como pendiente un rol.

---

## 🧾 Estructura de la Entidad `Role`

| **Campo**  |    **Tipo**    |     **Restricciones**                     | **Descripción** |
| ---------- | -------------- | ---------------------------------         | --------------- |
| idRole     | String         | Requerido. Máx. 255 alfanuméricos         |                 |
| name       | String         | Requerido. Máx. 50 caracteres             |                 |
| description| String         | Requerido. Máx. 100 caracteres            |                 |
| createdAt  | LocalDateTime  | Se asigna automáticamente al crear        |                 |
| updatedAt  | LocalDateTime  | Se actualiza automáticamente al modificar |                 |
| status     | String (ENUM)  | Valores: `active`, `inactive`             |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── role/
    ├── dto/
    ├── exception/
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (Role, RoleStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── util/            <- Utilidades y validaciones
    └── test/            <- Pruebas automatizadas

```

---

## ⚙️ Validaciones

El sistema valida:

- Formato y longitud de campos
- Existencia de valores obligatorios.
- Unicidad de `idRole` y `name`.
- Formato válido del estado (`status`).
- Restricciones específicas por operación (crear vs actualizar).

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`. 

- Casos exitosos de cada operación.
- Validaciones fallidas.
- Excepciones de base de datos.

---

## 🧰 Tecnologías Utilizadas

- **Java 21**
- **AWS Lambda**
- **JDBC / PostgreSQL**
- **Maven**
- **Java Time API (`LocalDateTime`)**
- **Patrón Builder + DTOs**
- **Validación personalizada**

---

## **📥 Ejemplos de Peticiones y Respuestas**

### **🔹 createRole "POST /roles”**

**JSON de entrada:**

```json
{
  "id_role": "ROLE001",
  "name": "Administrator",
  "description": "System administrator with full access privileges",
  "status": "active"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "id_role": "ROLE001",
  "name": "Administrator",
  "description": "System administrator with full access privileges",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333603829],
  "updatedAt": [2025, 7, 7, 16, 5, 41, 333603829],
  "status": "active"
}
```

### **🔹 getRoles "GET /roles"**

**JSON de entrada :**

```json
{
}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "id_role": "ROLE001",
    "name": "Administrator",
    "description": "System administrator with full access privileges",
    "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
    "updatedAt": [2025, 7, 7, 16, 5, 41, 333604000],
    "status": "active"
  }
]
```

### **🔹 getRoleById "GET /roles/{id}”**

**JSON de entrada :**

```json
{
  "pathParameters": {
    "id": "ROLE001"
  }
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id_role": "ROLE001",
  "name": "Administrator",
  "description": "System administrator with full access privileges",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "updatedAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "status": "active"
}
```

### **🔹 updateRole "PUT /roles/{id}”**

**JSON de entrada:**

```json
{
  "id_role": "ROLE001",
  "name": "Super Administrator",
  "description": "Updated system administrator with enhanced privileges"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id_role": "ROLE001",
  "name": "Super Administrator",
  "description": "Updated system administrator with enhanced privileges",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "updatedAt": [2025, 7, 7, 16, 12, 47, 113913334],
  "status": "active"
}
```

### **🔹 deleteRole "DELETE /roles/{id}”**

**JSON de entrada:**

```json
{
  "id_role": "ROLE001",
  "status": "inactive"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id_role": "ROLE001",
  "name": "Super Administrator",
  "description": "Updated system administrator with enhanced privileges",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "updatedAt": [2025, 7, 7, 16, 15, 22, 878901643],
  "status": "inactive"
}
```

---

## 📌 Consideraciones Finales

- El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- La arquitectura favorece separación de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- Se incluyen operaciones adicionales como conteo, búsqueda por estado o nombre, y validación de existencia.