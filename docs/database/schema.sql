DROP TABLE IF EXISTS booking_services;
DROP TABLE IF EXISTS room_change_requests;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS room_images;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS room_types;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS app_users;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
                       role_id INT IDENTITY(1,1) PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE app_users (
                           user_id INT IDENTITY(1,1) PRIMARY KEY,
                           full_name NVARCHAR(100) NOT NULL,
                           email VARCHAR(100) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           phone VARCHAR(20),
                           role_id INT NOT NULL,
                           status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                           created_at DATETIME2 DEFAULT GETDATE(),

                           CONSTRAINT fk_user_role
                               FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE room_types (
                            room_type_id INT IDENTITY(1,1) PRIMARY KEY,
                            type_name NVARCHAR(100) NOT NULL,
                            description NVARCHAR(500),
                            max_guests INT NOT NULL,
                            base_price DECIMAL(18,2) NOT NULL
);

CREATE TABLE rooms (
                       room_id INT IDENTITY(1,1) PRIMARY KEY,
                       room_number VARCHAR(20) NOT NULL UNIQUE,
                       room_type_id INT NOT NULL,
                       floor_number INT,
                       status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
                       description NVARCHAR(500),

                       CONSTRAINT fk_room_room_type
                           FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
);

CREATE TABLE room_images (
                             image_id INT IDENTITY(1,1) PRIMARY KEY,
                             room_id INT NOT NULL,
                             image_url VARCHAR(500) NOT NULL,
                             is_main BIT DEFAULT 0,

                             CONSTRAINT fk_room_image_room
                                 FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE services (
                          service_id INT IDENTITY(1,1) PRIMARY KEY,
                          service_name NVARCHAR(100) NOT NULL,
                          description NVARCHAR(500),
                          price DECIMAL(18,2) NOT NULL,
                          status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE bookings (
                          booking_id INT IDENTITY(1,1) PRIMARY KEY,
                          customer_id INT NOT NULL,
                          room_id INT NOT NULL,
                          check_in_date DATE NOT NULL,
                          check_out_date DATE NOT NULL,
                          actual_check_in DATETIME2 NULL,
                          actual_check_out DATETIME2 NULL,
                          number_of_guests INT NOT NULL,
                          total_amount DECIMAL(18,2) NOT NULL,
                          status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                          created_at DATETIME2 DEFAULT GETDATE(),

                          CONSTRAINT fk_booking_customer
                              FOREIGN KEY (customer_id) REFERENCES app_users(user_id),

                          CONSTRAINT fk_booking_room
                              FOREIGN KEY (room_id) REFERENCES rooms(room_id),

                          CONSTRAINT ck_booking_date
                              CHECK (check_out_date > check_in_date)
);

CREATE TABLE booking_services (
                                  booking_service_id INT IDENTITY(1,1) PRIMARY KEY,
                                  booking_id INT NOT NULL,
                                  service_id INT NOT NULL,
                                  quantity INT NOT NULL DEFAULT 1,
                                  price DECIMAL(18,2) NOT NULL,

                                  CONSTRAINT fk_booking_service_booking
                                      FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),

                                  CONSTRAINT fk_booking_service_service
                                      FOREIGN KEY (service_id) REFERENCES services(service_id)
);

CREATE TABLE payments (
                          payment_id INT IDENTITY(1,1) PRIMARY KEY,
                          booking_id INT NOT NULL,
                          amount DECIMAL(18,2) NOT NULL,
                          payment_method VARCHAR(50) NOT NULL,
                          payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                          paid_at DATETIME2 NULL,

                          CONSTRAINT fk_payment_booking
                              FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

CREATE TABLE room_change_requests (
                                      request_id INT IDENTITY(1,1) PRIMARY KEY,
                                      booking_id INT NOT NULL,
                                      requested_room_id INT NULL,
                                      reason NVARCHAR(500),
                                      status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                      created_at DATETIME2 DEFAULT GETDATE(),

                                      CONSTRAINT fk_room_change_booking
                                          FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),

                                      CONSTRAINT fk_room_change_requested_room
                                          FOREIGN KEY (requested_room_id) REFERENCES rooms(room_id)
);