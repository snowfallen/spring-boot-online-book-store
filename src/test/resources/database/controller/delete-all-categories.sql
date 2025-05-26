DELETE FROM books_categories;
DELETE FROM categories;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1;
