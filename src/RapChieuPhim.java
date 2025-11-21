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
            dispose();
            new Main(controller).setVisible(true);
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
        areaThongBao.setText("Danh sach phim da duoc cap nhat.");
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
        seatPanel.setLayout(new GridLayout(so_hang_ghe, so_cot_ghe, 5, 5));

        if (p.availableCount() == 0) {
            seatPanel.setLayout(new BorderLayout());
            seatPanel.add(new JLabel("Phim nay da het ve.Vui long chon phim khac."), BorderLayout.CENTER);
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        for (int r = 0; r < so_hang_ghe; r++) {
            for (int c = 0; c < so_cot_ghe; c++) {
                String label = p.seatLabel(r, c);
                boolean available = p.isSeatAvailable(r, c);
                JToggleButton btn = new JToggleButton(label);
                btn.setEnabled(available);
                btn.setBackground(available ? Color.WHITE : Color.LIGHT_GRAY);
                seatButtons.add(btn);
                seatPanel.add(btn);
            }
        }
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
            if (btn.isSelected() && btn.isEnabled()) toBook.add(btn.getText());
        }

        if (toBook.isEmpty()) {
            areaThongBao.setText("Vui long chon it nhat 1 ghe.");
            return;
        }

        List<String> failed = controller.bookSeats(idx, toBook);
        if (!failed.isEmpty()) {
            areaThongBao.setText("Ghe da duoc dat: " + failed);
            return;
        }

        String tenKhach = JOptionPane.showInputDialog(this, "Nhap ten khach:");
        String email = JOptionPane.showInputDialog(this, "Nhap email (tuy chon):");
        if (tenKhach == null || tenKhach.trim().isEmpty()) tenKhach = "Khach an danh";

        for (String ghe : toBook) {
            Ve ve = new Ve(p.getTenPhim(), ghe, tenKhach, email);
            controller.addVe(ve);
            JOptionPane.showMessageDialog(this, "Dat ve thanh cong!\n" + ve.toString());
        }

        showSeatLayout();
        loadFilmList();
    }
}