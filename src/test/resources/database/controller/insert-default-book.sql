DELETE FROM books_categories;
DELETE FROM books;
ALTER TABLE books ALTER COLUMN id RESTART WITH 1;
INSERT INTO books (title, author, isbn, price, description, cover_image, is_deleted) VALUES
('Test Book', 'Test Author', '1234567890123', 19.99, 'Test Description', 'test.jpg', false);
