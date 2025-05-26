DELETE FROM books_categories;

DELETE FROM books;
ALTER TABLE books AUTO_INCREMENT = 1;

DELETE FROM categories;
ALTER TABLE categories AUTO_INCREMENT = 1;

INSERT INTO categories (name, description, is_deleted) VALUES
('Test Category', 'Test Category Description', false);

INSERT INTO books (title, author, isbn, price, description, cover_image, is_deleted) VALUES
('Book A for Test Category', 'Author A', '12345678909876', 20.00, 'Description for Book A', 'coverA.jpg', false),
('Book B for Test Category', 'Author B', '23456789098765', 25.50, 'Description for Book B', 'coverB.jpg', false),
('Book C for Test Category', 'Author C', '34567890987654', 30.75, 'Description for Book C', 'coverC.jpg', false);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1), (2, 1), (3, 1);
