DROP TABLE IF EXISTS customer, "order", waiters, kitchen_staff, payment, menu_item, menu CASCADE;

-- 1) MENU TABLE
CREATE TABLE menu (
  menu_id SERIAL NOT NULL,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (menu_id)
);

-- 2) MENU_ITEM TABLE (REFERENCES MENU)
CREATE TABLE menu_item (
  item_id SERIAL NOT NULL,
  menu_id INT,
  name VARCHAR(50) NOT NULL,
  price NUMERIC(10, 2) NOT NULL,
  caloric_info INT,
  dietary_restrictions VARCHAR(50),
  allergy_information VARCHAR(50),
  popularity INT CHECK (popularity >= 0 AND popularity <= 5) NOT NULL,
  availability BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (item_id),
  FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

-- 3) CUSTOMER
CREATE TABLE customer (
  customer_id SERIAL NOT NULL,
  customer_name VARCHAR(50) NOT NULL,
  email VARCHAR(100),
  phone VARCHAR(15),
  dietary_restrictions VARCHAR(50),
  allergy_information VARCHAR(50),
  PRIMARY KEY (customer_id)
);

-- 4) WAITERS
CREATE TABLE waiters (
  waiter_id SERIAL NOT NULL,
  waiter_name VARCHAR(50) NOT NULL,
  email VARCHAR(100),
  status VARCHAR(50) DEFAULT 'Available',
  is_busy BOOLEAN DEFAULT FALSE,
  -- to match the @ManyToOne to Menu
  menu_id INT,
  PRIMARY KEY (waiter_id),
  FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

-- 5) KITCHEN_STAFF
CREATE TABLE kitchen_staff (
  staff_id SERIAL NOT NULL,
  staff_name VARCHAR(50) NOT NULL,
  role VARCHAR(50),
  PRIMARY KEY (staff_id)
);

-- 6) ORDER
CREATE TABLE "order" (
  order_id SERIAL NOT NULL,
  customer_id INT,
  waiter_id INT,
  order_date TIMESTAMP,
  total_price NUMERIC(10, 2),
  status VARCHAR(50),
  PRIMARY KEY (order_id),
  FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
  FOREIGN KEY (waiter_id) REFERENCES waiters(waiter_id)
);

-- 7) PAYMENT
CREATE TABLE payment (
  payment_id SERIAL NOT NULL,
  order_id INT,
  amount NUMERIC(10, 2) NOT NULL,
  payment_date TIMESTAMP,
  payment_status VARCHAR(50),
  PRIMARY KEY (payment_id),
  FOREIGN KEY (order_id) REFERENCES "order"(order_id)
);
