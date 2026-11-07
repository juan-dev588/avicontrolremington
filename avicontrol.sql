-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: avicontrol
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  `observaciones` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `configuracion`
--

DROP TABLE IF EXISTS `configuracion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `precio_por_kilo` decimal(12,2) NOT NULL,
  `fecha_actualizacion` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gastos`
--

DROP TABLE IF EXISTS `gastos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gastos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lote_id` int NOT NULL,
  `categoria` varchar(50) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `valor` decimal(12,2) NOT NULL,
  `fecha` date NOT NULL,
  `observaciones` text,
  PRIMARY KEY (`id`),
  KEY `lote_id` (`lote_id`),
  CONSTRAINT `gastos_ibfk_1` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `historial_movimientos`
--

DROP TABLE IF EXISTS `historial_movimientos`;
/*!50001 DROP VIEW IF EXISTS `historial_movimientos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `historial_movimientos` AS SELECT 
 1 AS `fecha`,
 1 AS `lote`,
 1 AS `tipo`,
 1 AS `detalle`,
 1 AS `valor`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `lotes`
--

DROP TABLE IF EXISTS `lotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `cantidad_inicial` int NOT NULL,
  `precio_por_pollito` decimal(12,2) NOT NULL DEFAULT '0.00',
  `costo_pollitos` decimal(12,2) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'En crianza',
  `observaciones` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mortalidad`
--

DROP TABLE IF EXISTS `mortalidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mortalidad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lote_id` int NOT NULL,
  `cantidad` int NOT NULL,
  `fecha` date NOT NULL,
  `motivo` varchar(100) DEFAULT NULL,
  `observaciones` text,
  PRIMARY KEY (`id`),
  KEY `lote_id` (`lote_id`),
  CONSTRAINT `mortalidad_ibfk_1` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagos`
--

DROP TABLE IF EXISTS `pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `venta_id` int NOT NULL,
  `valor` decimal(12,2) NOT NULL,
  `fecha` date NOT NULL,
  `metodo_pago` varchar(40) DEFAULT 'Efectivo',
  `observaciones` text,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_pagos_venta` (`venta_id`),
  CONSTRAINT `fk_pagos_venta` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_pagos_valor` CHECK ((`valor` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `resumen_lotes`
--

DROP TABLE IF EXISTS `resumen_lotes`;
/*!50001 DROP VIEW IF EXISTS `resumen_lotes`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `resumen_lotes` AS SELECT 
 1 AS `lote`,
 1 AS `pollos_iniciales`,
 1 AS `pollos_muertos`,
 1 AS `pollos_disponibles`,
 1 AS `pollos_vendidos`,
 1 AS `kilos_vendidos`,
 1 AS `ventas_totales`,
 1 AS `costo_pollitos`,
 1 AS `otros_gastos`,
 1 AS `inversion_total`,
 1 AS `ganancia_actual`,
 1 AS `precio_actual_por_kilo`,
 1 AS `kilos_para_punto_equilibrio`,
 1 AS `porcentaje_mortalidad`,
 1 AS `costo_por_pollo`,
 1 AS `porcentaje_pollos_vendidos`,
 1 AS `porcentaje_recuperacion`,
 1 AS `ganancia_por_kilo`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lote_id` int NOT NULL,
  `cantidad_pollos` int NOT NULL,
  `peso_total` decimal(10,2) NOT NULL,
  `precio_por_kilo` decimal(12,2) NOT NULL,
  `total_venta` decimal(12,2) NOT NULL,
  `fecha` date NOT NULL,
  `observaciones` text,
  `cliente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lote_id` (`lote_id`),
  KEY `fk_ventas_cliente` (`cliente_id`),
  CONSTRAINT `fk_ventas_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = cp850 */ ;
/*!50003 SET character_set_results = cp850 */ ;
/*!50003 SET collation_connection  = cp850_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `calcular_total_venta` BEFORE INSERT ON `ventas` FOR EACH ROW BEGIN
    SET NEW.total_venta = NEW.peso_total * NEW.precio_por_kilo;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = cp850 */ ;
