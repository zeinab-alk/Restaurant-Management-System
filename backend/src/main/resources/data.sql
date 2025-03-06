INSERT INTO menu_item (name, price, caloric_info, popularity, availability, image_url) 
VALUES 
('Margherita Pizza', 12.99, 800, 5, true, 'https://uk.ooni.com/cdn/shop/articles/20220211142645-margherita-9920_e41233d5-dcec-461c-b07e-03245f031dfe.jpg'),
('Chicken Caesar Salad', 9.99, 450, 4, true, NULL),
('Beef Burger', 14.99, 950, 5, true, NULL),
('Vegetable Pasta', 11.99, 600, 3, true, NULL),
('Greek Salad', 8.99, 380, 4, true, NULL),
('Seafood Paella', 16.99, 720, 5, true, NULL),
('Chocolate Cake', 6.99, 550, 5, true, NULL),
('Fresh Fruit Platter', 7.99, 220, 3, true, NULL);

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
(3, 'Gluten'); 
INSERT INTO waiters (waiter_name, email, status, is_busy) 
VALUES ('Lana Shaheen', 'Lana@gmail.com', 'Available', false);