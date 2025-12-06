import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RapQuanLy extends JFrame {
    private RapController controller;
    private DefaultListModel<String> model;
    private JList<String> listPhim;
    private JTextArea areaInfo;
    private JTextField tfTen, tfLoai, tfThoiLuong, tfRows, tfCols;

    public RapQuanLy(RapController controller) {
        this.controller = controller;
        setTitle("Quan ly rap - ADMIN");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultListModel<>();
        listPhim = new JList<>(model);
        listPhim.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollList = new JScrollPane(listPhim);
        scrollList.setPreferredSize(new Dimension(350, 0));

        areaInfo = new JTextArea();
        areaInfo.setEditable(false);

        JPanel input = new JPanel(new GridLayout(6, 2, 5, 5));
        tfTen = new JTextField();
        tfLoai = new JTextField();
        tfThoiLuong = new JTextField();
        tfRows = new JTextField("5");
        tfCols = new JTextField("8");

        input.add(new JLabel("Ten phim:"));
        input.add(tfTen);
        input.add(new JLabel("The loai:"));
        input.add(tfLoai);
        input.add(new JLabel("Thoi luong (phut):"));
        input.add(tfThoiLuong);
        input.add(new JLabel("So hang ghe:"));
        input.add(tfRows);
        input.add(new JLabel("So cot ghe:"));
        input.add(tfCols);

        JButton btnAdd = new JButton("Them phim");
        JButton btnDelete = new JButton("Xoa phim");
        JButton btnViewSeats = new JButton("Xem so do ghe");
        JButton btnViewTickets = new JButton("Xem ve da dat");
        JButton btnViewRevenue = new JButton("Xem doanh thu");
        JButton btnQuayLai = new JButton("Quay lai");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnViewSeats);
        btnPanel.add(btnViewTickets);
        btnPanel.add(btnViewRevenue);
        btnPanel.add(btnQuayLai);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, new JScrollPane(areaInfo));
        split.setDividerLocation(350);

        add(input, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addFilm());
        btnDelete.addActionListener(e -> deleteFilm());
        btnViewSeats.addActionListener(e -> viewSeats());
        btnViewTickets.addActionListener(e -> viewTickets());
        btnViewRevenue.addActionListener(e -> viewRevenue());
        btnQuayLai.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ban co chac chan muon quay lai?",
                    "Xac nhan",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Main(controller).setVisible(true);
            }
        });

        loadList();
    }

    private void loadList() {
        model.clear();
        for (Phim p : controller.getDanhSachPhim())
            model.addElement(p.toString());
    }

    private void addFilm() {
        try {
            String ten = tfTen.getText().trim();
            String loai = tfLoai.getText().trim();
            int thoi = Integer.parseInt(tfThoiLuong.getText().trim());
            int rows = Integer.parseInt(tfRows.getText().trim());
            int cols = Integer.parseInt(tfCols.getText().trim());

            if (ten.isEmpty() || loai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui long nhap day du thong tin.");
                return;
            }

            if (rows <= 0 || cols <= 0 || thoi <= 0) {
                JOptionPane.showMessageDialog(this, "So hang ghe, so cot ghe va thoi luong phai lon hon 0!");
                return;
            }


            String confirmMessage = "Ban co chac chan muon them phim:\n\n" +
                    "Ten: " + ten + "\n" +
                    "The loai: " + loai + "\n" +
                    "Thoi luong: " + thoi + " phut\n" +
                    "So ghe: " + rows + " hang x " + cols + " cot";

            int confirm = JOptionPane.showConfirmDialog(this, confirmMessage,
                    "Xac nhan them phim",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            controller.addPhim(new Phim(ten, loai, thoi, rows, cols));
            JOptionPane.showMessageDialog(this, "Da them phim thanh cong!");


            tfTen.setText("");
            tfLoai.setText("");
            tfThoiLuong.setText("");
            tfRows.setText("5");
            tfCols.setText("8");

            loadList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Nhap sai dinh dang!");
        }
    }

    private void deleteFilm() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Vui long chon phim de xoa");
            return;
        }

        Phim p = controller.getDanhSachPhim().get(idx);


        String confirmMessage = "Ban co chac chan muon xoa phim:\n\n" +
                "\"" + p.getTenPhim() + "\"\n\n" +
                "CHU Y: Tat ca ve da dat va thong tin ghe cua phim nay se bi xoa!";

        int confirm = JOptionPane.showConfirmDialog(this, confirmMessage,
                "Xac nhan xoa phim",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = controller.removePhim(idx);
        if (success) {
            JOptionPane.showMessageDialog(this, "Da xoa phim thanh cong!");
            areaInfo.setText("");
            loadList();
        } else {
            JOptionPane.showMessageDialog(this, "Xoa phim that bai!");
        }
    }

    private void viewSeats() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Vui long chon phim de xem ghe.");
            return;
        }

        Phim p = controller.getDanhSachPhim().get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("SO DO GHE: ").append(p.getTenPhim()).append("\n");
        sb.append("========================================\n\n");


        int tongGhe = p.getSoHangGhe() * p.getSoCotGhe();
        int gheConLai = p.availableCount();
        int gheDaDat = tongGhe - gheConLai;

        sb.append("Tong so ghe: ").append(tongGhe).append("\n");
        sb.append("Ghe con lai: ").append(gheConLai).append("\n");
        sb.append("Ghe da dat: ").append(gheDaDat).append("\n");
        sb.append("Ti le lap day: ").append(String.format("%.1f%%", (gheDaDat * 100.0 / tongGhe))).append("\n\n");

        sb.append("[ ] = Ghe trong    [X] = Ghe da dat\n\n");


        for (int r = 0; r < p.getSoHangGhe(); r++) {
            for (int c = 0; c < p.getSoCotGhe(); c++) {
                sb.append(p.seatLabel(r, c));
                sb.append(p.isSeatAvailable(r, c) ? "[ ] " : "[X] ");
            }
            sb.append("\n");
        }

        areaInfo.setText(sb.toString());
    }

    private void viewTickets() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("DANH SACH VE DA DAT\n");
        sb.append("========================================\n\n");

        ArrayList<Ve> danhSachVe = controller.getDanhSachVe();

        if (danhSachVe.isEmpty()) {
            sb.append("Chua co ve nao duoc dat.");
        } else {
            sb.append("Tong so ve: ").append(danhSachVe.size()).append("\n\n");

            for (int i = 0; i < danhSachVe.size(); i++) {
                Ve v = danhSachVe.get(i);
                sb.append("Ve #").append(i + 1).append(":\n");
                sb.append(v.toString()).append("\n");
                sb.append("-----------------\n");
            }
        }

        areaInfo.setText(sb.toString());
    }

    private void viewRevenue() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("BAO CAO DOANH THU\n");
        sb.append("========================================\n\n");


        int tongDoanhThu = controller.getTongDoanhThu();
        sb.append("TONG DOANH THU: ").append(formatMoney(tongDoanhThu)).append("\n\n");


        sb.append("DOANH THU THEO PHIM:\n");
        sb.append("----------------------------------------\n");

        ArrayList<Phim> danhSachPhim = controller.getDanhSachPhim();

        if (danhSachPhim.isEmpty()) {
            sb.append("Chua co phim nao.\n");
        } else {
            for (Phim p : danhSachPhim) {
                int doanhThuPhim = controller.getDoanhThuTheoPhim(p.getTenPhim());
                int tongGhe = p.getSoHangGhe() * p.getSoCotGhe();
                int gheConLai = p.availableCount();
                int gheDaDat = tongGhe - gheConLai;
                double tiLe = (tongGhe > 0) ? (gheDaDat * 100.0 / tongGhe) : 0;

                sb.append("\n").append(p.getTenPhim()).append(":\n");
                sb.append("  - Doanh thu: ").append(formatMoney(doanhThuPhim)).append("\n");
                sb.append("  - So ve ban: ").append(gheDaDat).append("/").append(tongGhe).append("\n");
                sb.append("  - Ti le lap day: ").append(String.format("%.1f%%", tiLe)).append("\n");
            }
        }


        ArrayList<Ve> danhSachVe = controller.getDanhSachVe();
        sb.append("\n========================================\n");
        sb.append("THONG KE:\n");
        sb.append("----------------------------------------\n");
        sb.append("Tong so ve da ban: ").append(danhSachVe.size()).append("\n");

        if (danhSachVe.size() > 0) {
            int trungBinhGiaVe = tongDoanhThu / danhSachVe.size();
            sb.append("Gia ve trung binh: ").append(formatMoney(trungBinhGiaVe)).append("\n");
        }

        areaInfo.setText(sb.toString());
    }

    private String formatMoney(int amount) {
        return String.format("%,d VND", amount);
    }
}
