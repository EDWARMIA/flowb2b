-- ============================================================
-- FLOWB2B - DATOS DEMO
-- Archivo: 02_seed.sql
-- Ejecutar DESPUÉS de 01_schema.sql
-- ============================================================
--
-- Usuario demo:
-- correo: admin@flowb2b.demo
-- contraseña: Admin123*
--
-- IMPORTANTE:
-- Esta cuenta es únicamente para entorno DEMO / desarrollo.
-- NO reutilizar esta contraseña en producción.
-- ============================================================

USE flowb2b_db;

START TRANSACTION;

-- ============================================================
-- 1. EMPRESA DEMO
-- ============================================================

INSERT INTO empresa (
    razon_social,
    nombre_comercial,
    ruc,
    correo,
    telefono,
    direccion,
    estado
)
VALUES (
    'FlowB2B Demo S.A.C.',
    'FlowB2B Demo',
    '20999999991',
    'contacto@flowb2b.demo',
    '999999999',
    'Lima, Perú',
    1
);

SET @empresa_id = LAST_INSERT_ID();


-- ============================================================
-- 2. CONFIGURACIÓN DE EMPRESA
-- ============================================================

INSERT INTO configuracion_empresa (
    empresa_id,
    descuento_maximo_vendedor,
    dias_vigencia_cotizacion,
    moneda,
    prefijo_solicitud,
    prefijo_cotizacion,
    prefijo_pedido
)
VALUES (
    @empresa_id,
    10.00,
    15,
    'PEN',
    'SOL',
    'COT',
    'PED'
);


-- ============================================================
-- 3. PERMISOS
-- ============================================================

INSERT INTO permiso (
    codigo,
    nombre,
    descripcion
)
VALUES

(
    'USUARIO_VER',
    'Ver usuarios',
    'Permite consultar usuarios'
),

(
    'USUARIO_CREAR',
    'Crear usuarios',
    'Permite registrar usuarios'
),

(
    'EMPRESA_VER',
    'Ver empresa',
    'Permite consultar información de la empresa'
),

(
    'EMPRESA_EDITAR',
    'Editar empresa',
    'Permite modificar información de la empresa'
),

(
    'CLIENTE_VER',
    'Ver clientes',
    'Permite consultar clientes'
),

(
    'CLIENTE_CREAR',
    'Crear clientes',
    'Permite registrar clientes'
),

(
    'CLIENTE_EDITAR',
    'Editar clientes',
    'Permite modificar clientes'
),

(
    'PRODUCTO_VER',
    'Ver productos',
    'Permite consultar productos'
),

(
    'PRODUCTO_CREAR',
    'Crear productos',
    'Permite registrar productos'
),

(
    'PRODUCTO_EDITAR',
    'Editar productos',
    'Permite modificar productos'
),

(
    'INVENTARIO_VER',
    'Ver inventario',
    'Permite consultar stock'
),

(
    'INVENTARIO_EDITAR',
    'Editar inventario',
    'Permite modificar stock'
),

(
    'SOLICITUD_VER',
    'Ver solicitudes',
    'Permite consultar solicitudes'
),

(
    'SOLICITUD_CREAR',
    'Crear solicitudes',
    'Permite registrar solicitudes'
),

(
    'COTIZACION_VER',
    'Ver cotizaciones',
    'Permite consultar cotizaciones'
),

(
    'COTIZACION_CREAR',
    'Crear cotizaciones',
    'Permite registrar cotizaciones'
),

(
    'COTIZACION_EDITAR',
    'Editar cotizaciones',
    'Permite modificar cotizaciones'
),

(
    'COTIZACION_APROBAR',
    'Aprobar cotizaciones',
    'Permite aprobar o rechazar cotizaciones'
),

(
    'PEDIDO_VER',
    'Ver pedidos',
    'Permite consultar pedidos'
),

(
    'PEDIDO_GESTIONAR',
    'Gestionar pedidos',
    'Permite cambiar estados del pedido'
),

(
    'TAREA_VER',
    'Ver tareas',
    'Permite consultar tareas operativas'
),

(
    'TAREA_GESTIONAR',
    'Gestionar tareas',
    'Permite crear o actualizar tareas'
),

(
    'NOTIFICACION_VER',
    'Ver notificaciones',
    'Permite consultar notificaciones'
),

(
    'DASHBOARD_GERENCIAL_VER',
    'Ver dashboard gerencial',
    'Permite consultar indicadores y analítica gerencial'
),

(
    'ADMIN_USUARIO_GESTIONAR',
    'Gestionar usuarios',
    'Permite administrar usuarios y asignar roles'
),

