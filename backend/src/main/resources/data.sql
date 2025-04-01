INSERT INTO menu_item (name, price, caloric_info, popularity, availability, image_url) VALUES
('Tacos al Pastor (Shepherd-style Tacos)', 9.99, 520, 5, true, 'https://t4.ftcdn.net/jpg/10/95/04/59/240_F_1095045915_YGK1JNYRJDlSfU6hiJR6QtaEZk5ujbXf.jpg'),
('Chicken Enchiladas', 11.99, 650, 4, true, 'https://t3.ftcdn.net/jpg/12/23/76/42/240_F_1223764260_QIPstIgDWrFdvyXmWOQdF9OniXX7v8o9.jpg'),
('Beef Burrito', 10.99, 800, 5, true, 'https://t3.ftcdn.net/jpg/13/20/24/46/240_F_1320244608_hMKDhzvMSLNu8TqrkfSQiOWnQHePJkXi.jpg'),
('Quesadillas (Chicken Quesadillas)', 8.99, 600, 4, true, 'https://t4.ftcdn.net/jpg/08/43/65/83/240_F_843658363_2B1JFdQpA8lFt8r0DWqslEchq6A2yAoa.jpg'),
('Guacamole & Chips', 6.50, 300, 4, true, 'https://t3.ftcdn.net/jpg/11/71/74/66/240_F_1171746689_xXbW3qebOW6zhkZODyW18P47Km7UQDTr.jpg'),
('Chiles Rellenos (Stuffed Peppers)', 12.49, 700, 4, true, 'https://t3.ftcdn.net/jpg/11/40/15/66/240_F_1140156669_WmuitttcxNU0ZCfexarqcIQrjZsNNxqv.jpg'),
('Tamales de Pollo (Chicken Tamales)', 9.50, 500, 3, true, 'https://t4.ftcdn.net/jpg/12/89/29/07/240_F_1289290794_RkWUxxGoXFdCPI2DwRWkvfWbj4FpoQOJ.jpg'),
('Pozole Rojo (Red Hominy Stew)', 10.25, 650, 5, true, 'https://t4.ftcdn.net/jpg/13/54/84/85/240_F_1354848502_kRdfm6Df3vSq2gY1LNWQd46m7LMgWOfs.jpg'),
('Churros with Chocolate', 5.99, 450, 5, true, 'https://t4.ftcdn.net/jpg/10/25/86/27/240_F_1025862702_jLJHni07UYwSlLyJMPJEv4wRzIkEjsfg.jpg'),
('Tres Leches Cake (Three Milks Cake)', 6.49, 480, 4, true, 'https://t4.ftcdn.net/jpg/10/69/46/17/240_F_1069461759_z0vNoHOkHcnRrpOkYC3Y3SCi5Z06toBB.jpg'),
('Horchata (Sweet Rice Milk Drink)', 3.99, 180, 4, true, 'https://t3.ftcdn.net/jpg/12/60/05/94/240_F_1260059476_MnlGuCnzawGX7HihbdbQF6VN6SGvq214.jpg'),
('Agua de Jamaica (Hibiscus Tea)', 3.49, 120, 4, true, 'https://t4.ftcdn.net/jpg/13/32/96/53/240_F_1332965399_CBi06CJtDEfcTA0kExd21QfAzH4IgqUO.jpg'),
('Mexican Salad', 7.99, 350, 4, true, 'https://t4.ftcdn.net/jpg/09/17/52/67/240_F_917526766_BG4nheYuGWwiLUkBhoh0Y3ZHbwJlMKkf.jpg');


-- Dietary Restrictions
INSERT INTO menu_item_dietary_restrictions (menu_item_item_id, dietary_restrictions)
VALUES 
(5, 'Vegetarian'),
(5, 'Gluten-Free'),
(6, 'Vegetarian'),
(8, 'Gluten-Free'),
(8, 'Vegetarian'),
(12, 'Gluten-Free');

-- Allergen Information
INSERT INTO menu_item_allergen_information (menu_item_item_id, allergen_information)
VALUES 
(1, 'Gluten'),
(1, 'Dairy'),
(2, 'Gluten'),
(2, 'Dairy'),
(3, 'Gluten'),
(3, 'Dairy'),
(3, 'Eggs'),
(4, 'Gluten'),
(4, 'Dairy'),
(5, 'Gluten'),
(6, 'Dairy'),
(6, 'Eggs'),
(6, 'Gluten'),
(7, 'Dairy'),
(8, 'None'),
(9, 'Gluten'),
(9, 'Dairy'),
(9, 'Eggs'),
(10, 'Gluten'),
(10, 'Dairy'),
(10, 'Eggs'),
(11, 'Dairy'),
(11, 'Nuts'),
(13, 'Dairy'),
(13, 'Eggs'),
(13, 'Gluten');


INSERT INTO waiters (waiter_name, email, status, is_busy) 
VALUES ('Lana Shaheen', 'Lana@gmail.com', 'Available', false); 

INSERT INTO waiter_tables (waiter_id, table_number) VALUES (1, 5);
INSERT INTO waiter_tables (waiter_id, table_number) VALUES (2, 6);
INSERT INTO waiter_tables (waiter_id, table_number) VALUES (3, 7);

-- Initialize users with PINs
INSERT INTO users (pin, role) VALUES
('12345001', 'WAITER'),
('98765001', 'KITCHEN_STAFF'),
('12341234', 'ADMIN');
