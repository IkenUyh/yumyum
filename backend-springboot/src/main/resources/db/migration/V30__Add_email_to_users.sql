ALTER TABLE users ADD COLUMN email VARCHAR(255) UNIQUE NULL;

UPDATE users SET email = 'huy@gmail.com' WHERE phone_number = '0987301126';
UPDATE users SET email = 'caodat@gmail.com' WHERE phone_number = '0376171242';
UPDATE users SET email = 'pbqhuy@gmail.com' WHERE phone_number = '0329815572';
