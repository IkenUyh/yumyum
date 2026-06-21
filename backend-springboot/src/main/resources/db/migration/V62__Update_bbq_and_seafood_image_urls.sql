-- V62__Update_bbq_and_seafood_image_urls.sql

UPDATE categories SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060353/images_w6ege8.jpg' WHERE name = 'Đồ Nướng & BBQ';
UPDATE categories SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060401/2-f19_npo95k.jpg' WHERE name = 'Hải Sản';

UPDATE foods SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060452/De-Suon-Bo-Nuong-Tai-Nha-Cuc-Ngon-4_l35ddx.webp' WHERE name = 'Dẻ Sườn Bò Mỹ Nướng Sauce BBQ';
UPDATE foods SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060485/thit-bo-cuon-nam-kim-cham_gu9cli.webp' WHERE name = 'Ba Chỉ Bò Cuộn Nấm Kim Châm';
UPDATE foods SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060519/thanh-pham-75_nudqag.jpg' WHERE name = 'Nầm Heo Nướng Sa Tế';

UPDATE foods SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060568/z5911268884438_89f8b6b7370c556ec5cc319170138b0c_a0m10j.jpg' WHERE name = 'Tôm Hùm Sốt Bơ Tỏi';
UPDATE foods SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060619/images_x8kdky.jpg' WHERE name = 'Cua Cà Mau Hấp Sả';
UPDATE foods SET image_url = 'https://res.cloudinary.com/dmhgfnxh9/image/upload/q_auto/f_auto/v1782060652/cach-lam-ngao-hap-sa-ot-dam-da-nong-hoi-ca-nha-me-tit-202006291324239176_shizys.jpg' WHERE name = 'Nghêu Hấp Sả Ớt';
