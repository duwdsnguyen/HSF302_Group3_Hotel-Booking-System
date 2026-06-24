INSERT INTO roles (role_name)
VALUES
    ('ADMIN'),
    ('RECEPTIONIST'),
    ('CUSTOMER');

INSERT INTO app_users (full_name, email, password_hash, phone, role_id)
VALUES
    (N'Admin User', 'admin@gmail.com', '123456', '0900000001', 1),
    (N'Receptionist User', 'receptionist@gmail.com', '123456', '0900000002', 2),
    (N'Nguyen Van A', 'customer1@gmail.com', '123456', '0900000003', 3),
    (N'Tran Thi B', 'customer2@gmail.com', '123456', '0900000004', 3);

INSERT INTO room_types (type_name, description, max_guests, base_price)
VALUES
    (N'Standard Room', N'Basic room for short stays', 2, 500000),
    (N'Deluxe Room', N'Comfortable room with better facilities', 3, 800000),
    (N'Suite Room', N'Large luxury room for families or VIP guests', 4, 1500000);

INSERT INTO rooms (room_number, room_type_id, floor_number, status, description)
VALUES
    ('101', 1, 1, 'AVAILABLE', N'Standard room on floor 1'),
    ('102', 1, 1, 'AVAILABLE', N'Standard room on floor 1'),
    ('201', 2, 2, 'AVAILABLE', N'Deluxe room on floor 2'),
    ('202', 2, 2, 'MAINTENANCE', N'Deluxe room under maintenance'),
    ('301', 3, 3, 'AVAILABLE', N'Suite room on floor 3');

INSERT INTO room_images (room_id, image_url, is_main)
VALUES
    (1, '/images/rooms/room-101.jpg', 1),
    (2, '/images/rooms/room-102.jpg', 1),
    (3, '/images/rooms/room-201.jpg', 1),
    (4, '/images/rooms/room-202.jpg', 1),
    (5, '/images/rooms/room-301.jpg', 1);

INSERT INTO services (service_name, description, price, status)
VALUES
    (N'Breakfast', N'Daily breakfast service', 100000, 'ACTIVE'),
    (N'Airport Shuttle', N'Airport pickup or drop-off service', 300000, 'ACTIVE'),
    (N'Spa', N'Spa and massage service', 500000, 'ACTIVE'),
    (N'Laundry', N'Clothes washing service', 80000, 'ACTIVE');

INSERT INTO bookings (
    customer_id,
    room_id,
    check_in_date,
    check_out_date,
    number_of_guests,
    total_amount,
    status
)
VALUES
    (3, 1, '2026-07-01', '2026-07-03', 2, 1000000, 'CONFIRMED'),
    (4, 3, '2026-07-05', '2026-07-08', 2, 2400000, 'PENDING');

INSERT INTO booking_services (booking_id, service_id, quantity, price)
VALUES
    (1, 1, 2, 100000),
    (1, 4, 1, 80000),
    (2, 2, 1, 300000);

INSERT INTO payments (booking_id, amount, payment_method, payment_status, paid_at)
VALUES
    (1, 1000000, 'CASH', 'PAID', GETDATE()),
    (2, 2400000, 'BANK_TRANSFER', 'PENDING', NULL);

INSERT INTO room_change_requests (booking_id, requested_room_id, reason, status)
VALUES
    (1, 3, N'Customer wants a larger room', 'PENDING');