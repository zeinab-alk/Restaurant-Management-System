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