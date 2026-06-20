INSERT INTO users (phone_number, password, full_name, email)
VALUES ('0987301126', '123456', 'Huy Onii-chan', 'huykiento@gmail.com');

INSERT INTO users (phone_number, password, full_name, email)
VALUES ('0376171242', '111111', 'Cao Dat', 'charafrisksans024@gmail.com');

INSERT INTO users (phone_number, password, full_name, email)
VALUES ('0329815572', '555555', 'pbqhuy', 'pbqhuy@gmail.com');

INSERT INTO users (phone_number, password, full_name, email)
VALUES ('6767676767', '666666', 'Trần Thanh Dân', 'thanhdan020809@gmail.com');

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