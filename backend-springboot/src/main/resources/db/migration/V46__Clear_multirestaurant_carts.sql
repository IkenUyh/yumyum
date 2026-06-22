-- V46: Clear existing cart items to reset any invalid mixed-restaurant carts.
DELETE FROM cart_items;
