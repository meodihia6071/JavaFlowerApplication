
CREATE DATABASE IF NOT EXISTS FlowerShop;
USE FlowerShop;


CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin','staff','customer') DEFAULT 'customer'
);


CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(150) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,
    image VARCHAR(255),
    category_id INT,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON DELETE SET NULL
);


CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    points INT DEFAULT 0,
    user_id INT,

    CONSTRAINT fk_customer_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);


CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0,
    points_used INT DEFAULT 0,
    points_earned INT DEFAULT 0,
    status VARCHAR(50),

    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id)
        ON DELETE SET NULL
);

CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_orderdetail_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_orderdetail_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

--Thêm--
USE FlowerShop;

INSERT INTO categories (category_name) VALUES
                                           ('Birthday Flowers'),
                                           ('Wedding Flowers'),
                                           ('Anniversary Flowers'),
                                           ('Sympathy Flowers');

SET @birthdayId = (SELECT category_id FROM categories WHERE category_name = 'Birthday Flowers' LIMIT 1);
SET @weddingId = (SELECT category_id FROM categories WHERE category_name = 'Wedding Flowers' LIMIT 1);
SET @anniversaryId = (SELECT category_id FROM categories WHERE category_name = 'Anniversary Flowers' LIMIT 1);
SET @sympathyId = (SELECT category_id FROM categories WHERE category_name = 'Sympathy Flowers' LIMIT 1);

INSERT INTO products (product_name, price, quantity, image, category_id) VALUES
-- Birthday Flowers (8)
('Rose Bouquet', 25.00, 100, 'flower-rose.jpg', @birthdayId),
('Pink Tulip Box', 28.00, 100, 'flower-rose.jpg', @birthdayId),
('Sunny Daisy Mix', 22.00, 100, 'flower-rose.jpg', @birthdayId),
('Pastel Carnation Basket', 24.00, 100, 'flower-rose.jpg', @birthdayId),
('Sweet Peony Wrap', 30.00, 100, 'flower-rose.jpg', @birthdayId),
('Garden Celebration Vase', 32.00, 100, 'flower-rose.jpg', @birthdayId),
('Baby Breath Bloom', 20.00, 100, 'flower-rose.jpg', @birthdayId),
('Colorful Birthday Mix', 27.00, 100, 'flower-rose.jpg', @birthdayId),

-- Wedding Flowers (8)
('White Rose Bridal Bouquet', 45.00, 100, 'flower-rose.jpg', @weddingId),
('Ivory Peony Bouquet', 48.00, 100, 'flower-rose.jpg', @weddingId),
('Elegant Orchid Spray', 52.00, 100, 'flower-rose.jpg', @weddingId),
('Classic Wedding Mix', 40.00, 100, 'flower-rose.jpg', @weddingId),
('Blush Bridal Roses', 46.00, 100, 'flower-rose.jpg', @weddingId),
('Pure Lily Arrangement', 44.00, 100, 'flower-rose.jpg', @weddingId),
('Wedding Tulip Bundle', 42.00, 100, 'flower-rose.jpg', @weddingId),
('Romantic White Garden', 50.00, 100, 'flower-rose.jpg', @weddingId),

-- Anniversary Flowers (8)
('Red Rose Anniversary', 35.00, 100, 'flower-rose.jpg', @anniversaryId),
('Golden Love Bouquet', 38.00, 100, 'flower-rose.jpg', @anniversaryId),
('Sweet Memory Tulips', 33.00, 100, 'flower-rose.jpg', @anniversaryId),
('Ruby Romance Basket', 36.00, 100, 'flower-rose.jpg', @anniversaryId),
('Classic Love Mix', 34.00, 100, 'flower-rose.jpg', @anniversaryId),
('Candlelight Peonies', 39.00, 100, 'flower-rose.jpg', @anniversaryId),
('Timeless Pink Roses', 37.00, 100, 'flower-rose.jpg', @anniversaryId),
('Forever Love Arrangement', 41.00, 100, 'flower-rose.jpg', @anniversaryId),

-- Sympathy Flowers (8)
('White Lily Tribute', 31.00, 100, 'flower-rose.jpg', @sympathyId),
('Peaceful White Roses', 29.00, 100, 'flower-rose.jpg', @sympathyId),
('Gentle Condolence Basket', 34.00, 100, 'flower-rose.jpg', @sympathyId),
('Serenity Orchid Spray', 36.00, 100, 'flower-rose.jpg', @sympathyId),
('Memorial Carnation Mix', 28.00, 100, 'flower-rose.jpg', @sympathyId),
('Soft Sympathy Bouquet', 30.00, 100, 'flower-rose.jpg', @sympathyId),
('Remembrance Flowers', 33.00, 100, 'flower-rose.jpg', @sympathyId),
('Graceful Farewell Vase', 35.00, 100, 'flower-rose.jpg', @sympathyId);

CREATE INDEX idx_orders_customer_status ON orders(customer_id, status);

SELECT * FROM categories;
SELECT * FROM products;

ALTER TABLE order_details
    ADD COLUMN note VARCHAR(255) NULL AFTER price;


SELECT product_name, quantity
FROM products
WHERE product_name = 'Pink Tulip Box';

SELECT product_id, product_name, quantity
FROM products
WHERE product_name = 'Pink Tulip Box';

UPDATE products
SET quantity = 5
WHERE product_id = 2;

ALTER TABLE orders
ADD COLUMN recipient_name VARCHAR(100) NULL AFTER customer_id,
ADD COLUMN recipient_email VARCHAR(100) NULL AFTER recipient_name,
ADD COLUMN recipient_phone VARCHAR(20) NULL AFTER recipient_email,
ADD COLUMN shipping_address VARCHAR(255) NULL AFTER recipient_phone,
ADD COLUMN payment_method VARCHAR(50) NULL AFTER shipping_address;