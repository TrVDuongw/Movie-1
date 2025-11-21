import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;

public class RapQuanLy extends JFrame {
    private RapController controller;
    private DefaultListModel<String> model;
    private JList<String> listPhim;
    private JTextArea areaInfo;
    private JTextField tfTen, tfLoai, tfThoiLuong, tfRows, tfCols;
    public RapQuanLy(RapController controller) {
        this.controller = controller;
        setTitle("Quan ly rap - ADMIN");
        setSize(900, 600);
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
        tfTen = new JTextField(); tfLoai = new JTextField();
        tfThoiLuong = new JTextField(); tfRows = new JTextField("5"); tfCols = new JTextField("8");
        input.add(new JLabel("Ten phim:")); input.add(tfTen);
        input.add(new JLabel("The loai:")); input.add(tfLoai);
        input.add(new JLabel("Thoi luong (phut):")); input.add(tfThoiLuong);
        input.add(new JLabel("So hang ghe:")); input.add(tfRows);
        input.add(new JLabel("Số  cot ghe:")); input.add(tfCols);
        JButton btnAdd = new JButton("Them phim");
        JButton btnDelete = new JButton("Xoa phim");
        JButton btnViewSeats = new JButton("Xem so do ghe");
        JButton btnViewTickets = new JButton("Xem ve da dat");
        JButton btnQuayLai = new JButton("Quay lai");
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd); btnPanel.add(btnDelete);
        btnPanel.add(btnViewSeats); btnPanel.add(btnViewTickets); btnPanel.add(btnQuayLai);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, new JScrollPane(areaInfo));
        split.setDividerLocation(350);
        add(input, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        btnAdd.addActionListener(e -> addFilm());
        btnDelete.addActionListener(e -> deleteFilm());
        btnViewSeats.addActionListener(e -> viewSeats());
        btnViewTickets.addActionListener(e -> viewTickets());
        btnQuayLai.addActionListener(e -> {
            dispose();
            new Main().notifyAll();
        });
        loadList();
    }
    private void loadList() {
        model.clear();
        for (Phim p : controller.getDanhSachPhim()) model.addElement(p.toString());
    }
    private void addFilm() {
        try {
            String ten = tfTen.getText().trim();
            String loai = tfLoai.getText().trim();
            int thoi = Integer.parseInt(tfThoiLuong.getText().trim());
            int rows = Integer.parseInt(tfRows.getText().trim());
            int cols = Integer.parseInt(tfCols.getText().trim());
            if (ten.isEmpty() || loai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui long nhap day du thông tin.");
                return;
            }
            controller.addPhim(new Phim(ten, loai, thoi, rows, cols));
            JOptionPane.showMessageDialog(this, "Da them phim thanh cong!");
            loadList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Nhhap sai dinh dang!");
        }
    }
    private void deleteFilm() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Chon phim de xoa");
            return;
        }
        controller.removePhim(idx);
        JOptionPane.showMessageDialog(this, "Da xoa phim thanh cong!");
        loadList();
    }
    private void viewSeats() {
        int idx = listPhim.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Vui long chon phim de xem ghe.");
            return;
        }
        Phim p = controller.getDanhSachPhim().get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("So do ghe ").append(p.getTenPhim()).append("\n");
        for (int r = 0; r < p.getSoHangGhe(); r++) {
            for (int c = 0; c < p.getSoCotGhe(); c++) {
                sb.append(p.seatLabel(r, c)).append(p.isSeatAvailable(r, c) ? "[ ] " : "[X] ");
            }
            sb.append("\n");
        }
        areaInfo.setText(sb.toString());
    }
    private void viewTickets() {
        StringBuilder sb = new StringBuilder("Danh sach ve da dat:\n\n");
        for (Ve v : controller.getDanhSachVe()) sb.append(v.toString()).append("\n-----------------\n");
        if (controller.getDanhSachVe().isEmpty()) sb.append("Chua co ve nao duoc dat.");
        areaInfo.setText(sb.toString());
    }
}