(
    'ADMIN_ROL_GESTIONAR',
    'Gestionar roles',
    'Permite crear roles y asignar permisos'
)

ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    descripcion = VALUES(descripcion);


-- ============================================================
-- 4. ROLES DE LA EMPRESA DEMO
-- ============================================================

INSERT INTO rol (
    empresa_id,
    nombre,
    descripcion,
    estado
)
VALUES (
    @empresa_id,
    'ADMINISTRADOR',
    'Acceso completo al entorno demo',
    1
);

SET @rol_admin = LAST_INSERT_ID();


INSERT INTO rol (
    empresa_id,
    nombre,
    descripcion,
    estado
)
VALUES (
    @empresa_id,
    'VENDEDOR',
    'Gestiona clientes, solicitudes y cotizaciones',
    1
);

SET @rol_vendedor = LAST_INSERT_ID();


INSERT INTO rol (
    empresa_id,
    nombre,
    descripcion,
    estado
)
VALUES (
    @empresa_id,
    'JEFE_COMERCIAL',
    'Aprueba cotizaciones y supervisa ventas',
    1
);

SET @rol_jefe = LAST_INSERT_ID();


-- ============================================================
-- 5. PERMISOS DEL ADMINISTRADOR
-- Todos los permisos existentes
-- ============================================================

INSERT INTO rol_permiso (
    rol_id,
    permiso_id
)
SELECT
    @rol_admin,
    id_permiso
FROM permiso;


-- ============================================================
-- 6. PERMISOS DEL VENDEDOR
-- ============================================================

INSERT INTO rol_permiso (
    rol_id,
    permiso_id
)
SELECT
    @rol_vendedor,
    id_permiso
FROM permiso
WHERE codigo IN (

    'CLIENTE_VER',
    'CLIENTE_CREAR',
    'CLIENTE_EDITAR',

    'PRODUCTO_VER',

    'INVENTARIO_VER',

    'SOLICITUD_VER',
    'SOLICITUD_CREAR',

    'COTIZACION_VER',
    'COTIZACION_CREAR',
    'COTIZACION_EDITAR',

    'PEDIDO_VER',

    'TAREA_VER',

    'NOTIFICACION_VER'
);


-- ============================================================
-- 7. PERMISOS DEL JEFE COMERCIAL
-- ============================================================

INSERT INTO rol_permiso (
    rol_id,
    permiso_id
)
SELECT
    @rol_jefe,
    id_permiso
FROM permiso
WHERE codigo IN (

    'EMPRESA_VER',

    'CLIENTE_VER',

    'PRODUCTO_VER',

    'INVENTARIO_VER',

    'SOLICITUD_VER',

    'COTIZACION_VER',
    'COTIZACION_APROBAR',

    'PEDIDO_VER',
    'PEDIDO_GESTIONAR',

    'TAREA_VER',
    'TAREA_GESTIONAR',

    'NOTIFICACION_VER',

    'DASHBOARD_GERENCIAL_VER'
);


-- ============================================================
-- 8. USUARIO ADMINISTRADOR DEMO
-- ============================================================
--
-- Contraseña original:
-- Admin123*
--
-- Hash BCrypt.
-- El backend utiliza BCryptPasswordEncoder.
--
-- ============================================================

INSERT INTO usuario (
    empresa_id,
    nombre,
    apellido,
    correo,
    password_hash,
    estado
)
VALUES (
    @empresa_id,
    'Administrador',
    'Demo',
    'admin@flowb2b.demo',

    '$2y$10$j3xVHsc8r/tP1D8DeDPncutp0CTF.sjSSJXpYl1XRH0vB4868Pak2',

    1
);

SET @usuario_admin = LAST_INSERT_ID();


-- Rol administrador
INSERT INTO usuario_rol (
    usuario_id,
    rol_id
)
VALUES (
    @usuario_admin,
    @rol_admin
);


-- ============================================================
-- 9. USUARIO VENDEDOR DEMO
-- ============================================================

INSERT INTO usuario (
    empresa_id,
    nombre,
    apellido,
    correo,
    password_hash,
    estado
)
VALUES (
    @empresa_id,
    'Carlos',
    'Demo',
    'vendedor@flowb2b.demo',

    '$2y$10$j3xVHsc8r/tP1D8DeDPncutp0CTF.sjSSJXpYl1XRH0vB4868Pak2',

    1
);

SET @usuario_vendedor = LAST_INSERT_ID();


INSERT INTO usuario_rol (
    usuario_id,
    rol_id
)
VALUES (
    @usuario_vendedor,
    @rol_vendedor
);


