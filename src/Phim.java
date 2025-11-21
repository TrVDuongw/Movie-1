import java.util.ArrayList;
import java.util.List;

public class Phim {
    private String tenPhim;
    private String theLoai;
    private int thoiLuong;
    private int so_hang_ghe;
    private int so_cot_ghe;
    private boolean[][] seats;

    public Phim(String tenPhim, String theLoai, int thoiLuong, int so_hang_ghe, int so_cot_ghe) {
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.thoiLuong = thoiLuong;
        this.so_hang_ghe = so_hang_ghe;
        this.so_cot_ghe = so_cot_ghe;
        seats = new boolean[so_hang_ghe][so_cot_ghe];
        for (int r = 0; r < so_hang_ghe; r++)
            for (int c = 0; c < so_cot_ghe; c++)
                seats[r][c] = true; 
    }


    public String getTenPhim() { return tenPhim; }
    public String getTheLoai() { return theLoai; }
    public int getThoiLuong() { return thoiLuong; }
    public int getSoHangGhe() { return so_hang_ghe; }
    public int getSoCotGhe() { return so_cot_ghe; }
    public boolean isSeatAvailable(int r, int c) { return seats[r][c]; }
    public boolean bookSeat(int r, int c) { if(seats[r][c]) { seats[r][c] = false; return true; } return false; }
    public String seatLabel(int r, int c) { return String.valueOf((char)('A'+r)) + (c+1); }
    public int[] labelToRC(String label) {
        if(label==null || label.length()<2) return null;
        label = label.trim().toUpperCase();
        char rowChar = label.charAt(0);
        if(rowChar<'A' || rowChar>='A'+so_hang_ghe) return null;
        try {
            int col = Integer.parseInt(label.substring(1))-1;
            int r = rowChar-'A';
            if(col<0 || col>=so_cot_ghe) return null;
            return new int[]{r, col};
        } catch(Exception e) { return null; }
    }
    public List<String> bookSeatsByLabels(List<String> labels) {
        List<String> failed = new ArrayList<>();
        for(String lab : labels) {
            int[] rc = labelToRC(lab);
            if(rc==null || !isSeatAvailable(rc[0], rc[1])) failed.add(lab);
        }
        if(!failed.isEmpty()) return failed;
        for(String lab : labels) {
            int[] rc = labelToRC(lab);
            bookSeat(rc[0], rc[1]);
        }
        return failed;
    }
    public int availableCount() {
        int cnt = 0;
        for(int r=0;r<so_hang_ghe;r++)
            for(int c=0;c<so_cot_ghe;c++)
                if(seats[r][c]) cnt++;
        return cnt;
    }
    @Override
    public String toString() {
        return tenPhim + " - " + theLoai + " (" + thoiLuong + "p) - Con: " + availableCount() + " ghe";
    }
}
