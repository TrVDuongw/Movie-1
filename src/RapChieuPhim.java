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
    private static final int GIA_VE_DOI = 100000;

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
                "Ghe thuong: " + formatMoney(GIA_VE_THUONG) + "\n" +
                "Ghe doi (hang cuoi - chon theo cap): " + formatMoney(GIA_VE_DOI));
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
        JLabel screenLabel = new JLabel("=== MAN HINH ===", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Arial", Font.BOLD, 14));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.DARK_GRAY);
        screenLabel.setForeground(Color.WHITE);
        mainSeatPanel.add(screenLabel, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;

        for (int r = 0; r < so_hang_ghe; r++) {
            boolean isLastRow = (r == so_hang_ghe - 1);

            for (int c = 0; c < so_cot_ghe; c++) {
                String label = p.seatLabel(r, c);
                boolean available = p.isSeatAvailable(r, c);

            
                if (isLastRow && c % 2 == 0 && c < so_cot_ghe - 1) {
                    String label1 = p.seatLabel(r, c);
                    String label2 = p.seatLabel(r, c + 1);
                    boolean available1 = p.isSeatAvailable(r, c);
                    boolean available2 = p.isSeatAvailable(r, c + 1);

                    if (available1 && available2) {
                       
                        JToggleButton btnDouble = new JToggleButton(label1 + "-" + label2);
                        btnDouble.setPreferredSize(new Dimension(125, 40));
                        btnDouble.setBackground(new Color(255, 182, 193));
                        btnDouble.setToolTipText("Ghe doi - " + formatMoney(GIA_VE_DOI));
                        btnDouble.setFont(new Font("Arial", Font.BOLD, 10));
                        btnDouble.setOpaque(true);
                        btnDouble.setBorderPainted(true);

                       
                        btnDouble.putClientProperty("seat1", label1);
                        btnDouble.putClientProperty("seat2", label2);
                        btnDouble.putClientProperty("isDouble", true);

                        seatButtons.add(btnDouble);

                        gbc.gridx = c;
                        gbc.gridy = r + 1;
                        gbc.gridwidth = 2;
                        mainSeatPanel.add(btnDouble, gbc);
                        gbc.gridwidth = 1;

                        c++; 
                    } else {
                       
                        for (int i = 0; i < 2 && c + i < so_cot_ghe; i++) {
                            String lbl = p.seatLabel(r, c + i);
                            boolean avail = p.isSeatAvailable(r, c + i);

                            if (!avail) {
                                JLabel lblBooked = new JLabel("X", SwingConstants.CENTER);
                                lblBooked.setPreferredSize(new Dimension(60, 40));
                                lblBooked.setOpaque(true);
                                lblBooked.setBackground(Color.LIGHT_GRAY);
                                lblBooked.setForeground(Color.RED);
                                lblBooked.setFont(new Font("Arial", Font.BOLD, 14));
                                lblBooked.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                                lblBooked.setToolTipText("Ghe da duoc dat");

                                gbc.gridx = c + i;
                                gbc.gridy = r + 1;
                                mainSeatPanel.add(lblBooked, gbc);
                            }
                        }
                        if (available1 || available2) {
                            c++; 
                        }
                    }
                    continue;
                }

              
                if (isLastRow && c % 2 == 1) {
                    continue;
                }

             
                if (available) {
                    JToggleButton btn = new JToggleButton(label);
                    btn.setPreferredSize(new Dimension(60, 40));

                    char row = label.charAt(0);

                   
                    if (row == 'A' || row == 'B') {
                        btn.setBackground(new Color(255, 215, 0));
                        btn.setToolTipText("Ghe VIP - " + formatMoney(GIA_VE_VIP));
                    }
                   
                    else {
                        btn.setBackground(new Color(173, 216, 230));
                        btn.setToolTipText("Ghe thuong - " + formatMoney(GIA_VE_THUONG));
                    }

                    btn.setFont(new Font("Arial", Font.BOLD, 10));
                    btn.setOpaque(true);
                    btn.setBorderPainted(true);
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

        JLabel vipLegend = new JLabel("  Ghe VIP (" + formatMoney(GIA_VE_VIP) + ")  ");
        vipLegend.setFont(new Font("Arial", Font.BOLD, 12));
        vipLegend.setOpaque(true);
        vipLegend.setBackground(new Color(255, 215, 0));
        vipLegend.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel normalLegend = new JLabel("  Ghe thuong (" + formatMoney(GIA_VE_THUONG) + ")  ");
        normalLegend.setOpaque(true);
        normalLegend.setBackground(new Color(173, 216, 230));
        normalLegend.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel doubleLegend = new JLabel("  Ghe doi (" + formatMoney(GIA_VE_DOI) + ")  ");
        doubleLegend.setFont(new Font("Arial", Font.BOLD, 12));
        doubleLegend.setOpaque(true);
        doubleLegend.setBackground(new Color(255, 182, 193));
        doubleLegend.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel bookedLegend = new JLabel("X Da dat");
        bookedLegend.setForeground(Color.RED);
        bookedLegend.setFont(new Font("Arial", Font.BOLD, 12));

        legendPanel.add(vipLegend);
        legendPanel.add(Box.createHorizontalStrut(20));
        legendPanel.add(normalLegend);
        legendPanel.add(Box.createHorizontalStrut(20));
        legendPanel.add(doubleLegend);
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
        int so_hang_ghe = p.getSoHangGhe();
        List<String> toBook = new ArrayList<>();

        for (JToggleButton btn : seatButtons) {
            if (btn.isSelected() && btn.isEnabled()) {
               
                Boolean isDouble = (Boolean) btn.getClientProperty("isDouble");
                if (isDouble != null && isDouble) {
                
                    String seat1 = (String) btn.getClientProperty("seat1");
                    String seat2 = (String) btn.getClientProperty("seat2");
                    toBook.add(seat1);
                    toBook.add(seat2);
                } else {
              
                    toBook.add(btn.getText());
                }
            }
        }

        if (toBook.isEmpty()) {
            areaThongBao.setText("Vui long chon it nhat 1 ghe.");
            return;
        }

        int tongTien = 0;
        int soGheDoi = 0;
        int soGheVIP = 0;
        int soGheThuong = 0;

        for (String ghe : toBook) {
            char row = ghe.charAt(0);
            int rowIndex = row - 'A';

          
            if (rowIndex == so_hang_ghe - 1) {
                tongTien += GIA_VE_DOI / 2; // Mỗi ghế trong cặp đôi
                soGheDoi++;
            }
        
            else if (row == 'A' || row == 'B') {
                tongTien += GIA_VE_VIP;
                soGheVIP++;
            }
        
            else {
                tongTien += GIA_VE_THUONG;
                soGheThuong++;
            }
        }

        StringBuilder chiTiet = new StringBuilder();
        if (soGheVIP > 0) chiTiet.append(soGheVIP).append(" ghe VIP, ");
        if (soGheThuong > 0) chiTiet.append(soGheThuong).append(" ghe thuong, ");
        if (soGheDoi > 0) chiTiet.append(soGheDoi / 2).append(" cap ghe doi, ");
        if (chiTiet.length() > 0) chiTiet.setLength(chiTiet.length() - 2);

        String thongTinDat = "Ban dang dat:\n" +
                "Phim: " + p.getTenPhim() + "\n" +
                "Chi tiet: " + chiTiet.toString() + "\n" +
                "Cac ghe: " + String.join(", ", toBook) + "\n" +
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
            
            for (String ghe : toBook) {
                char row = ghe.charAt(0);
                int rowIndex = row - 'A';
                int giaVe;

             
                if (rowIndex == so_hang_ghe - 1) {
                    giaVe = GIA_VE_DOI / 2;
                }
              
                else if (row == 'A' || row == 'B') {
                    giaVe = GIA_VE_VIP;
                }
               
                else {
                    giaVe = GIA_VE_THUONG;
                }

                Ve ve = new Ve(p.getTenPhim(), ghe, tenKhach, email, giaVe);
                controller.addVe(ve);
            }

            String thongTinVe = "=== DAT VE THANH CONG ===\n\n" +
                    "Phim: " + p.getTenPhim() + "\n" +
                    "Khach hang: " + tenKhach + "\n" +
                    (email != null && !email.isEmpty() ? "Email: " + email + "\n" : "") +
                    "Chi tiet: " + chiTiet.toString() + "\n" +
                    "Cac ghe: " + String.join(", ", toBook) + "\n" +
                    "Tong tien: " + formatMoney(tongTien) + "\n\n" +
                    "Cam on quy khach!";

            JOptionPane.showMessageDialog(this, thongTinVe,
                    "Thong tin ve",
                    JOptionPane.INFORMATION_MESSAGE);

            areaThongBao.setText("Da dat " + toBook.size() + " ghe thanh cong!\n" +
                    "Chi tiet: " + chiTiet.toString() + "\n" +
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
