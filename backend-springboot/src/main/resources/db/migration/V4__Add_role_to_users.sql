-- V4__Add_role_to_users.sql
-- Them cot role, mac dinh ai dang ky cung la CUSTOMER
ALTER TABLE users
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER';