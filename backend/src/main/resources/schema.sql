DROP TABLE IF EXISTS customer, "order", waiter, kitchen_staff, payment, menu, menu_item CASCADE;

CREATE TABLE menu (
                      menu_id SERIAL NOT NULL,
                      name VARCHAR(50) NOT NULL,
                      PRIMARY KEY (menu_id)
);

CREATE TABLE menu_item (
                           item_id SERIAL NOT NULL,
                           menu_id int,
                           name VARCHAR(50) NOT NULL,
                           price NUMERIC(10, 2) NOT NULL,
                           caloric_info INT,
                           dietary_restrictions VARCHAR(50),
                           allergy_information VARCHAR(50),
                           popularity int CHECK (popularity >= 0.0 AND popularity <= 5.0) NOT NULL,
                           availability BOOLEAN DEFAULT true,
                           image_url VARCHAR(255) NULL, --allows the image to be NULL since I will be URL loading
                           PRIMARY KEY (item_id),
                           FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

CREATE TABLE customer (
                          customer_id SERIAL NOT NULL,
                          customer_name VARCHAR(50) NOT NULL,
                          email VARCHAR(100),
                          phone VARCHAR(15),
                          dietary_restrictions VARCHAR(50),
                          allergy_information VARCHAR(50),
                          PRIMARY KEY (customer_id)
);


CREATE TABLE waiter (
                        waiter_id SERIAL NOT NULL,
                        waiter_name VARCHAR(50) NOT NULL,
                        email VARCHAR(100),
                        PRIMARY KEY (waiter_id)
);

CREATE TABLE kitchen_staff (
                               staff_id SERIAL NOT NULL,
                               staff_name VARCHAR(50) NOT NULL,
                               role VARCHAR(50),
                               PRIMARY KEY (staff_id)
);

CREATE TABLE "order" (
                         order_id SERIAL NOT NULL,
                         customer_id int,
                         waiter_id int,
                         order_date TIMESTAMP,
                         total_price NUMERIC(10, 2),
                         status VARCHAR(50),
                         PRIMARY KEY (order_id),
                         FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
                         FOREIGN KEY (waiter_id) REFERENCES waiter(waiter_id)
);

CREATE TABLE payment (
                         payment_id SERIAL NOT NULL,
                         order_id INT,
                         amount NUMERIC(10, 2) NOT NULL,
                         payment_date TIMESTAMP,
                         payment_status VARCHAR(50),
                         PRIMARY KEY (payment_id),
                         FOREIGN KEY (order_id) REFERENCES "order"(order_id)
);