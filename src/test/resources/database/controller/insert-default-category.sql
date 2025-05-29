DELETE FROM books_categories;
DELETE FROM categories;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1;

INSERT INTO categories (name, description, is_deleted) VALUES
('Test Category', 'Test Description', false);