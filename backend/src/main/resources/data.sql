INSERT INTO menu_item (name, price, caloric_info, popularity, availability, image_url) 
VALUES 
('Margherita Pizza', 12.99, 800, 5, true, 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Pizza_Margherita_stu_spivack.jpg/1200px-Pizza_Margherita_stu_spivack.jpg'),
('Chicken Caesar Salad', 9.99, 450, 4, true, 'https://reciperunner.com/wp-content/uploads/2017/07/Grilled-Chicken-Caesar-Salad-Photograph.jpg'),
('Beef Burger', 14.99, 950, 5, true, 'https://www.certifiedirishangus.ie/wp-content/uploads/2019/11/TheUltimateBurgerwBacon_RecipePic-1200x675.jpg'),
('Vegetable Pasta', 11.99, 600, 3, true, NULL); --No Image URL for testing

-- For dietary restrictions and allergens, we need separate inserts due to @ElementCollection
INSERT INTO menu_item_dietary_restrictions (menu_item_item_id, dietary_restrictions) 
VALUES 
(2, 'Gluten-Free'),
(4, 'Vegetarian');

INSERT INTO menu_item_allergen_information (menu_item_item_id, allergen_information)
VALUES 
(1, 'Gluten'),
(1, 'Dairy'),
(2, 'Dairy'),
(3, 'Gluten'); 