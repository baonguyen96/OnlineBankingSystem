/*
 * MySQL Workbench setup:
 * 		Username: root
 *		Password: password
 */

-- create new database
CREATE DATABASE IF NOT EXISTS `cs6359` DEFAULT CHARACTER SET utf8 ;

USE `cs6359`;

-- create tables
CREATE TABLE IF NOT EXISTS `cs6359`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `recover_password_question` varchar(300) NOT NULL,
  `recover_password_answer` varchar(300) NOT NULL,
  PRIMARY KEY (`user_id`));

CREATE TABLE IF NOT EXISTS `cs6359`.`transactions` (
  `transaction_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `type` VARCHAR(50) NOT NULL,	-- Deposit, Withdraw, Transfer - Add, Transfer - Receive 
  `amount` DECIMAL(65,2) NOT NULL,
  `updated_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaction_id`));

CREATE TABLE `accounts` (
   `account_id` int(11) NOT NULL AUTO_INCREMENT,
   `user_id` int(11) NOT NULL,
   `balance` decimal(65,2) NOT NULL,
   `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`account_id`)); 
