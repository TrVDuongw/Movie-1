# 🎬 Hệ thống Quản lý Rạp Chiếu Phim

## Mô tả
Ứng dụng quản lý rạp chiếu phim với Java Swing và MySQL, 
hỗ trợ đặt vé trực tuyến và quản lý doanh thu.

## Tính năng
- ✅ Đặt vé xem phim với sơ đồ ghế trực quan
- ✅ Phân loại ghế VIP và ghế thường
- ✅ Thanh toán đa phương thức
- ✅ Quản lý phim, vé, và doanh thu (Admin)
- ✅ Báo cáo thống kê chi tiết

## Công nghệ
- Java Swing
- MySQL Database
- JDBC

## Cài đặt
1. Import database từ file `cinema_db.sql`
2. Cấu hình kết nối trong `DatabaseConnection.java`
3. Chạy `Main.java`

## Tài khoản Admin
- Mật khẩu: `admin`
```

### 5. 🗂️ **Cấu trúc thư mục đề xuất**
```
project/
├── src/
│   ├── DatabaseConnection.java
│   ├── Main.java
│   ├── Phim.java
│   ├── PhimDAO.java
│   ├── Ve.java
│   ├── VeDAO.java
│   ├── RapController.java
│   ├── RapChieuPhim.java
│   └── RapQuanLy.java
├── database/
│   └── cinema_db.sql
├── docs/
│   ├── slides.pptx
│   └── report.pdf
├── screenshots/
│   ├── user-interface.png
│   ├── admin-interface.png
│   └── seat-selection.png
├── .gitignore
└── README.md
