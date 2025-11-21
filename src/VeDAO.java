import java.sql.*;
import java.util.ArrayList;

public class VeDAO {

    public boolean addVe(Ve ve) {
        String sql = "INSERT INTO ve (ten_phim, ghe, ten_khach, email) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, ve.getTenPhim());
            pst.setString(2, ve.getGhe());
            pst.setString(3, ve.getTenKhach());
            pst.setString(4, ve.getEmail());

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

                Ve ve = new Ve(tenPhim, ghe, tenKhach, email);
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

                Ve ve = new Ve(tenPhim, ghe, tenKhach, email);
                list.add(ve);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
