DROP TABLE IF EXISTS account;

CREATE TABLE account
(
    account_id             int          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    address                varchar(255) DEFAULT NULL,
    create_at              timestamp    DEFAULT NULL,
    current_borrowed_books int          DEFAULT NULL,
    email                  varchar(350) NOT NULL,
    first_name             varchar(200) DEFAULT NULL,
    last_name              varchar(200) DEFAULT NULL,
    membership_end_date    date         DEFAULT NULL,
    membership_number      varchar(50)  NOT NULL,
    membership_start_date  date         NOT NULL,
    membership_type        varchar(50)  DEFAULT NULL, -- Replaced ENUM with VARCHAR
    notes                  varchar(255) DEFAULT NULL, -- Replaced TINYTEXT with VARCHAR
    phone_number           varchar(20)  DEFAULT NULL,
    status                 varchar(50)  DEFAULT NULL, -- Replaced ENUM with VARCHAR
    total_books_borrowed   int          DEFAULT NULL,
    total_late_returns     int          DEFAULT NULL,
    update_at              timestamp    DEFAULT NULL,
    UNIQUE (email),
    UNIQUE (membership_number)
);
