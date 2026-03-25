-- ==========================================
-- 0. KHỞI TẠO DATABASE
CREATE DATABASE IF NOT EXISTS `flowershop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `flowershop`;
-- Thiết lập môi trường
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
SET FOREIGN_KEY_CHECKS = 0;
-- Xóa các bảng cũ nếu tồn tại để reset (Đã bổ sung xóa thêm bảng 'stock' cũ)
DROP TABLE IF EXISTS order_details, orders, stock, stock_imports, products, categories, suppliers, customers, employees, users;

-- ==========================================
-- 1. BẢNG PHÂN QUYỀN & TÀI KHOẢN
-- ==========================================
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `role` ENUM('admin', 'staff', 'customer') DEFAULT 'customer',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 2. BẢNG NHÂN VIÊN
-- ==========================================
CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNIQUE DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  `salary` decimal(10,2) DEFAULT 0.00,
  `status` ENUM('Active', 'Inactive') DEFAULT 'Active',
  PRIMARY KEY (`employee_id`),
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 3. BẢNG KHÁCH HÀNG
-- ==========================================
CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNIQUE DEFAULT NULL,
  `customer_name` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `points` int(11) DEFAULT 0,
  PRIMARY KEY (`customer_id`),
  CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 4. BẢNG DANH MỤC & SẢN PHẨM
-- ==========================================
CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL UNIQUE,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL,
  `product_name` varchar(150) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int(11) DEFAULT 0,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 5. BẢNG NHÀ CUNG CẤP & NHẬP KHO
-- ==========================================
CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `status` ENUM('Active', 'Inactive') DEFAULT 'Active',
  `created_date` date DEFAULT NULL,
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `stock_imports` (
  `import_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL CHECK (`quantity` > 0),
  `import_price` decimal(10,2) NOT NULL,
  `sell_price_at_import` decimal(10,2) DEFAULT NULL,
  `import_date` date NOT NULL,
  PRIMARY KEY (`import_id`),
  -- Đã đổi tên các Constraint này để không bị trùng
  CONSTRAINT `fk_stock_import_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_stock_import_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 6. BẢNG ĐƠN HÀNG & CHI TIẾT
-- ==========================================
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) NOT NULL,
  `employee_id` int(11) DEFAULT NULL,
  `order_date` datetime DEFAULT current_timestamp(),
  `total` decimal(10,2) DEFAULT 0.00,
  `points_used` int(11) DEFAULT 0,
  `points_earned` int(11) DEFAULT 0,
  `status` ENUM('CART', 'PLACED', 'PENDING', 'PAID', 'DONE', 'CANCELLED') DEFAULT 'CART',
  `payment_method` varchar(50) DEFAULT NULL,
  `recipient_name` varchar(100) DEFAULT NULL,
  `recipient_email` varchar(100) DEFAULT NULL,
  `recipient_phone` varchar(20) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_order_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order_details` (
  `order_detail_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL CHECK (`quantity` > 0),
  `price` decimal(10,2) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_detail_id`),
  CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orderdetail_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 7. INSERT DỮ LIỆU MẪU
-- ==========================================

INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `role`) VALUES
(1, 'admin_quyen', 'hashed_pw', 'admin@flowershop.com', 'admin'),
(2, 'staff_01', '1234', 'staff1@flowershop.com', 'staff'),
(3, 'kh_nguyenvana', '123', 'a@gmail.com', 'customer');

INSERT INTO `employees` (`employee_id`, `user_id`, `name`, `phone`, `role`, `salary`) VALUES
(1, 2, 'Nguyễn Nhân Viên 1', '0911111111', 'Sales', 8000000);

INSERT INTO `customers` (`customer_id`, `user_id`, `customer_name`, `phone`, `points`) VALUES
(1, 3, 'Nguyễn Văn A', '0901111111', 10),
(2, NULL, 'Trần Thị B', '0902222222', 20);

INSERT INTO `categories` (`category_id`, `category_name`) VALUES
(1, 'Hoa Tươi'), (2, 'Hoa Sinh Nhật'), (3, 'Hoa Khai Trương');

INSERT INTO `suppliers` (`supplier_id`, `name`, `address`, `email`, `phone`, `created_date`) VALUES
(1, 'Hoa Tươi Đà Lạt', 'Đà Lạt', 'dalat@flowers.com', '0900000001', '2026-01-01');

INSERT INTO `products` (`product_id`, `category_id`, `product_name`, `price`, `quantity`, `image`) VALUES
(1, 1, 'Hoa Hồng Đỏ', 50000, 98, 'flower-rose.jpg'),
(2, 1, 'Hoa Ly Trắng', 70000, 40, 'flower-rose.jpg');

INSERT INTO `stock_imports` (`product_id`, `supplier_id`, `quantity`, `import_price`, `sell_price_at_import`, `import_date`) VALUES
(1, 1, 50, 30000, 50000, '2026-03-01'),
(2, 1, 40, 50000, 70000, '2026-03-02');

INSERT INTO `orders` (`order_id`, `customer_id`, `order_date`, `total`, `status`) VALUES
(1, 1, '2026-03-10', 150000, 'DONE'),
(2, 2, '2026-03-11', 200000, 'DONE');

INSERT INTO `order_details` (`order_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 2, 50000),
(2, 2, 2, 70000);