-- =========================
--  USUARIOS DE PRUEBA
-- =========================
INSERT INTO users (id, name, phone, email, password, photo_url, date_birth, created_at, status, role)
VALUES
    ('u001', 'María Gómez', '+573001112233', 'maria.gomez@example.com',
     '$2b$10$A1bCdEfGhIjKlMnOpQrStUvWxYz1234567890abcdEfGhIj',
     'https://example.com/photos/maria.jpg', '1990-06-15', '2025-10-05 14:30:00', 'ACTIVE', 'GUEST'),

    ('u002', 'Juan Pérez', '+573224445566', 'juan.perez@example.com',
     '$2b$10$ZyXwVuTsRqPoNmLkJiHgFeDcBa9876543210ZYXWVUTSRQPON',
     'https://example.com/photos/juan.jpg', '1985-12-02', '2025-09-28 09:45:00', 'ACTIVE', 'HOST'),

    ('u003', 'Laura Mendoza', NULL, 'laura.mendoza@example.com',
     '$2b$10$MnOpQrStUvWxYz1234567890abcdefGhIjKlMnOpQrStUvWxY',
     NULL, '1998-03-22', '2025-10-01 17:20:00', 'INACTIVE', 'ADMIN');

-- =========================
--  HOST PROFILE
-- =========================
INSERT INTO host_profile (id, legal_document, about_me, created_at, user_id)
VALUES
    (1, 'CC123456789', 'Anfitrión con amplia experiencia en alojamiento turístico.', '2025-09-15 10:00:00', 'u002');

-- =========================
--  ACCOMMODATIONS
-- =========================
INSERT INTO accommodation (id, city, title, description, max_guests, price_per_night, average_rate, created_at, status, host_id, address_city, address)
VALUES
    (1, 'Medellín', 'Apartamento moderno con vista panorámica', 'Apartamento moderno con vista a la ciudad y acceso a piscina y gimnasio.', 4, 320000, 4.8, '2025-09-15 10:20:00', 'ACTIVE', 'u002', 'Medellín', 'Carrera 45 #23-10'),
    (2, 'Cartagena', 'Casa colonial en el centro histórico', 'Casa colonial con terraza y jacuzzi, ideal para grupos grandes.', 8, 580000, 4.6, '2025-08-30 18:45:00', 'ACTIVE', 'u002', 'Cartagena', 'Calle 10 #5-22'),
    (3, 'Bogotá', 'Estudio céntrico en Chapinero', 'Estudio acogedor cerca de restaurantes y transporte.', 2, 190000, 4.2, '2025-10-02 09:10:00', 'INACTIVE', 'u002', 'Bogotá', 'Carrera 7 #72-50');

-- =========================
--  LOCATIONS (embebidos)
-- =========================
UPDATE accommodation
SET address_city = 'Medellín', address = 'Carrera 45 #23-10'
WHERE id = 1;

UPDATE accommodation
SET address_city = 'Cartagena', address = 'Calle 10 #5-22'
WHERE id = 2;

UPDATE accommodation
SET address_city = 'Bogotá', address = 'Carrera 7 #72-50'
WHERE id = 3;

-- =========================
--  IMÁGENES DE ALOJAMIENTOS
-- =========================
INSERT INTO accommodation_images (accommodation_id, image)
VALUES
    (1, 'https://example.com/images/medellin1.jpg'),
    (1, 'https://example.com/images/medellin2.jpg'),
    (2, 'https://example.com/images/cartagena1.jpg'),
    (2, 'https://example.com/images/cartagena2.jpg'),
    (3, 'https://example.com/images/bogota1.jpg');

-- =========================
--  SERVICIOS DE ALOJAMIENTOS
-- =========================
INSERT INTO accommodation_services (accommodation_id, service)
VALUES
    (1, 'WIFI'),
    (1, 'POOL'),
    (1, 'AIR_CONDITIONING'),
    (2, 'WIFI'),
    (2, 'PARKING'),
    (3, 'BREAKFAST');

-- =========================
--  RESERVAS
-- =========================
INSERT INTO reservation (id, created_at, check_in, check_out, guests_number, total_price, reservation_status, guest_id, accommodation_id)
VALUES
    (1, '2025-10-05 10:00:00', '2025-11-01 13:00:00', '2025-11-07 12:00:00', 2, 640000, 'PENDING', 'u001', 1),
    (2, '2025-10-06 11:00:00', '2025-12-15 14:00:00', '2025-12-20 12:00:00', 3, 870000, 'CONFIRMED', 'u001', 2);

-- =========================
--  REVIEWS Y REPLIES
-- =========================
INSERT INTO review (id, user_id, accommodation_id, reservation_id, comment, rating, rate, created_at)
VALUES
    (1, 'u001', 1, 1, 'Excelente alojamiento, muy limpio y buena ubicación.', 5, 5, '2025-11-08 12:00:00'),
    (2, 'u001', 2, 2, 'Buena atención del anfitrión.', 4, 4, '2025-12-21 15:00:00');

INSERT INTO reply (id, message, replied_at, review_id)
VALUES
    (1, '¡Gracias por tu comentario, María!', '2025-11-09 10:00:00', 1),
    (2, 'Nos alegra que hayas disfrutado tu estadía.', '2025-12-22 09:30:00', 2);

-- =========================
--  PASSWORD RESET CODES
-- =========================
INSERT INTO password_reset_code (id, code, created_at, user_id)
VALUES
    (1, 'ABC123', '2025-10-01 08:00:00', 'u001'),
    (2, 'XYZ789', '2025-10-02 09:00:00', 'u002');
