-- Insertion for Crust (impasti)
INSERT INTO crust (code, description, price) VALUES ('NORMAL', 'Classic dough', 2.50);
INSERT INTO crust (code, description, price) VALUES ('MULTI_GRAIN', 'Whole grain dough', 3.00);
INSERT INTO crust (code, description, price) VALUES ('DEEP_DISK', 'Thick, deep dish dough', 3.50);

-- Insertion for Size (formati)
INSERT INTO size (code, description, price) VALUES ('BABY', 'Small size pizza', 0.00);  -- Base price for baby size pizza
INSERT INTO size (code, description, price) VALUES ('STANDARD', 'Medium size pizza', 1.50);
INSERT INTO size (code, description, price) VALUES ('FAMILY', 'Large family-size pizza', 3.00);

-- Insertion for Ingredient (ingredienti)
INSERT INTO ingredient (code, description, price) VALUES ('MUSHROOMS', 'Fresh mushrooms', 1.00);
INSERT INTO ingredient (code, description, price) VALUES ('PEPPERONI', 'Pepperoni slices', 1.50);
INSERT INTO ingredient (code, description, price) VALUES ('OLIVES', 'Green olives', 0.80);
INSERT INTO ingredient (code, description, price) VALUES ('TOMATO', 'Tomato slices', 0.50);
INSERT INTO ingredient (code, description, price) VALUES ('CHEESE', 'Mozzarella cheese', 1.20);

-- Insertion for Pizza (pizze)
-- Pizza 1: Margherita
INSERT INTO pizza (code, description, base_price) VALUES ('MARGHERITA', 'Classic Margherita', 5.00);

-- Pizza 2: Pepperoni
INSERT INTO pizza (code, description, base_price) VALUES ('PEPPERONI', 'Pepperoni pizza', 6.00);

-- Pizza 3: Veggie
INSERT INTO pizza (code, description, base_price) VALUES ('VEGGIE', 'Vegetarian pizza', 6.50);

-- Associating ingredients with pizzas
-- Margherita pizza with ingredients
INSERT INTO pizza_ingredient (pizza_id, ingredient_id) 
SELECT p.id, i.id FROM pizza p, ingredient i WHERE p.code = 'MARGHERITA' AND i.code IN ('TOMATO', 'CHEESE');

-- Pepperoni pizza with ingredients
INSERT INTO pizza_ingredient (pizza_id, ingredient_id) 
SELECT p.id, i.id FROM pizza p, ingredient i WHERE p.code = 'PEPPERONI' AND i.code IN ('TOMATO', 'CHEESE', 'PEPPERONI');

-- Veggie pizza with ingredients
INSERT INTO pizza_ingredient (pizza_id, ingredient_id) 
SELECT p.id, i.id FROM pizza p, ingredient i WHERE p.code = 'VEGGIE' AND i.code IN ('TOMATO', 'CHEESE', 'MUSHROOMS', 'OLIVES');

-- Associating sizes with pizzas
-- Margherita pizza available in different sizes
INSERT INTO pizza_size (pizza_id, size_id)
SELECT p.id, s.id FROM pizza p, size s WHERE p.code = 'MARGHERITA' AND s.code IN ('STANDARD', 'FAMILY');

-- Pepperoni pizza available in different sizes
INSERT INTO pizza_size (pizza_id, size_id)
SELECT p.id, s.id FROM pizza p, size s WHERE p.code = 'PEPPERONI' AND s.code IN ('STANDARD', 'FAMILY');

-- Veggie pizza available in different sizes
INSERT INTO pizza_size (pizza_id, size_id)
SELECT p.id, s.id FROM pizza p, size s WHERE p.code = 'VEGGIE' AND s.code IN ('STANDARD');

-- Associating crusts with pizzas
-- Margherita pizza available with different crusts
INSERT INTO pizza_crust (pizza_id, crust_id)
SELECT p.id, c.id FROM pizza p, crust c WHERE p.code = 'MARGHERITA' AND c.code IN ('NORMAL', 'MULTI_GRAIN');

-- Pepperoni pizza available with different crusts
INSERT INTO pizza_crust (pizza_id, crust_id)
SELECT p.id, c.id FROM pizza p, crust c WHERE p.code = 'PEPPERONI' AND c.code IN ('NORMAL', 'DEEP_DISK');

-- Veggie pizza available with different crusts
INSERT INTO pizza_crust (pizza_id, crust_id)
SELECT p.id, c.id FROM pizza p, crust c WHERE p.code = 'VEGGIE' AND c.code IN ('MULTI_GRAIN');
