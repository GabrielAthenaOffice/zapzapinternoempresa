CREATE TABLE usuarios(
    id SERIAL PRIMARY KEY UNIQUE NOT NULL,
    nome TEXT NOT NULL,
    email TEXT NOT NULL,
    senha TEXT NOT NULL,
    cargo TEXT NOT NULL,
    role TEXT NOT NULL,
);