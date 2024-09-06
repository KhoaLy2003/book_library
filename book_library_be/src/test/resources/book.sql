INSERT INTO book (book_id, average_rating, num_pages, publication_date, isbn, isbn13, title, authors, language_code,
                  publisher, status)
VALUES (1, 4.42, 352, '2003-11-01', '439554896', '9780439554893',
        'Harry Potter and the Chamber of Secrets (Harry Potter  #2)', 'J.K. Rowling', 'eng', 'Scholastic', 'AVAILABLE');

INSERT INTO book_inventory
    (book_id, current_quantity, total_quantity)
VALUES (1, 10, 10);