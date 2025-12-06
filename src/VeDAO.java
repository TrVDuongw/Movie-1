import java.sql.*;
import java.util.ArrayList;

public class VeDAO {

    public boolean addVe(Ve ve) {
        String sql = "INSERT INTO ve (ten_phim, ghe, ten_khach, email, gia_ve) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, ve.getTenPhim());
            pst.setString(2, ve.getGhe());
            pst.setString(3, ve.getTenKhach());
            pst.setString(4, ve.getEmail());
            pst.setInt(5, ve.getGiaVe());

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Ve> getAllVe() {
        ArrayList<Ve> list = new ArrayList<>();
        String sql = "SELECT * FROM ve ORDER BY ngay_dat DESC";

        Connection conn = DatabaseConnection.getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String tenPhim = rs.getString("ten_phim");
                String ghe = rs.getString("ghe");
                String tenKhach = rs.getString("ten_khach");
                String email = rs.getString("email");
                int giaVe = 0;


                try {
                    giaVe = rs.getInt("gia_ve");
                } catch (SQLException e) {
                    // Nếu chưa có cột gia_ve, tính giá mặc định
                    char row = ghe.charAt(0);
                    giaVe = (row == 'A' || row == 'B') ? 80000 : 50000;
                }

                Ve ve = new Ve(tenPhim, ghe, tenKhach, email, giaVe);
                list.add(ve);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Ve> getVeByPhim(String tenPhim) {
        ArrayList<Ve> list = new ArrayList<>();
        String sql = "SELECT * FROM ve WHERE ten_phim = ? ORDER BY ngay_dat DESC";

        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tenPhim);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String ghe = rs.getString("ghe");
                String tenKhach = rs.getString("ten_khach");
                String email = rs.getString("email");
                int giaVe = 0;


                try {
                    giaVe = rs.getInt("gia_ve");
                } catch (SQLException e) {
                    // Nếu chưa có cột gia_ve, tính giá mặc định
                    char row = ghe.charAt(0);
                    giaVe = (row == 'A' || row == 'B') ? 80000 : 50000;
                }

                Ve ve = new Ve(tenPhim, ghe, tenKhach, email, giaVe);
                list.add(ve);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTongDoanhThu() {
        String sql = "SELECT ve.*, CASE " +
                "WHEN ve.gia_ve > 0 THEN ve.gia_ve " +
                "WHEN ve.ghe LIKE 'A%' OR ve.ghe LIKE 'B%' THEN 80000 " +
                "ELSE 50000 END as gia_thuc " +
                "FROM ve";

        Connection conn = DatabaseConnection.getConnection();
        int tong = 0;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tong += rs.getInt("gia_thuc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tong;
    }

    public int getDoanhThuTheoPhim(String tenPhim) {
        String sql = "SELECT ve.*, CASE " +
                "WHEN ve.gia_ve > 0 THEN ve.gia_ve " +
                "WHEN ve.ghe LIKE 'A%' OR ve.ghe LIKE 'B%' THEN 80000 " +
                "ELSE 50000 END as gia_thuc " +
                "FROM ve WHERE ten_phim = ?";

        Connection conn = DatabaseConnection.getConnection();
        int tong = 0;
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tenPhim);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tong += rs.getInt("gia_thuc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tong;
    }
}
