INSERT INTO users (phone_number, password, full_name)
VALUES ('0987301126', '123456', 'Huy Onii-chan');

INSERT INTO users (phone_number, password, full_name)
VALUES ('0376171242', '111111', 'Cao Dat');

INSERT INTO users (phone_number, password, full_name)
VALUES ('0329815572', '366736', 'pbqhuy');

INSERT INTO users (phone_number, password, full_name)
VALUES ('6767676767', '666666', 'Trần Thanh Dân');

-- Thêm ảnh cho Cao Dat
UPDATE users
SET avatar_url = 'https://res.cloudinary.com/dkm39i1z8/image/upload/v1778040444/skadi_mi07hp.jpg'
WHERE phone_number = '0376171242';

-- Thêm ảnh cho pbqhuy
UPDATE users
SET avatar_url = 'https://res.cloudinary.com/dkm39i1z8/image/upload/v1778040445/usagi_hp1luy.jpg'
WHERE phone_number = '0329815572';

-- Thêm ảnh cho Trần Thanh Dân
UPDATE users
SET avatar_url = 'https://res.cloudinary.com/dkm39i1z8/image/upload/v1778040445/Dan-Kun_gfrakx.jpg'
WHERE phone_number = '6767676767';