CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    due_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL
);