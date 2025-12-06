import java.util.ArrayList;
import java.util.List;

public class RapController {
    private PhimDAO phimDAO;
    private VeDAO VeDAO;

    public RapController() {
        phimDAO = new PhimDAO();
        VeDAO = new VeDAO();


        if (getDanhSachPhim().isEmpty()) {
            addPhim(new Phim("Avengers: Endgame", "Hanh dong", 181, 5, 8));
            addPhim(new Phim("Conan Movie 26", "Trinh tham", 110, 5, 7));
            addPhim(new Phim("Doraemon: Phieu luu", "Hoat hinh", 95, 4, 6));
        }
    }


    public int getTongDoanhThu() { return VeDAO.getTongDoanhThu(); }


    public int getDoanhThuTheoPhim(String tenPhim) { return VeDAO.getDoanhThuTheoPhim(tenPhim); }
    public ArrayList<Phim> getDanhSachPhim() {
        return phimDAO.getAllPhim();
    }


    public ArrayList<Ve> getDanhSachVe() {
        return VeDAO.getAllVe();
    }


    public void addPhim(Phim p) {
        phimDAO.addPhim(p);
    }


    public boolean removePhim(int index) {
        ArrayList<Phim> list = getDanhSachPhim();
        if (index >= 0 && index < list.size()) {
            String tenPhim = list.get(index).getTenPhim();
            return phimDAO.deletePhim(tenPhim);
        }
        return false;
    }


    public void addVe(Ve v) {
        VeDAO.addVe(v);
    }


    public List<String> bookSeats(int filmIndex, List<String> labels) {
        ArrayList<Phim> list = getDanhSachPhim();
        if (filmIndex < 0 || filmIndex >= list.size()) {
            List<String> err = new ArrayList<>();
            err.add("Phim khong hop le");
            return err;
        }

        Phim phim = list.get(filmIndex);


        List<String> failed = new ArrayList<>();
        for (String lab : labels) {
            int[] rc = phim.labelToRC(lab);
            if (rc == null || !phim.isSeatAvailable(rc[0], rc[1])) {
                failed.add(lab);
            }
        }

        if (!failed.isEmpty()) {
            return failed;
        }


        for (String lab : labels) {
            int[] rc = phim.labelToRC(lab);
            phim.bookSeat(rc[0], rc[1]);
            phimDAO.updateSeatStatus(phim.getTenPhim(), rc[0], rc[1], true);
        }

        return failed;
    }
}
