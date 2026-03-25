-- 1. Moduł administracji i użytkowników
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL -- 'ADMIN', 'ARTIST', 'BUYER'
    );

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Tu będzie hash BCrypt
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role_id INTEGER REFERENCES roles(id),
    balance DECIMAL(15,2) DEFAULT 0.00, -- Saldo użytkownika (dla Artysty i Kupującego)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 2. Moduł konfiguracji (np. prowizja)
CREATE TABLE IF NOT EXISTS system_settings (
    key VARCHAR(50) PRIMARY KEY,
    value VARCHAR(255) NOT NULL,
    description TEXT
    );

-- 3. Moduł zarządzania dziełami sztuki
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL -- 'Obraz', 'Rzeźba', 'Fotografia'
    );

CREATE TABLE IF NOT EXISTS artworks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(15,2),
    is_priceless BOOLEAN DEFAULT FALSE,
    artist VARCHAR(255), -- Nazwa artysty (może być inna niż użytkownik)
    width DECIMAL(8,2), -- Szerokość w cm
    height DECIMAL(8,2), -- Wysokość w cm
    depth DECIMAL(8,2), -- Głębokość w cm
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE, -- Sprzedawca
    category_id INTEGER REFERENCES categories(id),
    image_path VARCHAR(255),
    is_sold BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'SOLD', 'HIDDEN'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 4. Moduł transakcji
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT REFERENCES users(id),
    total_amount DECIMAL(15,2) NOT NULL, -- Kwota całkowita
    system_commission DECIMAL(15,2) NOT NULL, -- Prowizja pobrana przez system
    artist_earning DECIMAL(15,2) NOT NULL, -- Kwota dla artysty
    status VARCHAR(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS transaction_items (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT REFERENCES transactions(id) ON DELETE CASCADE,
    artwork_id BIGINT REFERENCES artworks(id),
    price_at_sale DECIMAL(15,2) NOT NULL -- Cena w momencie zakupu
    );

-- 5. Koszyk (dla Kupującego)
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    artwork_id BIGINT REFERENCES artworks(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 6. Adresy użytkowników (dla Kupującego i Administratora)
CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house_number VARCHAR(20) NOT NULL,
    apartment_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );