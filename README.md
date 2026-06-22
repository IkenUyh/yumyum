<p align="center">
  <img src="docs/uit.png" alt="UIT Logo" width="600">
</p>

<h1 align="center">YumYum - Food Delivery Android Application 🍔</h1>

<p align="center">
  <strong>Đồ án môn học: Nhập môn ứng dụng di động</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
</p>

<details open>
  <summary><b>Mục lục</b></summary>
  <ol>
    <li><a href="#-thành-viên-nhóm">Thành viên nhóm</a></li>
    <li><a href="#-giới-thiệu-dự-án">Giới thiệu dự án</a></li>
    <li><a href="#-tính-năng-nổi-bật">Tính năng nổi bật</a></li>
    <li><a href="#-tech-stack">Tech Stack</a></li>
    <li><a href="#-cấu-trúc-thư-mục">Cấu trúc thư mục</a></li>
    <li><a href="#-hướng-dẫn-cài-đặt">Hướng dẫn cài đặt</a></li>
    <li><a href="#-demo--ảnh-chụp-màn-hình">Demo / Ảnh chụp màn hình</a></li>
  </ol>
</details>

---

## 👥 Thành viên nhóm

| STT | MSSV     | Họ và tên          | Vai trò | GitHub                                                  |
|:---:|:--------:|:-------------------|:-------:|:--------------------------------------------------------|
| 1   | 24520697 | Tô Kiến Huy        | Leader  | [@IkenUyh](https://github.com/IkenUyh)                  |
| 2   | 24520269 | Huỳnh Cao Đạt      | Member  | [@estellra](https://github.com/estellra)                 |
| 3   | 24520693 | Phan Bùi Quang Huy | Member  | [@pbqhuy](https://github.com/pbqhuy)                    |
| 4   | 24520248 | Trần Thanh Dân     | Member  | [@danttse](https://github.com/danttse)                   |

---

## 📖 Giới thiệu dự án

**YumYum & Food Delivery Integration** là một ứng dụng di động toàn diện kết hợp giữa dịch vụ giao đồ ăn (Food Delivery) và ví điện tử (E-Wallet) có khả năng tích hợp thanh toán qua **VNPay**. Hệ thống được xây dựng theo kiến trúc Client-Server, đảm bảo hiệu suất cao, bảo mật chặt chẽ và trải nghiệm người dùng mượt mà.

---

## 🌟 Tính năng nổi bật

### Dành cho Người dùng (Client App)
- **Xác thực đa lớp:** Đăng nhập, đăng ký an toàn. Hỗ trợ xác thực qua mã OTP và bảo mật giao dịch bằng mã PIN.
- **Khám phá ẩm thực:** Duyệt danh sách nhà hàng, món ăn với tính năng tìm kiếm thông minh và lọc linh hoạt.
- **Quản lý đơn hàng:** Thêm vào giỏ hàng, đặt món nhanh chóng và theo dõi trạng thái đơn hàng **thời gian thực (Real-time)**.
- **Ví điện tử (YumYum Pay):** Nạp/rút tiền, thanh toán nội bộ và tích hợp cổng thanh toán bên thứ ba (VNPay).
- **Định vị & Bản đồ:** Theo dõi vị trí giao hàng và tìm kiếm cửa hàng lân cận thông qua bản đồ tích hợp.
- **Thông báo:** Nhận thông báo đẩy (Push Notifications) về trạng thái đơn hàng, khuyến mãi.

### Dành cho Hệ thống (Backend API)
- **Bảo mật:** Phân quyền chặt chẽ bằng Spring Security kết hợp JSON Web Token (JWT).
- **Hiệu suất cao:** Tối ưu hóa truy vấn với Caching (Redis) và tìm kiếm tốc độ cao (Elasticsearch).
- **Đồng bộ thời gian thực:** Quản lý kết nối hai chiều (WebSocket) phục vụ tính năng chat và cập nhật trạng thái đơn hàng lập tức.

---

## 🛠 Tech Stack

### 📱 Frontend (Android App)
> Phát triển bằng **Java 11**, nhắm mục tiêu **Android SDK 36**.
- **Kiến trúc & Giao diện:** Android Native (XML), Material Design, ConstraintLayout.
- **Network & API:** Retrofit2, OkHttp3, Gson.
- **Bất đồng bộ & Real-time:** RxJava2, RxAndroid, StompProtocolAndroid (WebSocket).
- **Tiện ích:** Glide (Image Loading), ZXing (QR Code Scanner), MPAndroidChart (Charts & Graphs).
- **Bản đồ & Location:** OSMDroid, Google Play Services Location.
- **Cloud Services:** Firebase Cloud Messaging (FCM).

### ⚙️ Backend (Spring Boot API)
> Phát triển trên nền tảng **Java 21** với **Spring Boot 3.x**.
- **Cơ sở dữ liệu:** MySQL 8.0 (Spring Data JPA), Flyway (Database Migration).
- **Bộ nhớ đệm & Tìm kiếm:** Redis (Spring Data Redis), Elasticsearch.
- **Bảo mật:** Spring Security, JWT (HMAC-SHA256).
- **Giao tiếp Real-time:** Spring Boot WebSocket.
- **Lưu trữ đa phương tiện:** Cloudinary.
- **Tích hợp Cloud:** Spring Boot Mail, Firebase Admin SDK (Push Notification).
- **Triển khai:** Docker, Docker Compose.

---

## 📂 Cấu trúc thư mục

```bash
zalopay-android-integration/
├── android-studio-app/      # Source code ứng dụng Android (Frontend)
│   ├── app/                 # Module chính: Activities, Fragments, Layouts (XML)
│   └── build.gradle         # Gradle build script (app level)
├── backend-springboot/      # Source code Backend API Server
│   ├── src/                 # Controllers, Services, Repositories, Entities
│   ├── Dockerfile           # Script đóng gói Docker image cho backend
│   ├── docker-compose.yml   # Cấu hình triển khai nhanh với Docker (MySQL, Redis...)
│   └── pom.xml              # Maven dependencies configuration
└── docs/                    # Tài liệu đặc tả hệ thống, API Docs, thiết kế UI/UX
```

---

## 🚀 Hướng dẫn cài đặt

Vì hệ thống máy chủ (Backend Server) đã được triển khai chạy thực tế (Live), ta **chỉ cần chạy ứng dụng Android** là có thể trải nghiệm toàn bộ tính năng.

### 1. Khởi chạy Ứng dụng Android (Chính)

**Yêu cầu:** [Android Studio](https://developer.android.com/studio) bản mới nhất.

1. Khởi động Android Studio.
2. Chọn **Open** và trỏ đến thư mục `android-studio-app`.
3. Chờ Gradle đồng bộ (Sync) toàn bộ thư viện.
4. Mặc định ứng dụng đã được cấu hình trỏ tới máy chủ thật qua biến `BASE_URL = "https://kienhuy-dev.name.vn/"` trong file `build.gradle` (module `app`). Bạn **không cần** sửa đổi gì thêm.
5. Kết nối thiết bị Android (yêu cầu Android 7.0+) hoặc sử dụng Emulator.
6. Nhấn **Run** (`Shift + F10`) để build và trải nghiệm ứng dụng.

### 2. Khởi chạy Backend Server (Chỉ dành cho việc test dưới local)

Nếu bạn muốn tùy chỉnh API và tự chạy lại server trên máy tính cá nhân:

**Yêu cầu:** Máy tính cài sẵn [Java 21](https://jdk.java.net/21/) và [Docker](https://www.docker.com/).

```bash
# 1. Di chuyển vào thư mục backend
cd backend-springboot

# 2. Khởi tạo các dịch vụ phụ thuộc (MySQL, Redis, Elasticsearch...)
docker-compose up -d

# 3. Build và chạy ứng dụng Spring Boot
./mvnw spring-boot:run
```
> **Lưu ý:** Khi chạy local, server sẽ nằm ở `http://localhost:8080/`. Bạn nhớ vào file `build.gradle` ở Android app đổi lại biến `BASE_URL` để app trỏ đúng về local của bạn nhé.

---

## 📱 Demo / Ảnh chụp màn hình

*(Gắn link hình ảnh demo thực tế của ứng dụng tại đây)*

<div align="center">
  <img src="https://via.placeholder.com/200x400.png?text=Home+Screen" alt="Home" width="200"/>
  &nbsp;&nbsp;&nbsp;
  <img src="https://via.placeholder.com/200x400.png?text=Cart/Checkout" alt="Cart" width="200"/>
  &nbsp;&nbsp;&nbsp;
  <img src="https://via.placeholder.com/200x400.png?text=E-Wallet/VNPay" alt="VNPay" width="200"/>
</div>

---
<p align="center">
  Được phát triển với ❤️ bởi <b>Nhóm 14</b>
</p>
