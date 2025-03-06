INSERT INTO menu_item (name, price, caloric_info, popularity, availability) 
VALUES 
('Margherita Pizza', 12.99, 800, 5, true),
('Chicken Caesar Salad', 9.99, 450, 4, true),
('Beef Burger', 14.99, 950, 5, true),
('Vegetable Pasta', 11.99, 600, 3, true),
('Greek Salad', 8.99, 380, 4, true),
('Seafood Paella', 16.99, 720, 5, true),
('Chocolate Cake', 6.99, 550, 5, true),
('Fresh Fruit Platter', 7.99, 220, 3, true);

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