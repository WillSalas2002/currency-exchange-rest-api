CREATE TABLE currency (
                          id INTEGER PRIMARY KEY ,
                          code VARCHAR(50) UNIQUE NOT NULL,
                          full_name VARCHAR(150) NOT NULL,
                          sign VARCHAR(10) NOT NULL
);

CREATE TABLE exchange_rate (
                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                               base_currency_id INTEGER,
                               target_currency_id INTEGER,
                               rate DECIMAL(9, 6) NOT NULL,
                               UNIQUE (base_currency_id, target_currency_id),
                               FOREIGN KEY (base_currency_id) REFERENCES currency(id),
                               FOREIGN KEY (target_currency_id) REFERENCES currency(id)
);