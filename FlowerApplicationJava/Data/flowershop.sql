-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 26, 2026 at 10:05 AM
-- Server version: 10.4.17-MariaDB
-- PHP Version: 8.0.0


-- 0. KHỞI TẠO DATABASE
CREATE DATABASE IF NOT EXISTS `flowershop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `flowershop`;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `flowershop`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
                              `category_id` int(11) NOT NULL,
                              `category_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
                             `customer_id` int(11) NOT NULL,
                             `user_id` int(11) DEFAULT NULL,
                             `customer_name` varchar(100) NOT NULL,
                             `phone` varchar(15) DEFAULT NULL,
                             `email` varchar(100) DEFAULT NULL,
                             `points` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`customer_id`, `user_id`, `customer_name`, `phone`, `email`, `points`) VALUES
                                                                                                    (1, 3, 'Nguyễn Văn A', '0901111111', 'a@gmail.com', 10),
                                                                                                    (2, NULL, 'Trần Thị B', '0902222222', NULL, 20),
                                                                                                    (3, 5, 'ha khoa', '0919098606', 'hakhoa176@gmail.com', 677),
                                                                                                    (4, 6, 'ha khoa', '0919098607', '@gmail.com', 240),
                                                                                                    (5, 7, 'a a', '0823123232', 'hakhoa1708@gmail.com', 97);

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
                             `employee_id` int(11) NOT NULL,
                             `user_id` int(11) DEFAULT NULL,
                             `name` varchar(100) NOT NULL,
                             `phone` varchar(15) DEFAULT NULL,
                             `role` varchar(50) DEFAULT NULL,
                             `salary` decimal(10,2) DEFAULT 0.00,
                             `status` enum('Active','Inactive') DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`employee_id`, `user_id`, `name`, `phone`, `role`, `salary`, `status`) VALUES
    (1, 2, 'Nguyễn Nhân Viên 1', '0911111111', 'Sales', '8000000.00', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
                          `order_id` int(11) NOT NULL,
                          `customer_id` int(11) DEFAULT NULL,
                          `employee_id` int(11) DEFAULT NULL,
                          `order_date` datetime DEFAULT current_timestamp(),
                          `total` decimal(10,2) DEFAULT NULL,
                          `points_used` int(11) DEFAULT NULL,
                          `points_earned` int(11) DEFAULT NULL,
                          `status` enum('CART','PLACED','PENDING','PAID','DONE','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `recipient_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `recipient_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `recipient_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `shipping_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `customer_id`, `employee_id`, `order_date`, `total`, `points_used`, `points_earned`, `status`, `payment_method`, `recipient_name`, `recipient_email`, `recipient_phone`, `shipping_address`) VALUES
                                                                                                                                                                                                                                   (1, 1, NULL, '2026-03-10 00:00:00', '150000.00', NULL, NULL, 'DONE', NULL, NULL, NULL, NULL, NULL),
                                                                                                                                                                                                                                   (2, 2, NULL, '2026-03-11 00:00:00', '200000.00', NULL, NULL, 'DONE', NULL, NULL, NULL, NULL, NULL),
                                                                                                                                                                                                                                   (3, 3, NULL, '2026-03-24 17:10:28', '70005.00', 0, 350, 'PAID', 'VNPAY', 'ha khoa', '32423@gmail.com', '3242312342', '23432423432'),
                                                                                                                                                                                                                                   (4, 3, NULL, '2026-03-25 01:30:04', '70005.00', 0, 350, 'PAID', 'VNPAY', 'ha khoa', 'hakhoa176@gmail.com', '0919098606', '1231221'),
                                                                                                                                                                                                                                   (5, 3, NULL, '2026-03-25 01:38:48', '0.00', 30, 0, 'PAID', 'VNPAY', 'ha khoa', 'hakhoa176@gmail.com', '0919098606', '12312'),
                                                                                                                                                                                                                                   (6, 3, NULL, '2026-03-25 02:14:56', '70005.00', 0, 7, 'PLACED', 'Cash on Delivery', 'ha khoa', 'hakhoa176@gmail.com', '0919098606', '123'),
                                                                                                                                                                                                                                   (7, 4, NULL, '2026-03-25 02:15:17', '170005.00', 0, 17, 'PLACED', 'Credit Card', 'ha khoa', 'hakhoa@gmail.com', '0853468478', '12312asd'),
                                                                                                                                                                                                                                   (8, 4, NULL, '2026-03-25 02:27:50', '50005.00', 0, 5, 'PLACED', 'Cash on Delivery', 'ha khoa', 'hakhoa@gmail.com', '0919098609', 'ádasdasdasd'),
                                                                                                                                                                                                                                   (9, 3, NULL, '2026-03-25 02:26:37', '70005.00', 0, 0, 'CART', NULL, NULL, NULL, NULL, NULL),
                                                                                                                                                                                                                                   (10, 4, NULL, '2026-03-25 02:28:19', '70005.00', 0, 7, 'PLACED', 'Cash on Delivery', 'ha khoa', 'hakhoa@gmail.com', '0919098607', 'tytertrwertwe'),
                                                                                                                                                                                                                                   (11, 4, NULL, '2026-03-25 02:32:32', '21005.00', 29, 2, 'PAID', 'VNPAY', 'ha khoa', '@gmail.com', '0919098607', ';o/ilkhjghfgđ'),
                                                                                                                                                                                                                                   (12, 4, NULL, '2026-03-26 01:11:28', '2380005.00', 0, 238, 'PLACED', 'Thanh toán khi nhận hàng', 'ha khoa', '@gmail.com', '0919098607', '12312'),
                                                                                                                                                                                                                                   (13, 5, NULL, '2026-03-26 02:34:23', '900005.00', 0, 90, 'PLACED', 'Thanh toán khi nhận hàng', 'a a', 'hakhoa1708@gmail.com', '0823123232', 'ádascawsdasd'),
                                                                                                                                                                                                                                   (14, 5, NULL, '2026-03-26 14:14:26', '40005.00', 0, 4, 'PLACED', 'VNPay', 'a a', 'hakhoa1708@gmail.com', '0823123232', 'ưesesa'),
                                                                                                                                                                                                                                   (15, 5, NULL, '2026-03-26 14:18:10', '32005.00', 0, 3, 'PAID', 'VNPay', 'a a', 'hakhoa1708@gmail.com', '0823123232', 'dfsdbfgdfgdsa'),
                                                                                                                                                                                                                                   (16, 5, NULL, '2026-03-26 15:13:25', '75005.00', 0, 0, 'CART', NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
                                 `order_detail_id` int(11) NOT NULL,
                                 `order_id` int(11) DEFAULT NULL,
                                 `product_id` int(11) DEFAULT NULL,
                                 `quantity` int(11) DEFAULT NULL,
                                 `price` decimal(10,2) DEFAULT NULL,
                                 `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`order_detail_id`, `order_id`, `product_id`, `quantity`, `price`, `note`) VALUES
                                                                                                           (1, 1, 1, 2, '50000.00', NULL),
                                                                                                           (2, 2, 2, 2, '70000.00', NULL),
                                                                                                           (3, 3, 2, 1, '70000.00', NULL),
                                                                                                           (4, 4, 2, 1, '70000.00', NULL),
                                                                                                           (5, 5, 2, 1, '70000.00', NULL),
                                                                                                           (6, 6, 2, 1, '70000.00', NULL),
                                                                                                           (7, 7, 2, 1, '70000.00', NULL),
                                                                                                           (8, 7, 1, 2, '50000.00', NULL),
                                                                                                           (9, 8, 1, 1, '50000.00', NULL),
                                                                                                           (10, 9, 2, 1, '70000.00', NULL),
                                                                                                           (11, 10, 2, 1, '70000.00', NULL),
                                                                                                           (12, 11, 1, 1, '50000.00', NULL),
                                                                                                           (13, 12, 2, 34, '70000.00', NULL),
                                                                                                           (15, 13, 15, 6, '150000.00', NULL),
                                                                                                           (16, 14, 14, 1, '40000.00', NULL),
                                                                                                           (17, 15, 6, 1, '32000.00', NULL),
                                                                                                           (19, 16, 16, 1, '75000.00', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
                            `product_id` int(11) NOT NULL,
                            `category_id` int(11) NOT NULL,
                            `product_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `price` decimal(10,2) NOT NULL,
                            `quantity` int(11) DEFAULT 0,
                            `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

-- =========================================================
-- SỬA CATEGORY THÀNH 4 NHÓM CHUẨN
-- 1 = Hoa Tươi
-- 2 = Hoa Sinh Nhật
-- 3 = Hoa Cưới
-- 4 = Hoa Trang Trọng
-- =========================================================

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO categories (category_id, category_name) VALUES
                                                        (1, 'Hoa Tươi'),
                                                        (2, 'Hoa Sinh Nhật'),
                                                        (3, 'Hoa Cưới'),
                                                        (4, 'Hoa Trang Trọng');

ALTER TABLE categories AUTO_INCREMENT = 5;

INSERT INTO products (product_id, category_id, product_name, price, quantity, image) VALUES
-- =========================
-- CATEGORY 1: HOA TƯƠI
-- =========================
(1, 1, 'Hoa Hồng Đỏ', '50000.00', 94, 'flower-rose.jpg'),
(2, 1, 'Hoa Ly Trắng', '70000.00', 0, 'flower-rose.jpg'),
(3, 1, 'Hoa Hồng Trắng', '45000.00', 20, 'flower-rose.jpg'),
(4, 1, 'Hoa Hồng Vàng', '55000.00', 15, 'flower-rose.jpg'),
(5, 1, 'Hoa Cúc Trắng', '30000.00', 40, 'flower-rose.jpg'),
(6, 1, 'Hoa Cúc Vàng', '32000.00', 24, 'flower-rose.jpg'),
(7, 1, 'Hoa Tulip Đỏ', '80000.00', 10, 'flower-rose.jpg'),
(8, 1, 'Hoa Tulip Hồng', '85000.00', 8, 'flower-rose.jpg'),
(9, 1, 'Hoa Baby Trắng', '60000.00', 50, 'flower-rose.jpg'),
(10, 1, 'Hoa Baby Hồng', '65000.00', 35, 'flower-rose.jpg'),
(11, 1, 'Hoa Lan Trắng', '120000.00', 5, 'flower-rose.jpg'),
(12, 1, 'Hoa Lan Tím', '130000.00', 3, 'flower-rose.jpg'),

-- =========================
-- CATEGORY 2: HOA SINH NHẬT
-- =========================
(13, 2, 'Bó Hoa Sinh Nhật Hướng Dương', '70000.00', 18, 'flower-rose.jpg'),
(14, 2, 'Bó Hoa Sinh Nhật Đồng Tiền', '40000.00', 21, 'flower-rose.jpg'),
(15, 2, 'Bó Hoa Sinh Nhật Mẫu Đơn', '150000.00', 0, 'flower-rose.jpg'),
(16, 2, 'Giỏ Hoa Sinh Nhật Ly Vàng', '75000.00', 12, 'flower-rose.jpg'),
(17, 2, 'Giỏ Hoa Sinh Nhật Ly Hồng', '78000.00', 9, 'flower-rose.jpg'),
(18, 2, 'Bó Hoa Sinh Nhật Lavender', '90000.00', 7, 'flower-rose.jpg'),
(19, 2, 'Bó Hoa Sinh Nhật Cẩm Tú Cầu', '110000.00', 4, 'flower-rose.jpg'),
(20, 2, 'Bó Hoa Sinh Nhật Thạch Thảo', '35000.00', 30, 'flower-rose.jpg'),
(21, 2, 'Giỏ Hoa Sinh Nhật Sen Hồng', '95000.00', 11, 'flower-rose.jpg'),
(22, 2, 'Giỏ Hoa Sinh Nhật Sen Trắng', '90000.00', 13, 'flower-rose.jpg'),

-- =========================
-- CATEGORY 3: HOA CƯỚI
-- =========================
(23, 3, 'Hoa Cưới Trắng Classic', '120000.00', 10, 'flower-rose.jpg'),
(24, 3, 'Hoa Cưới Hồng Nhẹ Nhàng', '130000.00', 8, 'flower-rose.jpg'),
(25, 3, 'Hoa Cưới Tulip Mix', '110000.00', 15, 'flower-rose.jpg'),
(26, 3, 'Hoa Cưới Sang Trọng', '150000.00', 5, 'flower-rose.jpg'),
(27, 3, 'Hoa Cưới Baby Trắng', '90000.00', 20, 'flower-rose.jpg'),
(28, 3, 'Hoa Cưới Lavender', '140000.00', 7, 'flower-rose.jpg'),
(29, 3, 'Hoa Cưới Đỏ Lãng Mạn', '135000.00', 6, 'flower-rose.jpg'),
(30, 3, 'Hoa Cưới Hoa Lan', '160000.00', 4, 'flower-rose.jpg'),
(31, 3, 'Hoa Cưới Vintage', '125000.00', 9, 'flower-rose.jpg'),
(32, 3, 'Hoa Cưới Mini', '80000.00', 12, 'flower-rose.jpg'),

-- =========================
-- CATEGORY 4: HOA TRANG TRỌNG
-- =========================
(33, 4, 'Kệ Hoa Trang Trọng Đỏ', '100000.00', 15, 'flower-rose.jpg'),
(34, 4, 'Kệ Hoa Trang Trọng Hồng', '95000.00', 20, 'flower-rose.jpg'),
(35, 4, 'Kệ Hoa Trang Trọng Tulip', '105000.00', 10, 'flower-rose.jpg'),
(36, 4, 'Kệ Hoa Trang Trọng Sang Trọng', '150000.00', 5, 'flower-rose.jpg'),
(37, 4, 'Kệ Hoa Trang Trọng Hoa Lan', '130000.00', 8, 'flower-rose.jpg'),
(38, 4, 'Kệ Hoa Trang Trọng Baby', '85000.00', 25, 'flower-rose.jpg'),
(39, 4, 'Kệ Hoa Trang Trọng Mix', '90000.00', 18, 'flower-rose.jpg'),
(40, 4, 'Kệ Hoa Trang Trọng Lavender', '115000.00', 12, 'flower-rose.jpg'),
(41, 4, 'Kệ Hoa Trang Trọng Đặc Biệt', '170000.00', 3, 'flower-rose.jpg'),
(42, 4, 'Kệ Hoa Trang Trọng Nhẹ Nhàng', '88000.00', 14, 'flower-rose.jpg');

ALTER TABLE products AUTO_INCREMENT = 43;

SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------

--
-- Table structure for table `stock_imports`
--

CREATE TABLE `stock_imports` (
                                 `import_id` int(11) NOT NULL,
                                 `product_id` int(11) DEFAULT NULL,
                                 `supplier_id` int(11) DEFAULT NULL,
                                 `quantity` int(11) DEFAULT NULL,
                                 `import_price` decimal(10,2) DEFAULT NULL,
                                 `sell_price_at_import` decimal(10,2) DEFAULT NULL,
                                 `import_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stock_imports`
--

INSERT INTO `stock_imports` (`import_id`, `product_id`, `supplier_id`, `quantity`, `import_price`, `sell_price_at_import`, `import_date`) VALUES
                                                                                                                                              (1, 1, 1, 50, '30000.00', '50000.00', '2026-03-01'),
                                                                                                                                              (2, 2, 1, 40, '50000.00', '70000.00', '2026-03-02');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
                             `supplier_id` int(11) NOT NULL,
                             `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `status` enum('Active','Inactive') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `created_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`supplier_id`, `name`, `address`, `email`, `phone`, `status`, `created_date`) VALUES
    (1, 'Hoa Tươi Đà Lạt', 'Đà Lạt', 'dalat@flowers.com', '0900000001', NULL, '2026-01-01');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `user_id` int(11) NOT NULL,
                         `username` varchar(50) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `role` enum('admin','staff','customer') DEFAULT 'customer'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`) VALUES
                                                                    (1, 'admin_quyen', '123', 'admin'),
                                                                    (2, 'staff_01', '123', 'staff'),
                                                                    (3, 'kh_nguyenvana', '123', 'customer'),
                                                                    (4, 'khoa', '123', 'customer'),
                                                                    (5, 'hakhoa', '176', 'customer'),
                                                                    (6, 'hakhoa176', '176', 'customer'),
                                                                    (7, 'a', 'a', 'customer');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
    ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `category_name` (`category_name`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
    ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
    ADD PRIMARY KEY (`employee_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
    ADD PRIMARY KEY (`order_id`),
  ADD KEY `fk_order_customer` (`customer_id`),
  ADD KEY `fk_order_employee` (`employee_id`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
    ADD PRIMARY KEY (`order_detail_id`),
  ADD KEY `fk_orderdetail_order` (`order_id`),
  ADD KEY `fk_orderdetail_product` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
    ADD PRIMARY KEY (`product_id`),
  ADD KEY `fk_product_category` (`category_id`);

--
-- Indexes for table `stock_imports`
--
ALTER TABLE `stock_imports`
    ADD PRIMARY KEY (`import_id`),
  ADD KEY `fk_stock_import_product` (`product_id`),
  ADD KEY `fk_stock_import_supplier` (`supplier_id`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
    ADD PRIMARY KEY (`supplier_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
    MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
    MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
    MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
    MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
    MODIFY `order_detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
    MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT for table `stock_imports`
--
ALTER TABLE `stock_imports`
    MODIFY `import_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
    MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `customers`
--
ALTER TABLE `customers`
    ADD CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL;

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
    ADD CONSTRAINT `fk_employee_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
    ADD CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  ADD CONSTRAINT `fk_order_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`);

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
    ADD CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_orderdetail_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
    ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

--
-- Constraints for table `stock_imports`
--
ALTER TABLE `stock_imports`
    ADD CONSTRAINT `fk_stock_import_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_stock_import_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

ALTER TABLE users ADD COLUMN email VARCHAR(100);
DELETE FROM users WHERE username = 'admin';
INSERT INTO users (username, password, role)
VALUES ('admin', '123', 'admin');

select * from Categories;
select * from Products;
