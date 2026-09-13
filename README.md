\# FlowB2B



\### Plataforma SaaS multiempresa para gestión comercial B2B



FlowB2B es una aplicación web diseñada para centralizar y controlar el flujo comercial de empresas B2B, desde la solicitud inicial de un cliente hasta la preparación y gestión del pedido.



El proyecto busca reemplazar procesos fragmentados realizados mediante WhatsApp, hojas de cálculo y correo electrónico por un flujo centralizado, trazable y controlado.



\---



\## 🚀 Problema que resuelve



Muchas empresas B2B gestionan solicitudes, cotizaciones, aprobaciones, pedidos e inventario utilizando diferentes herramientas sin una integración central.



Esto puede generar:



\- Pérdida de trazabilidad.

\- Información comercial dispersa.

\- Demoras en aprobaciones.

\- Errores en el control de inventario.

\- Dificultad para conocer el estado de los pedidos.

\- Falta de información gerencial consolidada.



FlowB2B centraliza estos procesos dentro de una sola plataforma.



\---



\## 🔄 Flujo principal



```text

Solicitud del cliente

&#x20;       ↓

Cotización

&#x20;       ↓

Aprobación interna

&#x20;       ↓

Aceptación del cliente

&#x20;       ↓

Pedido

&#x20;       ↓

Validación y reserva de inventario

&#x20;       ↓

Preparación / seguimiento

&#x20;       ↓

Entrega y cierre

```



\---



\## ✨ Funcionalidades principales



\### 🔐 Seguridad



\- Autenticación mediante JWT.

\- Control de acceso basado en roles y permisos.

\- Protección de endpoints.

\- Separación de funcionalidades según permisos.

\- Contraseñas almacenadas mediante BCrypt.



\### 🏢 Arquitectura multiempresa



FlowB2B permite manejar diferentes empresas dentro de una misma aplicación manteniendo sus datos separados.



Los usuarios únicamente pueden acceder a información correspondiente a su empresa.



\### 👥 Clientes



\- Registro de clientes.

\- Edición y consulta.

\- Información comercial y de contacto.



\### 📦 Productos e inventario



\- Gestión de productos.

\- Categorías de productos.

\- Control de stock.

\- Stock disponible.

\- Stock reservado.

\- Validaciones de inventario.

\- Reserva y liberación de stock asociada al flujo de pedidos.



\### 📩 Solicitudes



\- Registro de solicitudes comerciales.

\- Asociación con clientes y vendedores.

\- Detalle de productos solicitados.

\- Seguimiento del estado de la solicitud.



\### 💰 Cotizaciones



\- Creación de cotizaciones.

\- Detalle de productos.

\- Descuentos.

\- Cálculo de subtotales y totales.

\- Envío a aprobación interna.



\### ✅ Aprobaciones



\- Flujo de aprobación comercial.

\- Aprobación o rechazo.

\- Registro de solicitante y aprobador.

\- Control de separación de responsabilidades.



\### 🛒 Pedidos



\- Generación de pedidos a partir del flujo comercial.

\- Detalle del pedido.

\- Gestión de estados.

\- Integración con inventario.

\- Historial de cambios.



\### 📋 Tareas



\- Creación de tareas operativas.

\- Asociación con pedidos.

\- Seguimiento de estados.



\### 🔔 Notificaciones



\- Notificaciones internas relacionadas con eventos del sistema.



\### ⚙️ Administración



\- Gestión de usuarios.

\- Creación de usuarios.

\- Activación y desactivación.

\- Asignación de roles.

\- Gestión de roles.

\- Gestión de permisos.



\### 📊 Dashboard



Panel principal con indicadores del estado general de la operación.



\### 📈 Dashboard gerencial



Módulo de analítica con información consolidada para apoyar el seguimiento comercial y operativo.



Incluye filtros por diferentes dimensiones del negocio.



\---



\## 🛠️ Tecnologías



\### Frontend



\- Angular 18

\- TypeScript

\- HTML

\- SCSS

\- Angular Router

\- HttpClient



\### Backend



\- Java 17

\- Spring Boot

\- Spring Web MVC

\- Spring Data JPA

\- Spring Security

\- JWT

\- BCrypt

\- Maven



\### Base de datos



\- MySQL



\### Documentación de API



\- OpenAPI

\- Swagger UI



\### Herramientas utilizadas



