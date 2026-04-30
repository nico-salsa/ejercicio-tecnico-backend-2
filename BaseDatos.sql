DROP DATABASE IF EXISTS customer_service;
DROP DATABASE IF EXISTS account_service;

CREATE DATABASE customer_service;
CREATE DATABASE account_service;

\connect customer_service;

CREATE TABLE persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    age INTEGER NOT NULL,
    identification VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(180) NOT NULL,
    phone VARCHAR(30) NOT NULL
);

CREATE TABLE customers (
    person_id BIGINT PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    status BOOLEAN NOT NULL,
    CONSTRAINT fk_customers_person
        FOREIGN KEY (person_id)
        REFERENCES persons (id)
        ON DELETE CASCADE
);

INSERT INTO persons (id, name, gender, age, identification, address, phone) VALUES
    (1, 'Jose Lema', 'Masculino', 30, '0102030405', 'Otavalo sn y principal', '098254785'),
    (2, 'Marianela Montalvo', 'Femenino', 28, '1112131415', 'Amazonas y NNUU', '097548965'),
    (3, 'Juan Osorio', 'Masculino', 35, '2122232425', '13 junio y Equinoccial', '098874587');

INSERT INTO customers (person_id, customer_id, password, status) VALUES
    (1, 'JL001', '1234', TRUE),
    (2, 'MM001', '5678', TRUE),
    (3, 'JO001', '1245', TRUE);

SELECT setval('persons_id_seq', 3, true);

\connect account_service;

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_type VARCHAR(40) NOT NULL,
    initial_balance NUMERIC(19, 2) NOT NULL,
    available_balance NUMERIC(19, 2) NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    status BOOLEAN NOT NULL
);

CREATE TABLE movements (
    id BIGSERIAL PRIMARY KEY,
    movement_date TIMESTAMP NOT NULL,
    movement_type VARCHAR(40) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_movements_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE CASCADE
);

INSERT INTO accounts (
    id,
    account_number,
    account_type,
    initial_balance,
    available_balance,
    customer_id,
    customer_name,
    status
) VALUES
    (1, '478758', 'Ahorro', 2000.00, 1425.00, 'JL001', 'Jose Lema', TRUE),
    (2, '225487', 'Corriente', 100.00, 700.00, 'MM001', 'Marianela Montalvo', TRUE),
    (3, '495878', 'Ahorros', 0.00, 150.00, 'JO001', 'Juan Osorio', TRUE),
    (4, '496825', 'Ahorros', 540.00, 0.00, 'MM001', 'Marianela Montalvo', TRUE),
    (5, '585545', 'Corriente', 1000.00, 1000.00, 'JL001', 'Jose Lema', TRUE);

INSERT INTO movements (
    id,
    movement_date,
    movement_type,
    amount,
    balance,
    account_id
) VALUES
    (1, '2022-02-10 10:00:00', 'RETIRO', -575.00, 1425.00, 1),
    (2, '2022-02-10 11:00:00', 'DEPOSITO', 600.00, 700.00, 2),
    (3, '2022-02-10 12:00:00', 'DEPOSITO', 150.00, 150.00, 3),
    (4, '2022-02-08 09:00:00', 'RETIRO', -540.00, 0.00, 4);

SELECT setval('accounts_id_seq', 5, true);
SELECT setval('movements_id_seq', 4, true);
