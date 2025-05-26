DELETE FROM books_categories;

DELETE FROM books;
ALTER TABLE books AUTO_INCREMENT = 1;

DELETE FROM categories;
ALTER TABLE categories AUTO_INCREMENT = 1;
