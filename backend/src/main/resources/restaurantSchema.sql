DROP TABLE IF EXISTS customer, "order", waiter, kitchen_staff, payment, menu, menu_item CASCADE;
DROP TYPE IF EXISTS name, order_status, payment_status, allergy_information, dietary_restrictions, role;

CREATE TYPE name AS
(first_name VARCHAR(50),
last_name VARCHAR(50));

CREATE TYPE order_status AS ENUM(
	'Pending', 
	'In Progress', 
	'Completed', 
	'Cancelled'
);

CREATE TYPE payment_status AS ENUM(
	'Pending', 
	'Failed', 
	'Successful'
);

CREATE TYPE allergy_information AS ENUM (
    'Celery', 
    'Gluten', 
    'Crustaceans', 
    'Eggs', 
    'Fish', 
    'Lupin', 
    'Milk', 
    'Molluscs', 
    'Mustard', 
    'Peanuts', 
    'Sesame seeds', 
    'Soya', 
    'Sulphur dioxide', 
    'Nuts'
);

CREATE TYPE dietary_restrictions AS ENUM (
    'Vegetarian', 
    'Vegan', 
    'Gluten-Free', 
    'Dairy-Free', 
    'Nut-Free', 
    'Halal', 
    'Kosher'
);

CREATE TYPE role AS ENUM(
	'Head Chef', 
	'Sous Chef', 
	'Line Cook'
);

CREATE TABLE menu (
    menu_id SERIAL int,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (menu_id)
);

CREATE TABLE menu_item (
    item_id SERIAL int, 
    menu_id int,
  	name VARCHAR(50) NOT NULL, 
  	price NUMERIC(10, 2) NOT NULL, 
  	caloricInfo VARCHAR(20), 
  	dietary_restrictions dietary_restrictions[], 
  	allergy_information allergy_information[], 
  	popularity int CHECK (popularity >= 0.0 AND popularity <= 5.0) NOT NULL,
  	image_url VARCHAR(255) NULL, --Allows the image to be null again
  	PRIMARY KEY (item_id), 
  	FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

CREATE TABLE customer (
    customer_id SERIAL int,
    customer_name name NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    dietary_restrictions dietary_restrictions[], 
  	allergy_information allergy_information[], 
    PRIMARY KEY (customer_id)
);


CREATE TABLE waiter (
    waiter_id SERIAL int,
    waiter_name name NOT NULL,
    email VARCHAR(100),
    PRIMARY KEY (waiter_id)
);

CREATE TABLE kitchen_staff (
    staff_id SERIAL int,
    staff_name name NOT NULL,
    role role,
    PRIMARY KEY (staff_id)
);

CREATE TABLE "order" (
    order_id SERIAL int,
    customer_id int,
    waiter_id int,
    order_date TIMESTAMP,
    total_price NUMERIC(10, 2),
    status order_status,
    PRIMARY KEY (order_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (waiter_id) REFERENCES waiter(waiter_id)
);

CREATE TABLE payment (
    payment_id SERIAL int,
    order_id INT,
    amount NUMERIC(10, 2) NOT NULL,
    payment_date TIMESTAMP,
    payment_status payment_status,
    PRIMARY KEY (payment_id),
    FOREIGN KEY (order_id) REFERENCES "order"(order_id)
);