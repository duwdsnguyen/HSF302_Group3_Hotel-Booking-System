DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       user_id INT IDENTITY(1,1) PRIMARY KEY,
                       full_name NVARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE rooms (
                       room_id INT IDENTITY(1,1) PRIMARY KEY,
                       room_number VARCHAR(20) NOT NULL UNIQUE,
                       room_type NVARCHAR(50) NOT NULL,
                       price DECIMAL(18,2) NOT NULL,
                       status VARCHAR(30) NOT NULL
);

CREATE TABLE bookings (
                          booking_id INT IDENTITY(1,1) PRIMARY KEY,
                          user_id INT NOT NULL,
                          room_id INT NOT NULL,
                          check_in_date DATE NOT NULL,
                          check_out_date DATE NOT NULL,
                          status VARCHAR(30) NOT NULL,

                          CONSTRAINT fk_booking_user
                              FOREIGN KEY (user_id) REFERENCES users(user_id),

                          CONSTRAINT fk_booking_room
                              FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);