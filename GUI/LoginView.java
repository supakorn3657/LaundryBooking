import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    // สี (ตั้งไว้ข้างบนจะได้แก้ที่เดียว)
    Color bgColor = new Color(247, 243, 236);
    Color darkText = new Color(17, 24, 39);
    Color grayText = new Color(107, 114, 128);
    Color lineColor = new Color(214, 219, 226);

    Font titleFont = new Font("Tahoma", Font.BOLD, 26);
    Font normalFont = new Font("Tahoma", Font.PLAIN, 14);
    Font boldFont = new Font("Tahoma", Font.BOLD, 14);

    JLabel topicLog = new JLabel("เข้าสู่ระบบ");
    JLabel noviceLog = new JLabel("ใส่เลขห้องอย่างเดียว ไม่มีรหัสผ่าน");
    JLabel roomtextLog = new JLabel("เลขห้อง");

    JTextField roomnumLog = new JTextField(10);

   
    JButton inLog = new JButton("เข้าจอสถานะ") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(new Color(21, 128, 61));
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(22, 163, 74));
            } else {
                g2.setColor(new Color(34, 197, 94));
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();

            super.paintComponent(g); // วาดตัวหนังสือทับ
        }
    };

    // กรอบขาว
    JPanel loginPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
            g2.setColor(new Color(228, 224, 216));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
            g2.dispose();
        }
    };

    public LoginView() {

        // ตั้งค่าหน้าต่าง
        setTitle("Laundry Dorm");
        setSize(500, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(bgColor);

        
        loginPanel.setOpaque(false); 
        loginPanel.setLayout(
            new BoxLayout(loginPanel, BoxLayout.Y_AXIS)
        );
        loginPanel.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );
        loginPanel.setPreferredSize(new Dimension(360, 340));

        // ฟอนต์และสี
        topicLog.setFont(titleFont);
        topicLog.setForeground(darkText);
        noviceLog.setFont(normalFont);
        noviceLog.setForeground(grayText);
        roomtextLog.setFont(boldFont);
        roomtextLog.setForeground(darkText);

        // ช่องกรอกเลขห้อง
        roomnumLog.setFont(new Font("Tahoma", Font.PLAIN, 16));
        roomnumLog.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor, 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
            )
        );

        // ปุ่ม
        inLog.setFont(new Font("Tahoma", Font.BOLD, 15));
        inLog.setForeground(Color.WHITE);
        inLog.setContentAreaFilled(false); // เราวาดพื้นปุ่มเองแล้ว
        inLog.setBorderPainted(false);
        inLog.setFocusPainted(false);
        inLog.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        
        topicLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        noviceLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomtextLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomnumLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        inLog.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        topicLog.setHorizontalAlignment(SwingConstants.CENTER);
        noviceLog.setHorizontalAlignment(SwingConstants.CENTER);
        topicLog.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, topicLog.getPreferredSize().height)
        );
        noviceLog.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, noviceLog.getPreferredSize().height)
        );

        
        roomnumLog.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );
        inLog.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 44)
        );

        // เพิ่มส่วนประกอบลง JPanel (glue = ดันให้เนื้อหาอยู่กลางการ์ด)
        loginPanel.add(Box.createVerticalGlue());

        loginPanel.add(topicLog);
        loginPanel.add(Box.createVerticalStrut(6));

        loginPanel.add(noviceLog);
        loginPanel.add(Box.createVerticalStrut(28));

        loginPanel.add(roomtextLog);
        loginPanel.add(Box.createVerticalStrut(6));

        loginPanel.add(roomnumLog);
        loginPanel.add(Box.createVerticalStrut(22));

        loginPanel.add(inLog);

        loginPanel.add(Box.createVerticalGlue());

        // วาง JPanel ไว้กลางหน้าต่าง
        mainPanel.add(loginPanel);

        add(mainPanel);

        // เมื่อกดปุ่ม
        inLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String room = roomnumLog.getText().trim();

                if (room.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        LoginView.this,
                        "กรุณากรอกเลขห้อง"
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        LoginView.this,
                        "เข้าสู่ระบบ ห้อง " + room
                    );
                }
            }
        });

        // กด Enter ในช่องกรอก = กดปุ่มเหมือนกัน
        roomnumLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inLog.doClick();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}