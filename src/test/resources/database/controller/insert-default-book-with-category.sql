DELETE FROM books_categories WHERE book_id IN (SELECT id FROM books);
DELETE FROM books;
ALTER TABLE books ALTER COLUMN id RESTART WITH 1;

INSERT INTO books (title, author, isbn, price, description, cover_image, is_deleted) VALUES
('Test Book', 'Test Author', '1234567890123', 19.99, 'Test Description', 'test.jpg', false);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
