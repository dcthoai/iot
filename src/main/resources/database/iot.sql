CREATE DATABASE IF NOT EXISTS `iot`
USE `iot`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)

DROP TABLE IF EXISTS `esp32`;

CREATE TABLE `esp32` (
  `id` int NOT NULL AUTO_INCREMENT,
  `time_refresh` bigint DEFAULT '5000',
  `led_status` int DEFAULT '1',
  `lcd_status` int DEFAULT '1',
  `time_analyze` bigint DEFAULT '60',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `esp32` WRITE;
INSERT INTO `esp32` VALUES (1,2000,1,1,60,'2024-10-13 15:04:34');
UNLOCK TABLES;

DROP TABLE IF EXISTS `sensor`;
CREATE TABLE `sensor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `temperature` float NOT NULL DEFAULT '0',
  `humidity` float NOT NULL DEFAULT '0',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `INDX_CREATED_DATE` (`created_date`)
) ENGINE=InnoDB AUTO_INCREMENT=5565 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
