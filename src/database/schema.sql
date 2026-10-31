-- =====================================================================
-- Movie Ticket Booking System - Database Schema (MySQL)
-- Inspired by BookMyShow Architecture
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `movie_booking_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `movie_booking_db`;

-- Drop existing tables if re-creating
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `booking_seats`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `shows`;
DROP TABLE IF EXISTS `seats`;
DROP TABLE IF EXISTS `screens`;
DROP TABLE IF EXISTS `theatres`;
DROP TABLE IF EXISTS `movies`;
DROP TABLE IF EXISTS `admins`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Users Table
CREATE TABLE `users` (
  `user_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(120) NOT NULL UNIQUE,
  `phone` VARCHAR(20) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Admins Table
CREATE TABLE `admins` (
  `admin_id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(120) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) DEFAULT 'SUPER_ADMIN',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Movies Table
CREATE TABLE `movies` (
  `movie_id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(150) NOT NULL,
  `genre` VARCHAR(100) NOT NULL,
  `language` VARCHAR(50) NOT NULL,
  `duration_minutes` INT NOT NULL,
  `release_date` DATE NOT NULL,
  `certification` VARCHAR(20) DEFAULT 'U/A',
  `description` TEXT,
  `rating` DECIMAL(3, 1) DEFAULT 8.5,
  `poster_path` VARCHAR(255),
  `banner_path` VARCHAR(255),
  `status` ENUM('NOW_SHOWING', 'UPCOMING') DEFAULT 'NOW_SHOWING',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Theatres Table
CREATE TABLE `theatres` (
  `theatre_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(150) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `total_screens` INT DEFAULT 3,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Screens Table
CREATE TABLE `screens` (
  `screen_id` INT AUTO_INCREMENT PRIMARY KEY,
  `theatre_id` INT NOT NULL,
  `screen_number` INT NOT NULL,
  `screen_name` VARCHAR(50) NOT NULL,
  `total_seats` INT DEFAULT 64,
  CONSTRAINT `fk_screens_theatre` FOREIGN KEY (`theatre_id`) REFERENCES `theatres` (`theatre_id`) ON DELETE CASCADE,
  CONSTRAINT `uq_theatre_screen` UNIQUE (`theatre_id`, `screen_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Seats Table
CREATE TABLE `seats` (
  `seat_id` INT AUTO_INCREMENT PRIMARY KEY,
  `screen_id` INT NOT NULL,
  `row_char` VARCHAR(2) NOT NULL,
  `seat_number` INT NOT NULL,
  `category` ENUM('SILVER', 'GOLD', 'PREMIUM') NOT NULL,
  `base_price` DECIMAL(10, 2) NOT NULL,
  CONSTRAINT `fk_seats_screen` FOREIGN KEY (`screen_id`) REFERENCES `screens` (`screen_id`) ON DELETE CASCADE,
  CONSTRAINT `uq_screen_seat` UNIQUE (`screen_id`, `row_char`, `seat_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Shows Table
CREATE TABLE `shows` (
  `show_id` INT AUTO_INCREMENT PRIMARY KEY,
  `movie_id` INT NOT NULL,
  `screen_id` INT NOT NULL,
  `show_date` DATE NOT NULL,
  `start_time` TIME NOT NULL,
  `ticket_price` DECIMAL(10, 2) NOT NULL,
  `available_seats` INT NOT NULL,
  CONSTRAINT `fk_shows_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_shows_screen` FOREIGN KEY (`screen_id`) REFERENCES `screens` (`screen_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Bookings Table
CREATE TABLE `bookings` (
  `booking_id` INT AUTO_INCREMENT PRIMARY KEY,
  `booking_reference` VARCHAR(50) NOT NULL UNIQUE,
  `user_id` INT NOT NULL,
  `show_id` INT NOT NULL,
  `total_seats` INT NOT NULL,
  `base_amount` DECIMAL(10, 2) NOT NULL,
  `convenience_fee` DECIMAL(10, 2) NOT NULL,
  `gst_amount` DECIMAL(10, 2) NOT NULL,
  `total_amount` DECIMAL(10, 2) NOT NULL,
  `booking_status` ENUM('CONFIRMED', 'CANCELLED') DEFAULT 'CONFIRMED',
  `booking_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_bookings_show` FOREIGN KEY (`show_id`) REFERENCES `shows` (`show_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Booking Seats Table
CREATE TABLE `booking_seats` (
  `booking_seat_id` INT AUTO_INCREMENT PRIMARY KEY,
  `booking_id` INT NOT NULL,
  `show_id` INT NOT NULL,
  `seat_id` INT NOT NULL,
  `seat_label` VARCHAR(10) NOT NULL,
  `category` VARCHAR(20) NOT NULL,
  `price` DECIMAL(10, 2) NOT NULL,
  `status` ENUM('BOOKED', 'CANCELLED') DEFAULT 'BOOKED',
  CONSTRAINT `fk_bs_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_bs_show` FOREIGN KEY (`show_id`) REFERENCES `shows` (`show_id`),
  CONSTRAINT `fk_bs_seat` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Payments Table
CREATE TABLE `payments` (
  `payment_id` INT AUTO_INCREMENT PRIMARY KEY,
  `booking_id` INT NOT NULL,
  `transaction_id` VARCHAR(100) NOT NULL UNIQUE,
  `payment_method` ENUM('UPI', 'CREDIT_CARD', 'DEBIT_CARD', 'NET_BANKING', 'CASH_AT_COUNTER') NOT NULL,
  `amount` DECIMAL(10, 2) NOT NULL,
  `payment_status` ENUM('SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'SUCCESS',
  `payment_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_payments_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
