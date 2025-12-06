import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    private RapController controller;

    public Main(RapController controller) {
        this.controller = controller;
        setTitle("Ứng dụng quản lý rạp chiếu phim");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });

        JButton btnUser = new JButton("Người dùng");
        JButton btnAdmin = new JButton("Quản trị viên");

        btnUser.addActionListener(e -> {
            dispose();
            new RapChieuPhim(controller).setVisible(true);
        });

        btnAdmin.addActionListener(e -> {
            String mk = JOptionPane.showInputDialog(this, "Nhập mật khẩu admin:");
            if ("admin".equals(mk)) {
                dispose();
                new RapQuanLy(controller).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Sai mật khẩu!");
            }
        });

        JPanel panel = new JPanel();
        panel.add(btnUser);
        panel.add(btnAdmin);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(new RapController()).setVisible(true));
    }
}
