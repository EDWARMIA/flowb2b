-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: flowb2b_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aprobacion`
--

DROP TABLE IF EXISTS `aprobacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aprobacion` (
  `id_aprobacion` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `cotizacion_id` bigint NOT NULL,
  `solicitante_id` bigint NOT NULL,
  `aprobador_id` bigint DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `motivo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comentario` text COLLATE utf8mb4_unicode_ci,
  `fecha_solicitud` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_respuesta` datetime DEFAULT NULL,
  PRIMARY KEY (`id_aprobacion`),
  KEY `idx_aprobacion_empresa` (`empresa_id`),
  KEY `idx_aprobacion_cotizacion` (`cotizacion_id`),
  KEY `idx_aprobacion_solicitante` (`solicitante_id`),
  KEY `idx_aprobacion_aprobador` (`aprobador_id`),
  KEY `idx_aprobacion_estado` (`empresa_id`,`estado`),
  CONSTRAINT `fk_aprobacion_aprobador` FOREIGN KEY (`aprobador_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_aprobacion_cotizacion` FOREIGN KEY (`cotizacion_id`) REFERENCES `cotizacion` (`id_cotizacion`),
  CONSTRAINT `fk_aprobacion_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_aprobacion_solicitante` FOREIGN KEY (`solicitante_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id_auditoria` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `usuario_id` bigint DEFAULT NULL,
  `accion` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entidad` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entidad_id` bigint DEFAULT NULL,
  `datos_anteriores` json DEFAULT NULL,
  `datos_nuevos` json DEFAULT NULL,
  `ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_auditoria`),
  KEY `idx_auditoria_empresa` (`empresa_id`),
  KEY `idx_auditoria_usuario` (`usuario_id`),
  KEY `idx_auditoria_entidad` (`empresa_id`,`entidad`,`entidad_id`),
  KEY `idx_auditoria_fecha` (`fecha`),
  CONSTRAINT `fk_auditoria_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categoria_producto`
--

DROP TABLE IF EXISTS `categoria_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria_producto` (
  `id_categoria` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `uk_categoria_empresa_nombre` (`empresa_id`,`nombre`),
  KEY `idx_categoria_empresa` (`empresa_id`),
  CONSTRAINT `fk_categoria_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `tipo_documento` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_comercial` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacto_nombre` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacto_telefono` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacto_correo` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `uk_cliente_empresa_documento` (`empresa_id`,`numero_documento`),
  KEY `idx_cliente_empresa` (`empresa_id`),
  KEY `idx_cliente_razon_social` (`razon_social`),
  CONSTRAINT `fk_cliente_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `configuracion_empresa`
--

DROP TABLE IF EXISTS `configuracion_empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_empresa` (
  `id_configuracion` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `descuento_maximo_vendedor` decimal(5,2) NOT NULL DEFAULT '0.00',
  `dias_vigencia_cotizacion` int NOT NULL DEFAULT '15',
  `moneda` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PEN',
  `prefijo_solicitud` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SOL',
  `prefijo_cotizacion` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'COT',
  `prefijo_pedido` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PED',
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_configuracion`),
  UNIQUE KEY `uk_configuracion_empresa` (`empresa_id`),
  CONSTRAINT `fk_configuracion_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `chk_config_descuento` CHECK (((`descuento_maximo_vendedor` >= 0) and (`descuento_maximo_vendedor` <= 100))),
  CONSTRAINT `chk_config_vigencia` CHECK ((`dias_vigencia_cotizacion` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cotizacion`
--

DROP TABLE IF EXISTS `cotizacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cotizacion` (
  `id_cotizacion` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `solicitud_id` bigint NOT NULL,
  `cliente_id` bigint NOT NULL,
  `vendedor_id` bigint NOT NULL,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `subtotal` decimal(14,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(14,2) NOT NULL DEFAULT '0.00',
  `impuesto` decimal(14,2) NOT NULL DEFAULT '0.00',
  `total` decimal(14,2) NOT NULL DEFAULT '0.00',
  `porcentaje_descuento` decimal(5,2) NOT NULL DEFAULT '0.00',
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BORRADOR',
  `fecha_emision` datetime DEFAULT NULL,
  `fecha_vencimiento` datetime DEFAULT NULL,
  `fecha_aceptacion` datetime DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cotizacion`),
  UNIQUE KEY `uk_cotizacion_empresa_codigo` (`empresa_id`,`codigo`),
  KEY `idx_cotizacion_empresa` (`empresa_id`),
  KEY `idx_cotizacion_solicitud` (`solicitud_id`),
  KEY `idx_cotizacion_cliente` (`cliente_id`),
  KEY `idx_cotizacion_vendedor` (`vendedor_id`),
  KEY `idx_cotizacion_estado` (`empresa_id`,`estado`),
  CONSTRAINT `fk_cotizacion_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `fk_cotizacion_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_cotizacion_solicitud` FOREIGN KEY (`solicitud_id`) REFERENCES `solicitud` (`id_solicitud`),
  CONSTRAINT `fk_cotizacion_vendedor` FOREIGN KEY (`vendedor_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalle_cotizacion`
--

DROP TABLE IF EXISTS `detalle_cotizacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_cotizacion` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `cotizacion_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad` decimal(14,2) NOT NULL,
  `precio_unitario` decimal(14,2) NOT NULL,
  `porcentaje_descuento` decimal(5,2) NOT NULL DEFAULT '0.00',
  `subtotal` decimal(14,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_detalle_cotizacion_cotizacion` (`cotizacion_id`),
  KEY `idx_detalle_cotizacion_producto` (`producto_id`),
  CONSTRAINT `fk_detalle_cotizacion_cotizacion` FOREIGN KEY (`cotizacion_id`) REFERENCES `cotizacion` (`id_cotizacion`),
  CONSTRAINT `fk_detalle_cotizacion_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `chk_detalle_cotizacion_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `chk_detalle_cotizacion_descuento` CHECK (((`porcentaje_descuento` >= 0) and (`porcentaje_descuento` <= 100))),
  CONSTRAINT `chk_detalle_cotizacion_precio` CHECK ((`precio_unitario` >= 0)),
  CONSTRAINT `chk_detalle_cotizacion_subtotal` CHECK ((`subtotal` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_pedido` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad` decimal(14,2) NOT NULL,
  `precio_unitario` decimal(14,2) NOT NULL,
  `cantidad_reservada` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cantidad_pendiente` decimal(14,2) NOT NULL DEFAULT '0.00',
  `subtotal` decimal(14,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_detalle_pedido_pedido` (`pedido_id`),
  KEY `idx_detalle_pedido_producto` (`producto_id`),
  CONSTRAINT `fk_detalle_pedido_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id_pedido`),
  CONSTRAINT `fk_detalle_pedido_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `chk_detalle_pedido_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `chk_detalle_pedido_pendiente` CHECK ((`cantidad_pendiente` >= 0)),
  CONSTRAINT `chk_detalle_pedido_precio` CHECK ((`precio_unitario` >= 0)),
  CONSTRAINT `chk_detalle_pedido_reserva_max` CHECK ((`cantidad_reservada` <= `cantidad`)),
  CONSTRAINT `chk_detalle_pedido_reservada` CHECK ((`cantidad_reservada` >= 0)),
  CONSTRAINT `chk_detalle_pedido_subtotal` CHECK ((`subtotal` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalle_solicitud`
--

DROP TABLE IF EXISTS `detalle_solicitud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_solicitud` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `solicitud_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `cantidad` decimal(14,2) NOT NULL,
  `observacion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_detalle_solicitud_solicitud` (`solicitud_id`),
  KEY `idx_detalle_solicitud_producto` (`producto_id`),
  CONSTRAINT `fk_detalle_solicitud_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `fk_detalle_solicitud_solicitud` FOREIGN KEY (`solicitud_id`) REFERENCES `solicitud` (`id_solicitud`),
  CONSTRAINT `chk_detalle_solicitud_cantidad` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `empresa`
--

DROP TABLE IF EXISTS `empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empresa` (
  `id_empresa` bigint NOT NULL AUTO_INCREMENT,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_comercial` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ruc` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_empresa`),
  UNIQUE KEY `uk_empresa_ruc` (`ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `historial_estado`
--

DROP TABLE IF EXISTS `historial_estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_estado` (
  `id_historial` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `tipo_entidad` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entidad_id` bigint NOT NULL,
  `estado_anterior` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_nuevo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `usuario_id` bigint NOT NULL,
  `comentario` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_historial`),
  KEY `idx_historial_empresa` (`empresa_id`),
  KEY `idx_historial_entidad` (`empresa_id`,`tipo_entidad`,`entidad_id`),
  KEY `idx_historial_usuario` (`usuario_id`),
  KEY `idx_historial_fecha` (`fecha`),
  CONSTRAINT `fk_historial_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_historial_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `id_inventario` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `stock_actual` decimal(14,2) NOT NULL DEFAULT '0.00',
  `stock_reservado` decimal(14,2) NOT NULL DEFAULT '0.00',
  `stock_minimo` decimal(14,2) NOT NULL DEFAULT '0.00',
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_inventario`),
  UNIQUE KEY `uk_inventario_empresa_producto` (`empresa_id`,`producto_id`),
  KEY `idx_inventario_empresa` (`empresa_id`),
  KEY `idx_inventario_producto` (`producto_id`),
  CONSTRAINT `fk_inventario_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_inventario_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `chk_stock_actual` CHECK ((`stock_actual` >= 0)),
  CONSTRAINT `chk_stock_minimo` CHECK ((`stock_minimo` >= 0)),
  CONSTRAINT `chk_stock_reservado` CHECK ((`stock_reservado` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificacion` (
  `id_notificacion` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `usuario_id` bigint NOT NULL,
  `titulo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mensaje` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entidad_tipo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entidad_id` bigint DEFAULT NULL,
  `leida` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_lectura` datetime DEFAULT NULL,
  PRIMARY KEY (`id_notificacion`),
  KEY `idx_notificacion_empresa` (`empresa_id`),
  KEY `idx_notificacion_usuario` (`usuario_id`),
  KEY `idx_notificacion_usuario_leida` (`usuario_id`,`leida`),
  CONSTRAINT `fk_notificacion_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_notificacion_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `id_pedido` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `cotizacion_id` bigint NOT NULL,
  `cliente_id` bigint NOT NULL,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CREADO',
  `subtotal` decimal(14,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(14,2) NOT NULL DEFAULT '0.00',
  `impuesto` decimal(14,2) NOT NULL DEFAULT '0.00',
  `total` decimal(14,2) NOT NULL DEFAULT '0.00',
  `fecha_pedido` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_estimada_entrega` datetime DEFAULT NULL,
  `fecha_entrega` datetime DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pedido`),
  UNIQUE KEY `uk_pedido_empresa_codigo` (`empresa_id`,`codigo`),
  UNIQUE KEY `uk_pedido_cotizacion` (`cotizacion_id`),
  KEY `idx_pedido_empresa` (`empresa_id`),
  KEY `idx_pedido_cliente` (`cliente_id`),
  KEY `idx_pedido_estado` (`empresa_id`,`estado`),
  CONSTRAINT `fk_pedido_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `fk_pedido_cotizacion` FOREIGN KEY (`cotizacion_id`) REFERENCES `cotizacion` (`id_cotizacion`),
  CONSTRAINT `fk_pedido_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `chk_pedido_descuento` CHECK ((`descuento` >= 0)),
  CONSTRAINT `chk_pedido_impuesto` CHECK ((`impuesto` >= 0)),
  CONSTRAINT `chk_pedido_subtotal` CHECK ((`subtotal` >= 0)),
  CONSTRAINT `chk_pedido_total` CHECK ((`total` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permiso`
--

DROP TABLE IF EXISTS `permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permiso` (
  `id_permiso` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_permiso`),
  UNIQUE KEY `uk_permiso_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `categoria_id` bigint NOT NULL,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `unidad_medida` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `precio_base` decimal(14,2) NOT NULL DEFAULT '0.00',
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `uk_producto_empresa_codigo` (`empresa_id`,`codigo`),
  KEY `idx_producto_empresa` (`empresa_id`),
  KEY `idx_producto_categoria` (`categoria_id`),
  KEY `idx_producto_nombre` (`nombre`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`id_categoria`),
  CONSTRAINT `fk_producto_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `nombre` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `uk_rol_empresa_nombre` (`empresa_id`,`nombre`),
  KEY `idx_rol_empresa` (`empresa_id`),
  CONSTRAINT `fk_rol_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rol_permiso`
--

DROP TABLE IF EXISTS `rol_permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_permiso` (
  `rol_id` bigint NOT NULL,
  `permiso_id` bigint NOT NULL,
  PRIMARY KEY (`rol_id`,`permiso_id`),
  KEY `fk_rol_permiso_permiso` (`permiso_id`),
  CONSTRAINT `fk_rol_permiso_permiso` FOREIGN KEY (`permiso_id`) REFERENCES `permiso` (`id_permiso`),
  CONSTRAINT `fk_rol_permiso_rol` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `solicitud`
--

DROP TABLE IF EXISTS `solicitud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solicitud` (
  `id_solicitud` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `cliente_id` bigint NOT NULL,
  `vendedor_id` bigint NOT NULL,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `origen` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NUEVA',
  `fecha_solicitud` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_solicitud`),
  UNIQUE KEY `uk_solicitud_empresa_codigo` (`empresa_id`,`codigo`),
  KEY `idx_solicitud_empresa` (`empresa_id`),
  KEY `idx_solicitud_cliente` (`cliente_id`),
  KEY `idx_solicitud_vendedor` (`vendedor_id`),
  KEY `idx_solicitud_estado` (`empresa_id`,`estado`),
  CONSTRAINT `fk_solicitud_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `fk_solicitud_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_solicitud_vendedor` FOREIGN KEY (`vendedor_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tarea`
--

DROP TABLE IF EXISTS `tarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarea` (
  `id_tarea` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `pedido_id` bigint NOT NULL,
  `responsable_id` bigint DEFAULT NULL,
  `titulo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `tipo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prioridad` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIA',
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `fecha_limite` datetime DEFAULT NULL,
  `fecha_completada` datetime DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_tarea`),
  KEY `idx_tarea_empresa` (`empresa_id`),
  KEY `idx_tarea_pedido` (`pedido_id`),
  KEY `idx_tarea_responsable` (`responsable_id`),
  KEY `idx_tarea_estado` (`empresa_id`,`estado`),
  KEY `idx_tarea_responsable_estado` (`responsable_id`,`estado`),
  CONSTRAINT `fk_tarea_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`),
  CONSTRAINT `fk_tarea_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id_pedido`),
  CONSTRAINT `fk_tarea_responsable` FOREIGN KEY (`responsable_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `empresa_id` bigint NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `ultimo_login` datetime DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uk_usuario_empresa_correo` (`empresa_id`,`correo`),
  KEY `idx_usuario_empresa` (`empresa_id`),
  CONSTRAINT `fk_usuario_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id_empresa`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario_rol`
--

DROP TABLE IF EXISTS `usuario_rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_rol` (
  `usuario_id` bigint NOT NULL,
  `rol_id` bigint NOT NULL,
  PRIMARY KEY (`usuario_id`,`rol_id`),
  KEY `fk_usuario_rol_rol` (`rol_id`),
  CONSTRAINT `fk_usuario_rol_rol` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-15 17:09:57
