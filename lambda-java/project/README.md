## ProjectLambda - Java 21

---

Este proyecto contiene un conjunto de funciones AWS Lambda escritas en Java 21. Implementa operaciones CRUD y gestión de estados sobre los proyectos.

## 📦 Funcionalidades Implementadas

Se desarrollaron las siguientes funciones Lambda:

- createProject: Crea un nuevo proyecto con los datos proporcionados.
- getProjects: Lista todos los proyectos existentes.
- getProjectById: Obtiene los detalles de un proyecto mediante su ID.
- updateProject: Actualiza los campos editables de un proyecto existente.
- deleteProject: Elimina un proyecto por su ID.
- Activaciones y cambios de estado: Permite activar, desactivar, suspender, completar, cancelar o marcar como pendiente un proyecto.

---

## 🧾 Estructura de la Entidad `Project`

| **Campo**      |    **Tipo**     |     **Restricciones**                     | **Descripción** |
| -------------- | --------------- | ----------------------------------------- | --------------- |
| id             | String          | Requerido. Máx. 255 alfanuméricos         |                 |
| name           | String          | Requerido. Máx. 100 caracteres            |                 |
| description    | String          | Requerido. Máx. 500 caracteres            |                 |
| address        | String          | Opcional. Máx. 150 caracteres             |                 |
| city           | String          | Opcional. Máx. 50 caracteres             |                 |
| state          | String          | Opcional. Máx. 100 caracteres             |                 |
| country        | String          | Opcional. Máx. 100 caracteres             |                 |
| createdAt      | LocalDateTime   | Se asigna automáticamente al crear        |                 |
| updatedAt      | LocalDateTime   | Se actualiza automáticamente al modificar |                 |

| status         | String (ENUM)   | Valores: `active`, `inactive`, `pending`,
                                         `suspended`, `completed`, `cancelled` |                 |

| responsibleUser| String          | Opcional. Máx. 100 caracteres             |                 |
| dataSource     | String          | Opcional. Máx. 100 caracteres             |                 |
| company        | String          | Opcional. Máx. 100 caracteres             |                 |
| createdBy      | String          | Opcional. Máx. 100 caracteres             |                 |
| budget         | BigDecimal      | Opcional. Valor por defecto: 0.00         |                 |
| inventory      | String          | Opcional. Máx. 255 caracteres             |                 |

---

## 🗂️ Estructura del Proyecto

```
lambda-java/
└── project/
    ├── dto/
    │   ├── request/     <- DTOs para peticiones (Create, Update)
    │   └── response/    <- DTOs para respuestas (Project, Delete)
    ├── exception/       <- Excepciones personalizadas
    ├── handler/         <- Controladores Lambda individuales
    ├── mapper/          <- Conversores DTO <-> Modelo
    ├── model/           <- Entidades del dominio (Project, ProjectStatus)
    ├── repository/      <- Acceso a base de datos con JDBC
    ├── service/         <- Lógica de negocio
    ├── validation/      <- validaciones
    └── test/            <- Pruebas automatizadas

```

---

## ⚙️ Validaciones

El sistema valida:

- Formato y longitud de campos
- Existencia de valores obligatorios.
- Unicidad de `id` y `name`.
- Formato válido del estado (`status`).
- Validación de presupuesto (valores no negativos).
- Restricciones específicas por operación (crear vs actualizar).

---

## 🧪 Pruebas

Las pruebas unitarias se ubican en `test/java/com/lambdas`. 

- Casos exitosos de cada operación.
- Validaciones fallidas.
- Excepciones de base de datos.
- Pruebas de pool de conexiones.

---

## 🧰 Tecnologías Utilizadas

- **Java 21**
- **AWS Lambda**
- **JDBC / PostgreSQL**
- **Maven**
- **Jackson (JSON processing)**
- **Java Time API (`LocalDateTime`)**
- **Patrón Builder + DTOs**
- **Validación personalizada**
- **Pool de conexiones con ConnectionPoolManager**

---

## **📥 Ejemplos de Peticiones y Respuestas**

### **🔹 createProject "POST /projects"**

**JSON de entrada:**

```json
{
  "id": "PROJ001",
  "name": "Sistema de Gestión Empresarial",
  "description": "Desarrollo de sistema integral para gestión de recursos empresariales",
  "address": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "status": "active",
  "responsibleUser": "juan.perez@company.com",
  "dataSource": "ERP_SYSTEM",
  "company": "TechCorp Solutions",
  "createdBy": "admin@company.com",
  "budget": 150000.00,
  "inventory": "INV-2025-001"
}
```

