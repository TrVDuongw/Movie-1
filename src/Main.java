import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    private RapController controller;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public Main(RapController controller) {
        this.controller = controller;
        setTitle("Hệ thống quản lý rạp chiếu phim");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));


        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(" CINEMA MANAGER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel subtitleLabel = new JLabel("Hệ thống quản lý rạp chiếu phim ", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setBackground(PRIMARY_COLOR);
        headerContent.add(titleLabel, BorderLayout.CENTER);
        headerContent.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(headerContent, BorderLayout.CENTER);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;


        JButton btnUser = createStyledButton("NGƯỜI DÙNG", "Đặt vé xem phim", SECONDARY_COLOR);
        btnUser.addActionListener(e -> {
            dispose();
            new RapChieuPhim(controller).setVisible(true);
        });


        JButton btnAdmin = createStyledButton("QUẢN TRỊ VIÊN", "Quản lý hệ thống", ACCENT_COLOR);
        btnAdmin.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel label = new JLabel("Nhập mật khẩu quản trị:");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            panel.add(label);
            panel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Xác thực quản trị viên",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String mk = new String(passwordField.getPassword());
                if ("admin".equals(mk)) {
                    dispose();
                    new RapQuanLy(controller).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Mật khẩu không chính xác!",
                            "Lỗi xác thực",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        centerPanel.add(btnUser, gbc);
        gbc.gridy = 1;
        centerPanel.add(btnAdmin, gbc);


        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(0, 40));

        JLabel footerLabel = new JLabel("© 2024 Cinema Manager - All Rights Reserved");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(189, 195, 199));
        footerPanel.add(footerLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, String tooltip, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(350, 60));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);


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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Main(new RapController()).setVisible(true));
    }
}
