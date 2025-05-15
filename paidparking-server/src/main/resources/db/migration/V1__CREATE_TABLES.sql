CREATE TABLE building (
    id SERIAL PRIMARY KEY,
    location_name VARCHAR(100) UNIQUE NOT NULL,
    address VARCHAR(255) NOT NULL CHECK (LENGTH(address) >= 5),
    total_parking_spots BIGINT,
    available_parking_spots BIGINT,
    cost_per_hour BIGINT,
    working_hours_start TIME,
    working_hours_end TIME
);

CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    car_number VARCHAR(20) UNIQUE NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    color VARCHAR(50) DEFAULT 'UNDEFINED'
);

CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE user_car (
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, car_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE CASCADE
);

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    car_id BIGINT,
    building_id BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE SET NULL,
    CONSTRAINT fk_building FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE SET NULL
);

CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    amount BIGINT NOT NULL CHECK (amount > 0),
    reservation_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    receipt_uploaded_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    receipt_file BYTEA,
    receipt_file_name VARCHAR(255),
    receipt_content_type VARCHAR(100),
    CONSTRAINT fk_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE
);

CREATE TABLE reservation_history (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_first_name VARCHAR(100),
    user_last_name VARCHAR(100),
    user_phone_number VARCHAR(20),
    car_id BIGINT,
    car_number VARCHAR(20),
    building_id BIGINT,
    building_address VARCHAR(255),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_amount BIGINT,
    payment_status VARCHAR(50),
    payment_external_id VARCHAR(255),
    payment_date TIMESTAMP,
    payment_confirmation_date TIMESTAMP,
    payment_redirect_url VARCHAR(255),
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);