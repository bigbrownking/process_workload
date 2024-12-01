CREATE TABLE IF NOT EXISTS trainer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);

INSERT INTO trainer (username, first_name, last_name, is_active) VALUES
('alice.green', 'Alice', 'Green', TRUE),
('david.white', 'David', 'White', TRUE),
('sophia.black', 'Sophia', 'Black', TRUE),
('olivia.moore', 'Olivia', 'Moore', TRUE),
('james.wilson', 'James', 'Wilson', TRUE);