\- Spring Tools / Eclipse

\- Visual Studio Code

\- MySQL Workbench

\- Postman

\- Git

\- GitHub



\---



\## 🏗️ Arquitectura



```text

┌─────────────────────────────┐

│          Angular            │

│          Frontend           │

└──────────────┬──────────────┘

&#x20;              │

&#x20;              │ HTTP / REST

&#x20;              │ JWT

&#x20;              ▼

┌─────────────────────────────┐

│       Spring Boot API       │

│                             │

│ Controllers                 │

│ Services                    │

│ Repositories                │

│ Spring Security             │

│ JWT / RBAC                  │

└──────────────┬──────────────┘

&#x20;              │

&#x20;              │ JPA / Hibernate

&#x20;              ▼

┌─────────────────────────────┐

│            MySQL            │

│        flowb2b\_db           │

└─────────────────────────────┘

```



\---



\## 🔐 Roles y permisos



El sistema implementa RBAC (Role-Based Access Control).



Ejemplos de roles utilizados:



```text

VENDEDOR

JEFE\_COMERCIAL

```



Cada rol posee permisos específicos.



Por ejemplo, un jefe comercial puede acceder a funciones administrativas y de aprobación que no están disponibles para un vendedor.



Los permisos también son incluidos en el token JWT generado durante la autenticación.



\---



\## 🏢 Aislamiento multiempresa



Las principales entidades del sistema están asociadas a una empresa.



```text

Usuario

&#x20;  ↓

empresa\_id

&#x20;  ↓

Clientes

Solicitudes

Cotizaciones

Pedidos

Inventario

Tareas

...

```



El backend utiliza la empresa del usuario autenticado para restringir el acceso a los datos correspondientes.



\---



\## 📚 API REST y Swagger



La API se encuentra documentada mediante OpenAPI y Swagger UI.



Durante el desarrollo puede consultarse en:



```text

http://localhost:8080/swagger-ui/index.html

```



La especificación OpenAPI se encuentra en:



```text

http://localhost:8080/v3/api-docs

```



Swagger permite visualizar los endpoints disponibles y realizar solicitudes autenticadas mediante JWT.



\---



\## 📁 Estructura del repositorio



```text

flowb2b/

│

├── backend/

│   ├── src/

│   ├── pom.xml

│   └── ...

│

├── frontend/

│   ├── src/

│   ├── angular.json

│   ├── package.json

│   └── ...

│

├── .gitignore

└── README.md

```



\---



\## ▶️ Ejecución local



\### Requisitos



\- Java 17

\- Maven

\- Node.js

\- Angular CLI

\- MySQL



\### Base de datos



Crear una base de datos:



```sql

CREATE DATABASE flowb2b\_db;

```



Configurar las siguientes variables de entorno:



```text

DB\_PASSWORD

JWT\_SECRET

```



Opcionalmente:



```text

DB\_URL

DB\_USERNAME

SERVER\_PORT

```



\### Backend



Desde la carpeta:



```text

backend

```



ejecutar:



```bash

mvn spring-boot:run

```



Por defecto la API estará disponible en:



```text

http://localhost:8080

```



\### Frontend



Desde:



```text

frontend

```



instalar dependencias:



```bash

npm install

```



Ejecutar:



```bash

ng serve

```



Abrir:



```text

http://localhost:4200

```



\---



\## 🔒 Seguridad de configuración



Las credenciales sensibles no se almacenan directamente en el repositorio.



La aplicación utiliza variables de entorno para información como:



```text

DB\_PASSWORD

JWT\_SECRET

```



\---



\## 🗺️ Roadmap



Posibles mejoras futuras:



\- Dockerización completa.

\- Pruebas automatizadas adicionales.

\- CI/CD.

\- Despliegue en nube.

\- Automatizaciones mediante n8n.

\- Integraciones externas.

\- Analítica avanzada.

\- Módulos asistidos por IA.



\---



\## 👨‍💻 Autor



\*\*Edwar Mia Aguirre\*\*



Proyecto desarrollado como parte de mi portafolio profesional de Ingeniería de Sistemas.



\---



\## 📌 Estado



\*\*V1 funcional\*\*



El flujo comercial principal se encuentra implementado y probado:



```text

Solicitud

→ Cotización

→ Aprobación

→ Aceptación

→ Pedido

→ Inventario

→ Tareas

→ Seguimiento

```

