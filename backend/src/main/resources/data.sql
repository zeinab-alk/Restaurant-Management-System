INSERT INTO menu_item (name, price, caloric_info, popularity, availability, image_url) 
VALUES 
('Margherita Pizza', 12.99, 800, 5, true, 'https://uk.ooni.com/cdn/shop/articles/20220211142645-margherita-9920_e41233d5-dcec-461c-b07e-03245f031dfe.jpg'),
('Chicken Caesar Salad', 9.99, 450, 4, true, 'https://s23209.pcdn.co/wp-content/uploads/2023/01/220905_DD_Chx-Caesar-Salad_051-500x500.jpg'),
('Beef Burger', 14.99, 950, 5, true, 'https://staticcookist.akamaized.net/wp-content/uploads/sites/22/2021/09/beef-burger.jpg'),
('Vegetable Pasta', 11.99, 600, 3, true, 'https://www.inspiredtaste.net/wp-content/uploads/2019/02/Vegetable-Spaghetti-Recipe-2-1200.jpg'),
('Greek Salad', 8.99, 380, 4, true, 'https://www.onceuponachef.com/images/2023/06/greek-salad-1-1200x1477.jpg'),
('Seafood Paella', 16.99, 720, 5, true, 'https://simply-delicious-food.com/wp-content/uploads/2020/10/0E2A1651-3.jpg'),
('Chocolate Cake', 6.99, 550, 5, true, 'https://www.hersheyland.com/content/dam/hersheyland/en-us/recipes/recipe-images/2-hersheys-perfectly-chocolate-chocolate-cake-recipe-hero.jpg'),
('Fresh Fruit Platter', 7.99, 220, 3, true, 'https://www.hermolis.com/cdn/shop/files/DSC07455-Edit.jpg?v=1682432666');

-- For dietary restrictions and allergens, we need separate inserts due to @ElementCollection
INSERT INTO menu_item_dietary_restrictions (menu_item_item_id, dietary_restrictions) 
VALUES 
(2, 'Gluten-Free'),
(4, 'Vegetarian'),
(5, 'Vegetarian'),
(5, 'Gluten-Free'),
(7, 'Vegetarian'),
(8, 'Vegan'),
(8, 'Gluten-Free');

INSERT INTO menu_item_allergen_information (menu_item_item_id, allergen_information)
VALUES 
(1, 'Gluten'),
(1, 'Dairy'),
(2, 'Dairy'),
(3, 'Gluten'),
(3, 'Eggs'),
(6, 'Fish'),
(6, 'Shellfish'),
(7, 'Dairy'),
(7, 'Eggs');

INSERT INTO waiters (waiter_name, email, status, is_busy) 
VALUES ('Lana Shaheen', 'Lana@gmail.com', 'Available', false); 

INSERT INTO waiter_tables (waiter_id, table_number) VALUES (1, 5);
INSERT INTO waiter_tables (waiter_id, table_number) VALUES (2, 6);
INSERT INTO waiter_tables (waiter_id, table_number) VALUES (3, 7);
