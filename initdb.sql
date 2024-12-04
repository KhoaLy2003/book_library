CREATE DATABASE keycloak;
CREATE DATABASE book_library;

GRANT ALL PRIVILEGES ON keycloak.* TO 'user'@'%';
GRANT ALL PRIVILEGES ON book_library.* TO 'user'@'%';
FLUSH PRIVILEGES;