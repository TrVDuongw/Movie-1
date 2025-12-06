import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RapChieuPhim extends JFrame {
    private RapController controller;
    private DefaultListModel<String> model;
    private JList<String> listPhim;
    private JPanel seatPanel;
    private JTextArea areaThongBao;
    private List<JToggleButton> seatButtons;

    private static final int GIA_VE_THUONG = 50000;
    private static final int GIA_VE_VIP = 80000;

    public RapChieuPhim(RapController controller) {
        this.controller = controller;
        setTitle("Dat ve xem phim (User)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultListModel<>();
        listPhim = new JList<>(model);
        listPhim.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollList = new JScrollPane(listPhim);
        scrollList.setPreferredSize(new Dimension(300, 0));

        seatPanel = new JPanel();
        seatPanel.setBorder(BorderFactory.createTitledBorder("So do ghe"));
        JScrollPane seatScroll = new JScrollPane(seatPanel);

        areaThongBao = new JTextArea(4, 30);
        areaThongBao.setEditable(false);

        JButton btnLoad = new JButton("Lam moi");
        JButton btnBook = new JButton("Dat ghe");
        JButton btnQuayLai = new JButton("Quay lai");

        JPanel control = new JPanel();
        control.add(btnBook);
        control.add(btnLoad);
        control.add(btnQuayLai);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, seatScroll);
        split.setDividerLocation(300);

        add(split, BorderLayout.CENTER);
        add(control, BorderLayout.NORTH);
        add(new JScrollPane(areaThongBao), BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadFilmList());
        btnQuayLai.addActionListener(e -> {
            // Xác nhận trước khi quay lại
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ban co chac chan muon quay lai?\nCac ghe dang chon se bi huy.",
                    "Xac nhan",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Main(controller).setVisible(true);
            }
        });
        btnBook.addActionListener(e -> handleBooking());
        listPhim.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSeatLayout();
        });

        loadFilmList();
    }

    private void loadFilmList() {
        model.clear();
        for (Phim p : controller.getDanhSachPhim()) model.addElement(p.toString());
        areaThongBao.setText("Danh sach phim da duoc cap nhat.\n" +
                "Ghe VIP (hang A, B): " + formatMoney(GIA_VE_VIP) + "\n" +
                "Ghe thuong: " + formatMoney(GIA_VE_THUONG));
        seatPanel.removeAll();
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void showSeatLayout() {
        seatPanel.removeAll();
        seatButtons = new ArrayList<>();
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            seatPanel.add(new JLabel("Ban chua chon phim."));
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        Phim p = controller.getDanhSachPhim().get(idx);
        int so_hang_ghe = p.getSoHangGhe();
        int so_cot_ghe = p.getSoCotGhe();

        if (p.availableCount() == 0) {
            seatPanel.setLayout(new BorderLayout());
            seatPanel.add(new JLabel("Phim nay da het ve. Vui long chon phim khac."), BorderLayout.CENTER);
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        JPanel mainSeatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = so_cot_ghe;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel screenLabel = new JLabel("=== MÀN HÌNH ===", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Arial", Font.BOLD, 14));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.DARK_GRAY);
        screenLabel.setForeground(Color.WHITE);
        mainSeatPanel.add(screenLabel, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;

        for (int r = 0; r < so_hang_ghe; r++) {
            for (int c = 0; c < so_cot_ghe; c++) {
                String label = p.seatLabel(r, c);
                boolean available = p.isSeatAvailable(r, c);

                if (available) {
                    JToggleButton btn = new JToggleButton(label);
                    btn.setPreferredSize(new Dimension(60, 40));

                    char row = label.charAt(0);
                    if (row == 'A' || row == 'B') {
                        btn.setBackground(new Color(255, 215, 0));
                        btn.setToolTipText("Ghe VIP - " + formatMoney(GIA_VE_VIP));
                    } else {
                        btn.setBackground(Color.WHITE);
                        btn.setToolTipText("Ghe thuong - " + formatMoney(GIA_VE_THUONG));
                    }

                    btn.setFont(new Font("Arial", Font.BOLD, 10));
                    seatButtons.add(btn);

                    gbc.gridx = c;
                    gbc.gridy = r + 1;
                    mainSeatPanel.add(btn, gbc);
                } else {
                    JLabel lblBooked = new JLabel("X", SwingConstants.CENTER);
                    lblBooked.setPreferredSize(new Dimension(60, 40));
                    lblBooked.setOpaque(true);
                    lblBooked.setBackground(Color.LIGHT_GRAY);
                    lblBooked.setForeground(Color.RED);
                    lblBooked.setFont(new Font("Arial", Font.BOLD, 14));
                    lblBooked.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    lblBooked.setToolTipText("Ghe da duoc dat");

                    gbc.gridx = c;
                    gbc.gridy = r + 1;
                    mainSeatPanel.add(lblBooked, gbc);
                }
            }
        }

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Chu thich"));

        JLabel vipLegend = new JLabel("■ Ghe VIP (" + formatMoney(GIA_VE_VIP) + ")");
        vipLegend.setForeground(new Color(255, 215, 0));
        vipLegend.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel normalLegend = new JLabel("■ Ghe thuong (" + formatMoney(GIA_VE_THUONG) + ")");
        normalLegend.setForeground(Color.BLACK);

        JLabel bookedLegend = new JLabel("X Da dat");
        bookedLegend.setForeground(Color.RED);
        bookedLegend.setFont(new Font("Arial", Font.BOLD, 12));

        legendPanel.add(vipLegend);
        legendPanel.add(Box.createHorizontalStrut(20));
        legendPanel.add(normalLegend);
        legendPanel.add(Box.createHorizontalStrut(20));
        legendPanel.add(bookedLegend);

        seatPanel.setLayout(new BorderLayout());
        seatPanel.add(mainSeatPanel, BorderLayout.CENTER);
        seatPanel.add(legendPanel, BorderLayout.SOUTH);

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void handleBooking() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            areaThongBao.setText("Vui long chon phim truoc.");
            return;
        }

        Phim p = controller.getDanhSachPhim().get(idx);
        List<String> toBook = new ArrayList<>();
        for (JToggleButton btn : seatButtons) {
            if (btn.isSelected() && btn.isEnabled()) {
                toBook.add(btn.getText());
            }
        }

        if (toBook.isEmpty()) {
            areaThongBao.setText("Vui long chon it nhat 1 ghe.");
            return;
        }

        int tongTien = 0;
        for (String ghe : toBook) {
            char row = ghe.charAt(0);
            if (row == 'A' || row == 'B') {
                tongTien += GIA_VE_VIP;
            } else {
                tongTien += GIA_VE_THUONG;
            }
        }

        String thongTinDat = "Ban dang dat:\n" +
                "Phim: " + p.getTenPhim() + "\n" +
                "So ghe: " + toBook.size() + " (" + String.join(", ", toBook) + ")\n" +
                "Tong tien: " + formatMoney(tongTien) + "\n\n" +
                "Ban co chac chan muon dat ve nay?";

        int confirm = JOptionPane.showConfirmDialog(this, thongTinDat,
                "Xac nhan dat ve",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<String> failed = controller.bookSeats(idx, toBook);
        if (!failed.isEmpty()) {
            areaThongBao.setText("Cac ghe sau da duoc dat: " + failed);
            showSeatLayout();
            return;
        }

        String tenKhach = JOptionPane.showInputDialog(this, "Nhap ten khach hang:");
        if (tenKhach == null || tenKhach.trim().isEmpty()) {
            tenKhach = "Khach an danh";
        }

        String email = JOptionPane.showInputDialog(this, "Nhap email (co the bo qua):");

        boolean thanhToanThanhCong = xuLyThanhToan(tongTien);

        if (thanhToanThanhCong) {
            // Lưu vé với giá tương ứng
            for (String ghe : toBook) {
                char row = ghe.charAt(0);
                int giaVe = (row == 'A' || row == 'B') ? GIA_VE_VIP : GIA_VE_THUONG;
                Ve ve = new Ve(p.getTenPhim(), ghe, tenKhach, email, giaVe);
                controller.addVe(ve);
            }

            String thongTinVe = "=== DAT VE THANH CONG ===\n\n" +
                    "Phim: " + p.getTenPhim() + "\n" +
                    "Khach hang: " + tenKhach + "\n" +
                    (email != null && !email.isEmpty() ? "Email: " + email + "\n" : "") +
                    "Cac ghe: " + String.join(", ", toBook) + "\n" +
                    "Tong tien: " + formatMoney(tongTien) + "\n\n" +
                    "Cam on quy khach!";

            JOptionPane.showMessageDialog(this, thongTinVe,
                    "Thong tin ve",
                    JOptionPane.INFORMATION_MESSAGE);

            areaThongBao.setText("Da dat " + toBook.size() + " ghe thanh cong!\n" +
                    "Tong tien: " + formatMoney(tongTien));

            showSeatLayout();
            loadFilmList();
        } else {
            areaThongBao.setText("Thanh toan that bai. Vui long thu lai.");
        }
    }

    private boolean xuLyThanhToan(int tongTien) {
        JPanel paymentPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTongTien = new JLabel("Tong tien:");
        JLabel lblSoTien = new JLabel(formatMoney(tongTien));
        lblSoTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblSoTien.setForeground(Color.RED);

        JLabel lblPhuongThuc = new JLabel("Phuong thuc:");
        String[] phuongThuc = {"Tien mat", "The ngan hang", "Vi dien tu"};
        JComboBox<String> cbPhuongThuc = new JComboBox<>(phuongThuc);

        paymentPanel.add(lblTongTien);
        paymentPanel.add(lblSoTien);
        paymentPanel.add(lblPhuongThuc);
        paymentPanel.add(cbPhuongThuc);

        int result = JOptionPane.showConfirmDialog(this, paymentPanel,
                "Thanh toan",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String method = (String) cbPhuongThuc.getSelectedItem();

            if ("Tien mat".equals(method)) {
                return xuLyThanhToanTienMat(tongTien);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Dang xu ly thanh toan qua " + method + "...\nVui long doi...",
                        "Xu ly thanh toan",
                        JOptionPane.INFORMATION_MESSAGE);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                JOptionPane.showMessageDialog(this,
                        "Thanh toan thanh cong!",
                        "Ket qua",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }

        return false;
    }

    private boolean xuLyThanhToanTienMat(int tongTien) {
        String input = JOptionPane.showInputDialog(this,
                "Tong tien: " + formatMoney(tongTien) + "\nNhap so tien khach dua:");

        if (input == null) return false;

        try {
            int tienKhachDua = Integer.parseInt(input.trim());

            if (tienKhachDua < tongTien) {
                JOptionPane.showMessageDialog(this,
                        "So tien khong du!\nThieu: " + formatMoney(tongTien - tienKhachDua),
                        "Loi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int tienThua = tienKhachDua - tongTien;
            String message = "Thanh toan thanh cong!\n\n" +
                    "Tien khach dua: " + formatMoney(tienKhachDua) + "\n" +
                    "Tong tien: " + formatMoney(tongTien) + "\n" +
                    "Tien thua: " + formatMoney(tienThua);

            JOptionPane.showMessageDialog(this, message,
                    "Hoa don",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Vui long nhap so hop le!",
                    "Loi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String formatMoney(int amount) {
        return String.format("%,d VND", amount);
    }
}
