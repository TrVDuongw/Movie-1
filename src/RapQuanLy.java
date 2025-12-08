import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class RapQuanLy extends JFrame {
    private RapController controller;
    private DefaultListModel<String> model;
    private JList<String> listPhim;
    private JTextArea areaInfo;
    private JTextField tfTen, tfLoai, tfThoiLuong, tfRows, tfCols;

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color INFO_COLOR = new Color(142, 68, 173);
    private static final Color BG_COLOR = new Color(236, 240, 241);

    public RapQuanLy(RapController controller) {
        this.controller = controller;
        setTitle("⚙️ Quản lý rạp chiếu phim - ADMIN");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);

        initComponents();
        loadList();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("⚙️ HỆ THỐNG QUẢN LÝ RẠP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Administrator Control Panel", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setBackground(PRIMARY_COLOR);
        headerContent.add(titleLabel, BorderLayout.CENTER);
        headerContent.add(subtitleLabel, BorderLayout.SOUTH);
        headerPanel.add(headerContent, BorderLayout.CENTER);


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                createTitledBorder("➕ THÊM PHIM MỚI")
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        tfTen = createStyledTextField();
        tfLoai = createStyledTextField();
        tfThoiLuong = createStyledTextField();
        tfRows = createStyledTextField();
        tfRows.setText("5");
        tfCols = createStyledTextField();
        tfCols.setText("8");

        addFormRow(inputPanel, gbc, 0, "🎬 Tên phim:", tfTen);
        addFormRow(inputPanel, gbc, 1, "🎭 Thể loại:", tfLoai);
        addFormRow(inputPanel, gbc, 2, "⏱️ Thời lượng (phút):", tfThoiLuong);
        addFormRow(inputPanel, gbc, 3, "📊 Số hàng ghế:", tfRows);
        addFormRow(inputPanel, gbc, 4, "📊 Số cột ghế:", tfCols);


        model = new DefaultListModel<>();
        listPhim = new JList<>(model);
        listPhim.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPhim.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listPhim.setFixedCellHeight(45);
        listPhim.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollList = new JScrollPane(listPhim);
        scrollList.setPreferredSize(new Dimension(400, 0));
        scrollList.setBorder(createTitledBorder("📋 DANH SÁCH PHIM"));


        areaInfo = new JTextArea();
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaInfo.setBackground(new Color(253, 254, 254));
        areaInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollInfo = new JScrollPane(areaInfo);
        scrollInfo.setBorder(createTitledBorder("ℹ️ THÔNG TIN CHI TIẾT"));


        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAdd = createStyledButton("➕ Thêm phim", SUCCESS_COLOR, "🎬");
        JButton btnDelete = createStyledButton("🗑️ Xóa phim", ACCENT_COLOR, "❌");
        JButton btnViewSeats = createStyledButton("🪑 Xem sơ đồ ghế", PRIMARY_COLOR, "📊");
        JButton btnViewTickets = createStyledButton("🎫 Danh sách vé", INFO_COLOR, "📋");
        JButton btnViewRevenue = createStyledButton("💰 Doanh thu", WARNING_COLOR, "📈");
        JButton btnQuayLai = createStyledButton("← Quay lại", new Color(52, 73, 94), "🚪");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnViewSeats);
        buttonPanel.add(btnViewTickets);
        buttonPanel.add(btnViewRevenue);
        buttonPanel.add(btnQuayLai);


        JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, scrollInfo);
        splitMain.setDividerLocation(400);
        splitMain.setBorder(null);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(splitMain, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        btnAdd.addActionListener(e -> addFilm());
        btnDelete.addActionListener(e -> deleteFilm());
        btnViewSeats.addActionListener(e -> viewSeats());
        btnViewTickets.addActionListener(e -> viewTickets());
        btnViewRevenue.addActionListener(e -> viewRevenue());
        btnQuayLai.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn quay lại?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Main(controller).setVisible(true);
            }
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(52, 73, 94)
        );
    }

    private JButton createStyledButton(String text, Color bgColor, String icon) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadList() {
        model.clear();
        for (Phim p : controller.getDanhSachPhim()) {
            model.addElement("🎬 " + p.toString());
        }
    }

    private void addFilm() {
        try {
            String ten = tfTen.getText().trim();
            String loai = tfLoai.getText().trim();
            int thoi = Integer.parseInt(tfThoiLuong.getText().trim());
            int rows = Integer.parseInt(tfRows.getText().trim());
            int cols = Integer.parseInt(tfCols.getText().trim());

            if (ten.isEmpty() || loai.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Vui lòng nhập đầy đủ thông tin.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (rows <= 0 || cols <= 0 || thoi <= 0) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Số hàng ghế, số cột ghế và thời lượng phải lớn hơn 0!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String confirmMessage = "✅ XÁC NHẬN THÊM PHIM\n\n" +
                    "🎬 Tên: " + ten + "\n" +
                    "🎭 Thể loại: " + loai + "\n" +
                    "⏱️ Thời lượng: " + thoi + " phút\n" +
                    "🪑 Số ghế: " + rows + " hàng × " + cols + " cột\n" +
                    "📊 Tổng: " + (rows * cols) + " ghế";

            int confirm = JOptionPane.showConfirmDialog(this, confirmMessage,
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.addPhim(new Phim(ten, loai, thoi, rows, cols));
                JOptionPane.showMessageDialog(this,
                        "✅ Đã thêm phim thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                tfTen.setText("");
                tfLoai.setText("");
                tfThoiLuong.setText("");
                tfRows.setText("5");
                tfCols.setText("8");
                loadList();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Nhập sai định dạng!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFilm() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Vui lòng chọn phim để xóa",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Phim p = controller.getDanhSachPhim().get(idx);
        String confirmMessage = "⚠️ XÁC NHẬN XÓA PHIM\n\n" +
                "🎬 \"" + p.getTenPhim() + "\"\n\n" +
                "❌ CHÚ Ý: Tất cả vé đã đặt và thông tin ghế\ncủa phim này sẽ bị xóa!";

        int confirm = JOptionPane.showConfirmDialog(this, confirmMessage,
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.removePhim(idx);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "✅ Đã xóa phim thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                areaInfo.setText("");
                loadList();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Xóa phim thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSeats() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Vui lòng chọn phim để xem ghế.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Phim p = controller.getDanhSachPhim().get(idx);
        StringBuilder sb = new StringBuilder();

        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║        🪑 SƠ ĐỒ GHẾ NGỒI              ║\n");
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append("║  🎬 Phim: ").append(p.getTenPhim()).append("\n");
        sb.append("╚════════════════════════════════════════╝\n\n");

        int tongGhe = p.getSoHangGhe() * p.getSoCotGhe();
        int gheConLai = p.availableCount();
        int gheDaDat = tongGhe - gheConLai;

        sb.append("📊 THỐNG KÊ:\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("  📍 Tổng số ghế:  ").append(tongGhe).append("\n");
        sb.append("  ✅ Ghế còn lại:  ").append(gheConLai).append("\n");
        sb.append("  ❌ Ghế đã đặt:   ").append(gheDaDat).append("\n");
        sb.append("  📈 Tỷ lệ lấp đầy: ").append(String.format("%.1f%%", (gheDaDat * 100.0 / tongGhe))).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("🎯 CHÚ THÍCH:\n");
        sb.append("  ✅ [  ] = Ghế trống\n");
        sb.append("  ❌ [ X ] = Ghế đã đặt\n\n");

        sb.append("═══════════════════════════════════\n");
        sb.append("          🎬 MÀN HÌNH 🎬\n");
        sb.append("═══════════════════════════════════\n\n");

        for (int r = 0; r < p.getSoHangGhe(); r++) {
            sb.append("  ");
            for (int c = 0; c < p.getSoCotGhe(); c++) {
                sb.append(p.seatLabel(r, c));
                sb.append(p.isSeatAvailable(r, c) ? "[  ] " : "[ X ] ");
            }
            sb.append("\n");
        }

        areaInfo.setText(sb.toString());
    }

    private void viewTickets() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║       🎫 DANH SÁCH VÉ ĐÃ ĐẶT         ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");

        ArrayList<Ve> danhSachVe = controller.getDanhSachVe();

        if (danhSachVe.isEmpty()) {
            sb.append("  ℹ️ Chưa có vé nào được đặt.\n");
        } else {
            sb.append("📊 Tổng số vé: ").append(danhSachVe.size()).append("\n\n");

            for (int i = 0; i < danhSachVe.size(); i++) {
                Ve v = danhSachVe.get(i);
                sb.append("┌─────────────────────────────────┐\n");
                sb.append("│  🎫 Vé #").append(i + 1).append("\n");
                sb.append("├─────────────────────────────────┤\n");
                sb.append("│  ").append(v.toString().replace("\n", "\n│  ")).append("\n");
                sb.append("└─────────────────────────────────┘\n\n");
            }
        }

        areaInfo.setText(sb.toString());
    }

    private void viewRevenue() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║       💰 BÁO CÁO DOANH THU            ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");

        int tongDoanhThu = controller.getTongDoanhThu();
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  💵 TỔNG DOANH THU                 ┃\n");
        sb.append("┃  ").append(formatMoney(tongDoanhThu)).append("\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

        sb.append("📊 DOANH THU THEO PHIM:\n");
        sb.append("═══════════════════════════════════════\n\n");

        ArrayList<Phim> danhSachPhim = controller.getDanhSachPhim();

        if (danhSachPhim.isEmpty()) {
            sb.append("  ℹ️ Chưa có phim nào.\n");
        } else {
            for (Phim p : danhSachPhim) {
                int doanhThuPhim = controller.getDoanhThuTheoPhim(p.getTenPhim());
                int tongGhe = p.getSoHangGhe() * p.getSoCotGhe();
                int gheConLai = p.availableCount();
                int gheDaDat = tongGhe - gheConLai;
                double tiLe = (tongGhe > 0) ? (gheDaDat * 100.0 / tongGhe) : 0;

                sb.append("┌────────────────────────────────┐\n");
                sb.append("│ 🎬 ").append(p.getTenPhim()).append("\n");
                sb.append("├────────────────────────────────┤\n");
                sb.append("│  💰 Doanh thu:    ").append(formatMoney(doanhThuPhim)).append("\n");
                sb.append("│  🎫 Số vé bán:    ").append(gheDaDat).append("/").append(tongGhe).append("\n");
                sb.append("│  📈 Tỷ lệ lấp:    ").append(String.format("%.1f%%", tiLe)).append("\n");
                sb.append("└────────────────────────────────┘\n\n");
            }
        }

        ArrayList<Ve> danhSachVe = controller.getDanhSachVe();
        sb.append("═══════════════════════════════════════\n");
        sb.append("📊 THỐNG KÊ TỔNG QUAN:\n");
        sb.append("───────────────────────────────────────\n");
        sb.append("  🎫 Tổng số vé đã bán: ").append(danhSachVe.size()).append("\n");

        if (danhSachVe.size() > 0) {
            int trungBinhGiaVe = tongDoanhThu / danhSachVe.size();
            sb.append("  💵 Giá vé trung bình: ").append(formatMoney(trungBinhGiaVe)).append("\n");
        }
        sb.append("───────────────────────────────────────\n");

        areaInfo.setText(sb.toString());
    }

    private String formatMoney(int amount) {
        return String.format("%,d VNĐ", amount);
    }
}
