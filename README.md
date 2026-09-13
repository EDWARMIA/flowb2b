# FlowB2B

Plataforma web multiempresa para centralizar y controlar el flujo comercial B2B, desde la solicitud del cliente hasta la gestión del pedido.

## Demo

🌐 https://edwarmia.github.io/flowb2b/

## Tecnologías

- Angular 18
- TypeScript
- Spring Boot
- Java 17
- MySQL
- Spring Security
- JWT
- JPA / Hibernate
- Swagger / OpenAPI
- Git / GitHub

## Funcionalidades principales

- Gestión de clientes
- Solicitudes comerciales
- Cotizaciones
- Aprobaciones internas
- Pedidos
- Inventario
- Tareas
- Notificaciones
- Dashboard general
- Dashboard gerencial
- Gestión de usuarios
- Roles y permisos
- Arquitectura multiempresa
- Autenticación JWT

## Flujo principal

```text
Solicitud
→ Cotización
→ Aprobación
→ Aceptación
→ Pedido
→ Inventario
→ Seguimiento
```

## Arquitectura

```text
Angular
   ↓
REST API
   ↓
Spring Boot
   ↓
MySQL
```

La seguridad se gestiona mediante JWT, Spring Security y control de acceso basado en roles y permisos.

## API

La API se encuentra documentada con Swagger / OpenAPI.

Durante el desarrollo:

```text
http://localhost:8080/swagger-ui/index.html
```

## Estructura

```text
flowb2b/
├── backend/
├── frontend/
├── docs/
└── README.md
```

## Autor

**Edwar Mia Aguirre**

Proyecto desarrollado como parte de mi portafolio profesional de Ingeniería de Sistemas.