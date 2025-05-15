INSERT INTO role (role) VALUES ('ROLE_USER'), ('ROLE_ADMIN');
INSERT INTO users (first_name, last_name, phone_number, password)
VALUES ('Admin', 'Adminov', '+7234567890', '$2a$10$examplehashedpassword');
INSERT INTO user_role (user_id, role_id)
VALUES (1, 1), (1, 2);