-- ============================================================
-- 10. USUARIO JEFE COMERCIAL DEMO
-- ============================================================

INSERT INTO usuario (
    empresa_id,
    nombre,
    apellido,
    correo,
    password_hash,
    estado
)
VALUES (
    @empresa_id,
    'María',
    'Demo',
    'jefe@flowb2b.demo',

    '$2y$10$j3xVHsc8r/tP1D8DeDPncutp0CTF.sjSSJXpYl1XRH0vB4868Pak2',

    1
);

SET @usuario_jefe = LAST_INSERT_ID();


INSERT INTO usuario_rol (
    usuario_id,
    rol_id
)
VALUES (
    @usuario_jefe,
    @rol_jefe
);


-- ============================================================
-- 11. CATEGORÍAS DEMO
-- ============================================================

INSERT INTO categoria_producto (
    empresa_id,
    nombre,
    descripcion,
    estado
)
VALUES (
    @empresa_id,
    'Laptops',
    'Equipos portátiles para uso empresarial',
    1
);

SET @categoria_laptop = LAST_INSERT_ID();


INSERT INTO categoria_producto (
    empresa_id,
    nombre,
    descripcion,
    estado
)
VALUES (
    @empresa_id,
    'Monitores',
    'Monitores y pantallas para estaciones de trabajo',
    1
);

SET @categoria_monitor = LAST_INSERT_ID();


-- ============================================================
-- 12. PRODUCTOS DEMO
-- ============================================================

INSERT INTO producto (
    empresa_id,
    categoria_id,
    codigo,
    nombre,
    descripcion,
    unidad_medida,
    precio_base,
    estado
)
VALUES (
    @empresa_id,
    @categoria_laptop,
    'LAP-001',
    'Laptop Empresarial 15',
    'Laptop de demostración para entorno FlowB2B',
    'UNIDAD',
    3499.90,
    1
);

SET @producto_laptop = LAST_INSERT_ID();


INSERT INTO producto (
    empresa_id,
    categoria_id,
    codigo,
    nombre,
    descripcion,
    unidad_medida,
    precio_base,
    estado
)
VALUES (
    @empresa_id,
    @categoria_monitor,
    'MON-001',
    'Monitor Empresarial 24',
    'Monitor Full HD de demostración',
    'UNIDAD',
    699.90,
    1
);

SET @producto_monitor = LAST_INSERT_ID();


-- ============================================================
-- 13. INVENTARIO DEMO
-- ============================================================

INSERT INTO inventario (
    empresa_id,
    producto_id,
    stock_actual,
    stock_reservado,
    stock_minimo
)
VALUES (
    @empresa_id,
    @producto_laptop,
    30.00,
    0.00,
    5.00
);


INSERT INTO inventario (
    empresa_id,
    producto_id,
    stock_actual,
    stock_reservado,
    stock_minimo
)
VALUES (
    @empresa_id,
    @producto_monitor,
    50.00,
    0.00,
    10.00
);


-- ============================================================
-- 14. CLIENTE DEMO
-- ============================================================

INSERT INTO cliente (
    empresa_id,
    tipo_documento,
    numero_documento,
    razon_social,
    nombre_comercial,
    correo,
    telefono,
    direccion,
    contacto_nombre,
    contacto_telefono,
    contacto_correo,
    estado
)
VALUES (
    @empresa_id,
    'RUC',
    '20999999992',
    'Cliente Demo S.A.C.',
    'Cliente Demo',
    'ventas@clientedemo.test',
    '988888888',
    'Lima, Perú',
    'Ana Demo',
    '977777777',
    'ana@clientedemo.test',
    1
);


COMMIT;


-- ============================================================
-- COMPROBACIÓN
-- ============================================================

SELECT
    'Seed FlowB2B cargado correctamente' AS resultado;


SELECT
    id_empresa,
    razon_social,
    nombre_comercial
FROM empresa
WHERE id_empresa = @empresa_id;


SELECT
    u.id_usuario,
    u.nombre,
    u.apellido,
    u.correo,
    r.nombre AS rol
FROM usuario u
JOIN usuario_rol ur
    ON ur.usuario_id = u.id_usuario
JOIN rol r
    ON r.id_rol = ur.rol_id
WHERE u.empresa_id = @empresa_id;


SELECT
    p.codigo,
    p.nombre
FROM permiso p
JOIN rol_permiso rp
    ON rp.permiso_id = p.id_permiso
WHERE rp.rol_id = @rol_admin
ORDER BY p.codigo;