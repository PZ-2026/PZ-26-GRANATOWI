-- 1. Użytkownicy (Rola jako tekst bezpośrednio w tabeli)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- 'ADMIN', 'ARTIST', 'BUYER'
    balance DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Moduł Sprzedawcy
CREATE TABLE IF NOT EXISTS seller_descriptions (
    id SERIAL PRIMARY KEY,
    short_description TEXT,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS seller_user_follows (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    seller_id BIGINT REFERENCES users(id)
);

-- 3. Kategorie i Dzieła
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS artworks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(15,2),
    is_priceless BOOLEAN DEFAULT FALSE,
    artist VARCHAR(255),
    image_path VARCHAR(255),
    width FLOAT,
    height FLOAT,
    depth FLOAT,
    category_id INTEGER REFERENCES categories(id),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    is_sold BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Koszyk
CREATE TABLE IF NOT EXISTS carts (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_items (
    id SERIAL PRIMARY KEY,
    artwork_id BIGINT REFERENCES artworks(id),
    quantity INTEGER DEFAULT 1,
    cart_id INTEGER REFERENCES carts(id) ON DELETE CASCADE
);

-- 5. Zamówienia i Sprzedaż
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    total_price DECIMAL(15,2),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    artwork_id BIGINT REFERENCES artworks(id),
    quantity INTEGER,
    price DECIMAL(15,2),
    order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sales (
    id SERIAL PRIMARY KEY,
    price DECIMAL(15,2),
    sold_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    artwork_id BIGINT REFERENCES artworks(id),
    user_id BIGINT REFERENCES users(id) -- Kupujący
);

-- 6. Donacje i Adresy
CREATE TABLE IF NOT EXISTS donations (
    id SERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES users(id),
    seller_id BIGINT REFERENCES users(id),
    amount DECIMAL(15,2)
);

CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(255),
    postal_code VARCHAR(20),
    street VARCHAR(255),
    house_number VARCHAR(20),
    apartment_number VARCHAR(20),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Historia Transakcji Portfela
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_income BOOLEAN NOT NULL
);