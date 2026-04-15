-- ==========================================================
-- 1. UŻYTKOWNICY (Bez tabeli roles - rola jako string)
-- Hasła (BCrypt): admin123, artist123, buyer123
-- ==========================================================
INSERT INTO users (id, username, first_name, last_name, email, password, role, balance, is_verified) VALUES
-- Admin
(1, 'admin_boss', 'Robert', 'Gie', 'admin@gmail.com', '$2a$10$QKF1lCALKVYAYcQqKFM.XON5/0jo4wNjaVc./seo/foE59Kk46oc6', 'ADMIN', 0.00, TRUE),
-- Sprzedawcy (ARTIST)
(2, 'vincent_v', 'Vincent', 'van Gogh', 'artist1@art.pl', '$2a$10$0H2LUr1P9YTYzkEs5ZWVbea.Q.iIo56tDykg240GErzFHdgZGL16K', 'ARTIST', 500.00, TRUE),
(3, 'anna_art', 'Anna', 'Nowak', 'artist2@art.pl', '$2a$10$0H2LUr1P9YTYzkEs5ZWVbea.Q.iIo56tDykg240GErzFHdgZGL16K', 'ARTIST', 1200.00, FALSE),
(4, 'sculptor_max', 'Maksymilian', 'Rzeźbiarz', 'artist3@art.pl', '$2a$10$0H2LUr1P9YTYzkEs5ZWVbea.Q.iIo56tDykg240GErzFHdgZGL16K', 'ARTIST', 0.00, TRUE),
-- Kupujący (BUYER)
(5, 'jan_kowalski', 'Jan', 'Kowalski', 'buyer1@gmail.pl', '$2a$10$gBNvBS5dAfIrNoDdVFDphe2THId1ACcJ2scXaL8bMycngvy8SARSO', 'BUYER', 3000.00, FALSE),
(6, 'marta_k', 'Marta', 'Kwiatkowska', 'buyer2@gmail.pl', '$2a$10$gBNvBS5dAfIrNoDdVFDphe2THId1ACcJ2scXaL8bMycngvy8SARSO', 'BUYER', 1500.00, FALSE),
(7, 'piotr_t', 'Piotr', 'Tester', 'buyer3@gmail.pl', '$2a$10$gBNvBS5dAfIrNoDdVFDphe2THId1ACcJ2scXaL8bMycngvy8SARSO', 'BUYER', 500.00, FALSE)
ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 2. OPISY SPRZEDAWCÓW (Seller Descriptions)
-- ==========================================================
INSERT INTO seller_descriptions (id, user_id, short_description) VALUES
(1, 2, 'Mistrz światła i koloru, postimpresjonista.'),
(2, 3, 'Sztuka nowoczesna, ilustracje cyfrowe.'),
(3, 4, 'Rzeźbiarz pracujący w marmurze i granicie.')
ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 3. KATEGORIE
-- ==========================================================
INSERT INTO categories (id, name, description, slug, parent_id, is_active, display_order, icon_name, color) VALUES
(1, 'Malarstwo', 'Wszystkie rodzaje dzieł malarskich', 'malarstwo', null, true, 1, 'palette', '#E91E63'),
(2, 'Malarstwo olejne', 'Klasyczne malarstwo olejne', 'malarstwo-olejne', 1, true, 1, 'brush', '#E91E63'),
(3, 'Akwarele', 'Delikatne malarstwo akwarelowe', 'akwarele', 1, true, 2, 'water_drop', '#2196F3'),
(4, 'Rzeźba', 'Trójwymiarowe dzieła sztuki', 'rzezba', null, true, 2, 'sculpture', '#795548'),
(5, 'Rzeźba kamienna', 'Klasyczne rzeźby z kamienia', 'rzezba-kamienna', 4, true, 1, 'architecture', '#795548'),
(6, 'Grafika', 'Grafika artystyczna i użytkowa', 'grafika', null, true, 3, 'draw', '#4CAF50'),
(7, 'Fotografia', 'Fotografia artystyczna', 'fotografia', null, true, 4, 'camera_alt', '#9C27B0')
ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 4. DZIEŁA SZTUKI (Artworks)
-- ==========================================================
INSERT INTO artworks (id, title, description, price, is_priceless, artist, category_id, user_id, status) VALUES
(1, 'Gwiaździsta Noc', 'Opis klasyka', 1200.00, false, 'Vincent van Gogh', 2, 2, 'AVAILABLE'),
(2, 'Słoneczniki', 'Bukiet w wazonie', 900.00, false, 'Vincent van Gogh', 2, 2, 'SOLD'),
(3, 'Neon City', 'Grafika komputerowa', 450.00, false, 'Anna Nowak', 6, 3, 'AVAILABLE'),
(4, 'Kamienna Twarz', 'Rzeźba z piaskowca', 2100.00, false, 'Maksymilian Rzeźbiarz', 5, 4, 'AVAILABLE'),
(5, 'Bez tytułu', 'Eksperyment rzeźbiarski', null, true, 'Maksymilian Rzeźbiarz', 4, 4, 'AVAILABLE')
ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 5. RELACJE (Obserwacje i Donacje)
-- ==========================================================
INSERT INTO seller_user_follows (id, user_id, seller_id) VALUES
(1, 5, 2), (2, 5, 3), -- Jan obserwuje Vincenta i Annę
(3, 6, 4)          -- Marta obserwuje Maksymiliana
ON CONFLICT (id) DO NOTHING;

