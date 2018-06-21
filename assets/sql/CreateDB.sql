/*
 * MySQL Workbench setup:
 * 		Username: root
 *		Password: password
 */

-- create new database
DROP DATABASE IF EXISTS `cs6359`;
CREATE DATABASE IF NOT EXISTS `cs6359` DEFAULT CHARACTER SET utf8 ;

USE `cs6359`;

-- create tables
CREATE TABLE IF NOT EXISTS `cs6359`.`user` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(300) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `recover_password_question` VARCHAR(300) NOT NULL,
  `recover_password_answer` VARCHAR(300) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `account` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) NOT NULL,
  `name` VARCHAR(50) NOT NULL, 
  `balance` DECIMAL(65,2) NOT NULL,
  `created_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (user_id) REFERENCES `user` (`id`),
  CONSTRAINT UNIQUE (`user_id`, `name`)
);

CREATE TABLE IF NOT EXISTS `cs6359`.`transaction` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `account_id` INT(10) NOT NULL,
  `type` VARCHAR(50) NOT NULL,	-- Deposit, Withdraw, Transfer - Add, Transfer - Receive 
  `amount` DECIMAL(65,2) NOT NULL,
  `created_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (account_id) REFERENCES `account` (`id`)  
 );
