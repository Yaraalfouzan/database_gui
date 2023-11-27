-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.28-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for grocery stock management system
CREATE DATABASE IF NOT EXISTS `grocery stock management system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `grocery stock management system`;

-- Dumping structure for table grocery stock management system.account
CREATE TABLE IF NOT EXISTS `account` (
  `userName` varchar(15) NOT NULL,
  `Acc_phoneNum` varchar(10) NOT NULL,
  `posints` int(11) NOT NULL,
  PRIMARY KEY (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.account: ~0 rows (approximately)
DELETE FROM `account`;

-- Dumping structure for table grocery stock management system.branch
CREATE TABLE IF NOT EXISTS `branch` (
  `B_id` int(11) NOT NULL AUTO_INCREMENT,
  `ZIP` int(11) NOT NULL DEFAULT 0,
  `City` varchar(15) NOT NULL DEFAULT '0',
  `B_address` varchar(50) NOT NULL DEFAULT '0',
  `b_phonenum` varchar(10) NOT NULL DEFAULT '0',
  `EMP_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`B_id`),
  KEY `EMP` (`EMP_id`),
  CONSTRAINT `EMP` FOREIGN KEY (`EMP_id`) REFERENCES `employee` (`E_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.branch: ~0 rows (approximately)
DELETE FROM `branch`;

-- Dumping structure for table grocery stock management system.employee
CREATE TABLE IF NOT EXISTS `employee` (
  `E_id` int(11) NOT NULL AUTO_INCREMENT,
  `E_salary` int(11) NOT NULL,
  `Fname` varchar(50) NOT NULL DEFAULT '',
  `Lname` varchar(50) NOT NULL DEFAULT '',
  `E_shift` time NOT NULL,
  `E_phonenum` varchar(10) NOT NULL DEFAULT '',
  `E_position` varchar(20) NOT NULL DEFAULT '',
  `B_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`E_id`),
  KEY `FK_employee_branch` (`B_id`),
  CONSTRAINT `FK_employee_branch` FOREIGN KEY (`B_id`) REFERENCES `branch` (`B_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.employee: ~0 rows (approximately)
DELETE FROM `employee`;

-- Dumping structure for table grocery stock management system.in
CREATE TABLE IF NOT EXISTS `in` (
  `ProIN_id` int(11) DEFAULT NULL,
  `Bra_id` int(11) DEFAULT NULL,
  KEY `FK__proucat` (`ProIN_id`),
  KEY `Bra_id` (`Bra_id`),
  CONSTRAINT `Bra_id` FOREIGN KEY (`Bra_id`) REFERENCES `branch` (`B_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK__proucat` FOREIGN KEY (`ProIN_id`) REFERENCES `product` (`P_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.in: ~0 rows (approximately)
DELETE FROM `in`;

-- Dumping structure for table grocery stock management system.invoice
CREATE TABLE IF NOT EXISTS `invoice` (
  `InoiceNum` int(11) NOT NULL,
  `Totl_Price` int(11) NOT NULL,
  `Acc_userName` varchar(15) NOT NULL,
  PRIMARY KEY (`InoiceNum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.invoice: ~0 rows (approximately)
DELETE FROM `invoice`;

-- Dumping structure for table grocery stock management system.product
CREATE TABLE IF NOT EXISTS `product` (
  `P_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `P_brand` varchar(20) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `p_type` varchar(20) DEFAULT NULL,
  `Ex_date` date DEFAULT NULL,
  `Pro_date` date DEFAULT NULL,
  `S_id` int(11) DEFAULT NULL,
  `Product_quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`P_id`),
  KEY `FK_product_supplier` (`S_id`),
  CONSTRAINT `FK_product_supplier` FOREIGN KEY (`S_id`) REFERENCES `supplier` (`S_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.product: ~0 rows (approximately)
DELETE FROM `product`;

-- Dumping structure for table grocery stock management system.purchased_by
CREATE TABLE IF NOT EXISTS `purchased_by` (
  `pro_id` int(11) DEFAULT NULL,
  `InvoiceNumber` int(11) DEFAULT NULL,
  `Product_quantity` int(11) DEFAULT NULL,
  KEY `FK_purchased_by_proucat` (`pro_id`),
  KEY `FK_purchased_by_invoice` (`InvoiceNumber`),
  CONSTRAINT `FK_purchased_by_invoice` FOREIGN KEY (`InvoiceNumber`) REFERENCES `invoice` (`InoiceNum`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_purchased_by_proucat` FOREIGN KEY (`pro_id`) REFERENCES `product` (`P_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.purchased_by: ~0 rows (approximately)
DELETE FROM `purchased_by`;

-- Dumping structure for table grocery stock management system.supplier
CREATE TABLE IF NOT EXISTS `supplier` (
  `S_id` int(11) NOT NULL AUTO_INCREMENT,
  `S_name` varchar(20) NOT NULL,
  `S_location` varchar(50) NOT NULL,
  `S_phonenum` varchar(10) NOT NULL,
  PRIMARY KEY (`S_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table grocery stock management system.supplier: ~0 rows (approximately)
DELETE FROM `supplier`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
