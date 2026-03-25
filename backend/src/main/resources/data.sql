-- Role
INSERT INTO roles (id, name) VALUES (1, 'ADMIN'), (2, 'ARTIST'), (3, 'BUYER') ON CONFLICT DO NOTHING;

-- Użytkownicy (Hasła zahashowane BCrypt)
-- admin@gmail.com / admin123
-- artist@gmail.com / artist123
-- buyer@gmail.pl / buyer123
INSERT INTO users (id, username, email, password, role_id, balance) VALUES
(1, 'admin_boss', 'admin@gmail.com', '$2a$10$QKF1lCALKVYAYcQqKFM.XON5/0jo4wNjaVc./seo/foE59Kk46oc6', 1, 0.00),
(2, 'vincent_v', 'artist@gmail.com', '$2a$10$0H2LUr1P9YTYzkEs5ZWVbea.Q.iIo56tDykg240GErzFHdgZGL16K', 2, 5000.00),
(3, 'jan_kowalski', 'buyer@gmail.pl', '$2a$10$gBNvBS5dAfIrNoDdVFDphe2THId1ACcJ2scXaL8bMycngvy8SARSO', 3, 1000.00)
ON CONFLICT (id) DO NOTHING;

-- Konfiguracja
INSERT INTO system_settings (key, value, description)
VALUES ('platform_commission_percentage', '15', 'Procent prowizji pobieranej od każdej sprzedaży')
ON CONFLICT (key) DO NOTHING;

-- Kategorie
INSERT INTO categories (id, name) VALUES 
(1, 'Obraz'), 
(2, 'Rzeźba'), 
(3, 'Grafika'), 
(4, 'Fotografia'), 
(5, 'Malarstwo'),
(6, 'Rysunek') 
ON CONFLICT (id) DO NOTHING;

-- Dzieła
INSERT INTO artworks (id, title, price, is_priceless, artist, user_id, category_id, width, height, depth, is_sold, status) VALUES
(1, 'Gwiaździsta Noc', 1200.00, false, 'Vincent van Gogh', 2, 1, 73.7, 92.1, 2.0, false, 'AVAILABLE'),
(2, 'Dawid', null, true, 'Michał Anioł', 2, 2, 150.0, 517.0, 150.0, false, 'AVAILABLE'),
(3, 'Mona Lisa', 2500.00, false, 'Leonardo da Vinci', 2, 1, 53.0, 77.0, 2.5, false, 'AVAILABLE')
ON CONFLICT (id) DO NOTHING;

-- Synchronizacja liczników ID
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('artworks_id_seq', (SELECT MAX(id) FROM artworks));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));