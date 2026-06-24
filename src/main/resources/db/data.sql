INSERT INTO users (full_name, email, password, role)
VALUES
    ('Admin', 'admin@gmail.com', '123456', 'ADMIN'),
    ('Receptionist', 'receptionist@gmail.com', '123456', 'RECEPTIONIST');

INSERT INTO rooms (room_number, room_type, price, status)
VALUES
    ('101', 'Standard', 500000, 'AVAILABLE'),
    ('102', 'Deluxe', 800000, 'AVAILABLE'),
    ('201', 'Suite', 1500000, 'AVAILABLE');