INSERT INTO donations (id, client_id, seller_id, amount) VALUES
(1, 5, 2, 50.00), (2, 6, 4, 100.00)
ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 6. KOSZYKI I ZAMÓWIENIA
-- ==========================================================
INSERT INTO carts (id, user_id) VALUES (1, 5), (2, 6) ON CONFLICT (id) DO NOTHING;

-- Jan (5) ma Gwiaździstą Noc (1) w koszyku
INSERT INTO cart_items (id, artwork_id, quantity, cart_id) VALUES (1, 1, 1, 1) ON CONFLICT (id) DO NOTHING;

-- Zamówienie zrealizowane (Jan kupił Słoneczniki)
INSERT INTO orders (id, user_id, total_price, status) VALUES (1, 5, 900.00, 'PAID') ON CONFLICT (id) DO NOTHING;
INSERT INTO order_items (id, artwork_id, quantity, price, order_id) VALUES (1, 2, 1, 900.00, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO sales (id, price, artwork_id, user_id) VALUES (1, 900.00, 2, 5) ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 7. ADRESY
-- ==========================================================

INSERT INTO addresses (id, user_id, city, postal_code, street, house_number) VALUES
(1, 1, 'Warszawa', '00-001', 'Adminowa', '1'),
(2, 2, 'Kraków', '31-001', 'Zaułek Artysty', '5A'),
(3, 5, 'Gdańsk', '80-001', 'Kupiecka', '12'),
(4, 6, 'Łódź', '90-001', 'Piotrkowska', '100')
ON CONFLICT (id) DO NOTHING;

-- ==========================================================
-- 8. SYNCHRONIZACJA LICZNIKÓW
-- ==========================================================
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('artworks_id_seq', (SELECT COALESCE(MAX(id), 1) FROM artworks));
SELECT setval('categories_id_seq', (SELECT COALESCE(MAX(id), 1) FROM categories));
SELECT setval('carts_id_seq', (SELECT COALESCE(MAX(id), 1) FROM carts));
SELECT setval('orders_id_seq', (SELECT COALESCE(MAX(id), 1) FROM orders));
SELECT setval('addresses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM addresses));
SELECT setval('seller_descriptions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM seller_descriptions));
SELECT setval('seller_user_follows_id_seq', (SELECT COALESCE(MAX(id), 1) FROM seller_user_follows));
SELECT setval('donations_id_seq', (SELECT COALESCE(MAX(id), 1) FROM donations));
SELECT setval('cart_items_id_seq', (SELECT COALESCE(MAX(id), 1) FROM cart_items));
SELECT setval('order_items_id_seq', (SELECT COALESCE(MAX(id), 1) FROM order_items));
SELECT setval('sales_id_seq', (SELECT COALESCE(MAX(id), 1) FROM sales));