**Respuesta (`statusCode: 201`):**

```json
{
  "id": "PROJ001",
  "name": "Sistema de Gestión Empresarial",
  "description": "Desarrollo de sistema integral para gestión de recursos empresariales",
  "address": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333603829],
  "updatedAt": [2025, 7, 7, 16, 5, 41, 333603829],
  "status": "active",
  "responsibleUser": "juan.perez@company.com",
  "dataSource": "ERP_SYSTEM",
  "company": "TechCorp Solutions",
  "createdBy": "admin@company.com",
  "budget": 150000.00,
  "inventory": "INV-2025-001"
}
```

### **🔹 getProjects "GET /projects"**

**JSON de entrada:**

```json
{
}
```

**Respuesta (`statusCode: 200`):**

```json
[
  {
    "id": "PROJ001",
    "name": "Sistema de Gestión Empresarial",
    "description": "Desarrollo de sistema integral para gestión de recursos empresariales",
    "address": "Calle 123 #45-67",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "Colombia",
    "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
    "updatedAt": [2025, 7, 7, 16, 5, 41, 333604000],
    "status": "active",
    "responsibleUser": "juan.perez@company.com",
    "dataSource": "ERP_SYSTEM",
    "company": "TechCorp Solutions",
    "createdBy": "admin@company.com",
    "budget": 150000.00,
    "inventory": "INV-2025-001"
  }
]
```

### **🔹 getProjectById "GET /projects/{id}"**

**JSON de entrada:**

```json
{
  "pathParameters": {
    "id": "PROJ001"
  }
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id": "PROJ001",
  "name": "Sistema de Gestión Empresarial",
  "description": "Desarrollo de sistema integral para gestión de recursos empresariales",
  "address": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "updatedAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "status": "active",
  "responsibleUser": "juan.perez@company.com",
  "dataSource": "ERP_SYSTEM",
  "company": "TechCorp Solutions",
  "createdBy": "admin@company.com",
  "budget": 150000.00,
  "inventory": "INV-2025-001"
}
```

### **🔹 updateProject "PUT /projects/{id}"**

**JSON de entrada:**

```json
{
  "name": "Sistema de Gestión Empresarial Avanzado",
  "description": "Desarrollo de sistema integral para gestión de recursos empresariales con módulos avanzados",
  "address": "Calle 123 #45-67, Edificio Torre Norte",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "responsibleUser": "maria.rodriguez@company.com",
  "budget": 200000.00,
  "status": "active"
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "id": "PROJ001",
  "name": "Sistema de Gestión Empresarial Avanzado",
  "description": "Desarrollo de sistema integral para gestión de recursos empresariales con módulos avanzados",
  "address": "Calle 123 #45-67, Edificio Torre Norte",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "createdAt": [2025, 7, 7, 16, 5, 41, 333604000],
  "updatedAt": [2025, 7, 7, 16, 12, 47, 113913334],
  "status": "active",
  "responsibleUser": "maria.rodriguez@company.com",
  "dataSource": "ERP_SYSTEM",
  "company": "TechCorp Solutions",
  "createdBy": "admin@company.com",
  "budget": 200000.00,
  "inventory": "INV-2025-001"
}
```

### **🔹 deleteProject "DELETE /projects/{id}"**

**JSON de entrada:**

```json
{
  "pathParameters": {
    "id": "PROJ001"
  }
}
```

**Respuesta (`statusCode: 200`):**

```json
{
  "message": "Project successfully deactivated",
  "projectId": "PROJ001",
  "success": true
}
```

---

## 📌 Consideraciones Finales

- El manejo de conexión a base de datos se gestiona a través de `ConnectionPoolManager` (pool de conexiones singleton).
- La arquitectura favorece separación de responsabilidades mediante paquetes independientes para `handler`, `service`, `repository`, `dto`, `exception`, etc.
- Se incluyen operaciones adicionales como conteo, búsqueda por estado, compañía o usuario responsable, y validación de existencia.
- El sistema maneja múltiples estados para proyectos: activo, inactivo, pendiente, suspendido, completado y cancelado.
- Se implementa validación de presupuesto para evitar valores negativos.
- Los timestamps se actualizan automáticamente en cada modificación del proyecto.
- El patrón Builder facilita la creación de objetos Project tanto para nuevas entidades como para carga desde base de datos.