/*!50003 SET character_set_results = cp850 */ ;
/*!50003 SET collation_connection  = cp850_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `validar_cantidad_venta` BEFORE INSERT ON `ventas` FOR EACH ROW BEGIN
    DECLARE pollos_disponibles INT;

    SELECT
        cantidad_inicial
        - COALESCE((
            SELECT SUM(cantidad)
            FROM mortalidad
            WHERE lote_id = NEW.lote_id
        ), 0)
        - COALESCE((
            SELECT SUM(cantidad_pollos)
            FROM ventas
            WHERE lote_id = NEW.lote_id
        ), 0)
    INTO pollos_disponibles
    FROM lotes
    WHERE id = NEW.lote_id;

    IF NEW.cantidad_pollos > pollos_disponibles THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede realizar la venta: no hay suficientes pollos disponibles';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `historial_movimientos`
--

/*!50001 DROP VIEW IF EXISTS `historial_movimientos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `historial_movimientos` AS select `g`.`fecha` AS `fecha`,`l`.`codigo` AS `lote`,'GASTO' AS `tipo`,`g`.`categoria` AS `detalle`,`g`.`valor` AS `valor` from (`gastos` `g` join `lotes` `l` on((`g`.`lote_id` = `l`.`id`))) union all select `m`.`fecha` AS `fecha`,`l`.`codigo` AS `lote`,'MORTALIDAD' AS `tipo`,concat(`m`.`cantidad`,' pollos muertos - ',coalesce(`m`.`motivo`,'Sin motivo')) AS `detalle`,0 AS `valor` from (`mortalidad` `m` join `lotes` `l` on((`m`.`lote_id` = `l`.`id`))) union all select `v`.`fecha` AS `fecha`,`l`.`codigo` AS `lote`,'VENTA' AS `tipo`,concat(`v`.`cantidad_pollos`,' pollos - ',`v`.`peso_total`,' kg') AS `detalle`,`v`.`total_venta` AS `valor` from (`ventas` `v` join `lotes` `l` on((`v`.`lote_id` = `l`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `resumen_lotes`
--

/*!50001 DROP VIEW IF EXISTS `resumen_lotes`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `resumen_lotes` AS select `l`.`codigo` AS `lote`,`l`.`cantidad_inicial` AS `pollos_iniciales`,coalesce((select sum(`m`.`cantidad`) from `mortalidad` `m` where (`m`.`lote_id` = `l`.`id`)),0) AS `pollos_muertos`,((`l`.`cantidad_inicial` - coalesce((select sum(`m`.`cantidad`) from `mortalidad` `m` where (`m`.`lote_id` = `l`.`id`)),0)) - coalesce((select sum(`v`.`cantidad_pollos`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0)) AS `pollos_disponibles`,coalesce((select sum(`v`.`cantidad_pollos`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) AS `pollos_vendidos`,coalesce((select sum(`v`.`peso_total`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) AS `kilos_vendidos`,coalesce((select sum(`v`.`total_venta`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) AS `ventas_totales`,`l`.`costo_pollitos` AS `costo_pollitos`,coalesce((select sum(`g`.`valor`) from `gastos` `g` where (`g`.`lote_id` = `l`.`id`)),0) AS `otros_gastos`,(`l`.`costo_pollitos` + coalesce((select sum(`g`.`valor`) from `gastos` `g` where (`g`.`lote_id` = `l`.`id`)),0)) AS `inversion_total`,(coalesce((select sum(`v`.`total_venta`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) - (`l`.`costo_pollitos` + coalesce((select sum(`g`.`valor`) from `gastos` `g` where (`g`.`lote_id` = `l`.`id`)),0))) AS `ganancia_actual`,(select `configuracion`.`precio_por_kilo` from `configuracion` where (`configuracion`.`id` = 1)) AS `precio_actual_por_kilo`,round(((`l`.`costo_pollitos` + coalesce((select sum(`g`.`valor`) from `gastos` `g` where (`g`.`lote_id` = `l`.`id`)),0)) / nullif((select `configuracion`.`precio_por_kilo` from `configuracion` where (`configuracion`.`id` = 1)),0)),2) AS `kilos_para_punto_equilibrio`,round(((coalesce((select sum(`m`.`cantidad`) from `mortalidad` `m` where (`m`.`lote_id` = `l`.`id`)),0) * 100.0) / nullif(`l`.`cantidad_inicial`,0)),2) AS `porcentaje_mortalidad`,round(((`l`.`costo_pollitos` + coalesce((select sum(`g`.`valor`) from `gastos` `g` where (`g`.`lote_id` = `l`.`id`)),0)) / nullif((`l`.`cantidad_inicial` - coalesce((select sum(`m`.`cantidad`) from `mortalidad` `m` where (`m`.`lote_id` = `l`.`id`)),0)),0)),2) AS `costo_por_pollo`,round(((coalesce((select sum(`v`.`cantidad_pollos`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) * 100.0) / nullif(`l`.`cantidad_inicial`,0)),2) AS `porcentaje_pollos_vendidos`,round(((coalesce((select sum(`v`.`total_venta`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) * 100.0) / nullif((`l`.`costo_pollitos` + coalesce((select sum(`g`.`valor`) from `gastos` `g` where (`g`.`lote_id` = `l`.`id`)),0)),0)),2) AS `porcentaje_recuperacion`,round((coalesce((select sum(`v`.`total_venta`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0) / nullif((select sum(`v`.`peso_total`) from `ventas` `v` where (`v`.`lote_id` = `l`.`id`)),0)),2) AS `ganancia_por_kilo` from `lotes` `l` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-05 23:42:50
