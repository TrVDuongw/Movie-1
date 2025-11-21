import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhimDAO {


    public boolean addPhim(Phim phim) {

        String sqlPhim = "INSERT INTO phim (ten_phim, the_loai, thoi_luong, so_hang_ghe, so_cot_ghe) VALUES (?, ?, ?, ?, ?)";


        String sqlGhe = "INSERT INTO ghe (phim_id, row_index, col_index, da_dat) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);


            PreparedStatement pstPhim = conn.prepareStatement(sqlPhim, Statement.RETURN_GENERATED_KEYS);
            pstPhim.setString(1, phim.getTenPhim());
            pstPhim.setString(2, phim.getTheLoai());
            pstPhim.setInt(3, phim.getThoiLuong());


            pstPhim.setInt(4, phim.getSoHangGhe()); 
            pstPhim.setInt(5, phim.getSoCotGhe()); 

            pstPhim.executeUpdate();


            ResultSet rs = pstPhim.getGeneratedKeys();
            int phimId = 0;
            if (rs.next()) {
                phimId = rs.getInt(1);
            } else {
                throw new SQLException("Không thể tạo phim, không lấy được ID.");
            }


            PreparedStatement pstGhe = conn.prepareStatement(sqlGhe);


            for (int r = 0; r < phim.getSoHangGhe(); r++) {
                for (int c = 0; c < phim.getSoCotGhe(); c++) {
                    pstGhe.setInt(1, phimId);
                    pstGhe.setInt(2, r);
                    pstGhe.setInt(3, c);
                    pstGhe.setBoolean(4, false); 
                    pstGhe.addBatch();
                }
            }
            pstGhe.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }


    public ArrayList<Phim> getAllPhim() {
        ArrayList<Phim> list = new ArrayList<>();
        String sql = "SELECT * FROM phim";

        Connection conn = DatabaseConnection.getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten_phim");
                String loai = rs.getString("the_loai");
                int thoiLuong = rs.getInt("thoi_luong");


                int soHangGhe = rs.getInt("so_hang_ghe");
                int soCotGhe = rs.getInt("so_cot_ghe");

                Phim phim = new Phim(ten, loai, thoiLuong, soHangGhe, soCotGhe);


                loadSeatsForPhim(phim, id);

                list.add(phim);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    private void loadSeatsForPhim(Phim phim, int phimId) {
        String sql = "SELECT row_index, col_index, da_dat FROM ghe WHERE phim_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, phimId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int r = rs.getInt("row_index");
                int c = rs.getInt("col_index");
                boolean daDat = rs.getBoolean("da_dat");

                if (daDat) {
                    phim.bookSeat(r, c); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
    public boolean deletePhim(String tenPhim) {
        String sql = "DELETE FROM phim WHERE ten_phim = ?";

        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tenPhim);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateSeatStatus(String tenPhim, int row, int col, boolean daDat) {
        String sql = "UPDATE ghe g INNER JOIN phim p ON g.phim_id = p.id " +
                "SET g.da_dat = ? WHERE p.ten_phim = ? AND g.row_index = ? AND g.col_index = ?";

        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setBoolean(1, daDat);
            pst.setString(2, tenPhim);
            pst.setInt(3, row);
            pst.setInt(4, col);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
