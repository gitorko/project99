CREATE TABLE employee
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) UNIQUE,
    age     INTEGER,
    salary  DOUBLE PRECISION
);