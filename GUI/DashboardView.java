import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class DashboardView extends JFrame {

    // สี (ตั้งไว้ข้างบนจะได้แก้ที่เดียว)
    Color bgColor = new Color(247, 243, 236);
    Color darkText = new Color(17, 24, 39);
    Color grayText = new Color(107, 114, 128);
    Color lineColor = new Color(228, 224, 216);

    Font titleFont = new Font("Tahoma", Font.BOLD, 20);
    Font normalFont = new Font("Tahoma", Font.PLAIN, 14);
    Font boldFont = new Font("Tahoma", Font.BOLD, 13);
    Font smallFont = new Font("Tahoma", Font.PLAIN, 11);

    // รูปเครื่องซักผ้า (wash.png) โหลดครั้งเดียวแล้วย่อไว้ที่นี่
    // ที่อยู่ไฟล์รูป (ขึ้นต้นด้วย / = นับจากโฟลเดอร์หลักของโปรเจกต์)
    String imagePath = "/images/wash.png";
    BufferedImage washImage;
    int iconSize = 34;

    // ส่วนหัว
    JLabel topicDash = new JLabel("สถานะตู้ซักผ้า");
    JLabel clockDash = new JLabel("00:00:00"); // ยังไม่เดิน ไว้ทำทีหลัง
    JLabel roomtextDash = new JLabel("เลขห้อง");
    JLabel roomnumDash = new JLabel("305");    // เลขห้องที่ล็อกอิน (ตัวอย่าง)

    // ข้อมูลตัวอย่างของเครื่อง 6 เครื่อง (ยังไม่ได้ต่อข้อมูลจริง)
    String[] nameDash = {"M-01", "M-02", "M-03", "M-04", "M-05", "M-06"};
    String[] statusDash = {"AVAILABLE", "IN USE", "IN USE", "AVAILABLE", "Pending", "AVAILABLE"};
    String[] roomDash = {"--", "100", "100", "--", "102", "--"};
    String[] leftDash = {
        "เหลือเวลา : --:--",
        "เหลือเวลา : 45:00",
        "เหลือเวลา : 03:12",
        "เหลือเวลา : --:--",
        "เหลือเวลาเก็บผ้า : 02:00",
        "เหลือเวลา : --:--"
    };
    String[] finishDash = {
        "ซักเสร็จประมาณ : --:--",
        "ซักเสร็จประมาณ : 17:00",
        "ซักเสร็จประมาณ : 16:00",
        "ซักเสร็จประมาณ : --:--",
        "ซักเสร็จแล้ว",
        "ซักเสร็จประมาณ : --:--"
    };

    public DashboardView() {

        // ตั้งค่าหน้าต่าง
        setTitle("Laundry Dorm");
        setSize(500, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // โหลดรูปเครื่องซักผ้า (ต้องทำก่อนสร้างการ์ด)
        loadWashImage();

        // พื้นที่หลัก
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);

        // ---------- ส่วนหัว ----------
        topicDash.setFont(titleFont);
        topicDash.setForeground(darkText);
        clockDash.setFont(titleFont);
        clockDash.setForeground(darkText);

        roomtextDash.setFont(normalFont);
        roomtextDash.setForeground(darkText);

        // ป้ายเลขห้องสีเทา
        roomnumDash.setFont(new Font("Tahoma", Font.BOLD, 12));
        roomnumDash.setForeground(Color.WHITE);
        roomnumDash.setOpaque(true);
        roomnumDash.setBackground(new Color(156, 163, 175));
        roomnumDash.setBorder(
            BorderFactory.createEmptyBorder(3, 14, 3, 14)
        );

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(topicDash, BorderLayout.WEST);
        topRow.add(clockDash, BorderLayout.EAST);

        JPanel roomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        roomRow.setOpaque(false);
        roomRow.add(roomtextDash);
        roomRow.add(roomnumDash);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );
        headerPanel.add(topRow, BorderLayout.NORTH);
        headerPanel.add(roomRow, BorderLayout.CENTER);

        // ---------- การ์ดเครื่อง 6 ใบ (3 แถว 2 คอลัมน์) ----------
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 12, 12));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 20, 20)
        );

        for (int i = 0; i < nameDash.length; i++) {
            gridPanel.add(makeCard(i));
        }

        // ใส่ NORTH ไว้ การ์ดจะได้ไม่ถูกยืดเวลาหน้าต่างสูงเกิน
        JPanel wrapPanel = new JPanel(new BorderLayout());
        wrapPanel.setOpaque(false);
        wrapPanel.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    // สร้างการ์ดของเครื่องลำดับที่ i
    JPanel makeCard(final int i) {

        final Color statusFg = getStatusFg(statusDash[i]);
        final Color statusBg = getStatusBg(statusDash[i]);

        // การ์ดสีขาวมุมมน
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(lineColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ไอคอนเครื่องซักผ้า (ใช้รูป wash.png ย้อมสีตามสถานะ)
        final BufferedImage iconImage = tintImage(statusFg);

        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // พื้นสี่เหลี่ยมมนสีอ่อนตามสถานะ
                g2.setColor(statusBg);
                g2.fillRoundRect(0, 0, 44, 44, 12, 12);

                if (iconImage != null) {
                    // วางรูปไว้กลางพื้น
                    int pos = (44 - iconSize) / 2;
                    g2.drawImage(iconImage, pos, pos, null);
                } else {
                    // ถ้าหาไฟล์รูปไม่เจอ วาดวงกลมแทนไปก่อน
                    g2.setColor(statusFg);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(11, 13, 20, 20);
                }
                g2.dispose();
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(44, 44));

        // ชื่อเครื่อง + น้ำหนัก
        JLabel nameLabel = new JLabel(nameDash[i]);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        nameLabel.setForeground(darkText);

        JLabel kgLabel = new JLabel("10 kg");
        kgLabel.setFont(smallFont);
        kgLabel.setForeground(grayText);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // เว้นระยะจากไอคอน
        namePanel.add(nameLabel);
        namePanel.add(kgLabel);

        // แถวบน = ไอคอน + ชื่อ
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.add(icon);
        topRow.add(namePanel);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        // ป้ายสถานะมุมมน
        JLabel chip = new JLabel(statusDash[i]) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(statusBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();

                super.paintComponent(g); // วาดตัวหนังสือทับ
            }
        };
        chip.setFont(new Font("Tahoma", Font.BOLD, 11));
        chip.setForeground(statusFg);
        chip.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setMaximumSize(chip.getPreferredSize()); // ไม่ให้ป้ายถูกยืดเต็มการ์ด

        // ข้อมูลของเครื่อง
        JLabel roomLabel = new JLabel("ห้อง: " + roomDash[i]);
        roomLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        roomLabel.setForeground(darkText);
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel leftLabel = new JLabel(leftDash[i]);
        leftLabel.setFont(boldFont);
        leftLabel.setForeground(darkText);
        leftLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel finishLabel = new JLabel(finishDash[i]);
        finishLabel.setFont(boldFont);
        finishLabel.setForeground(darkText);
        finishLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hintLabel = new JLabel("คลิกการ์ดเพื่อเปิดหน้าคำสั่ง");
        hintLabel.setFont(smallFont);
        hintLabel.setForeground(grayText);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // เพิ่มส่วนประกอบลงการ์ด
        card.add(topRow);
        card.add(Box.createVerticalStrut(8));
        card.add(chip);
        card.add(Box.createVerticalStrut(10));
        card.add(roomLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(leftLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(finishLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(hintLabel);

        // เมื่อคลิกการ์ด (ตอนนี้แค่โชว์ข้อความ ยังไม่ไปหน้า MachineView)
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                    DashboardView.this,
                    "เปิดหน้าเครื่อง " + nameDash[i]
                );
            }
        });

        return card;
    }

    // โหลด wash.png (วางไว้โฟลเดอร์เดียวกับไฟล์ .class) แล้วย่อให้เล็กลง
    void loadWashImage() {
        try {
            URL url = DashboardView.class.getResource(imagePath);
            BufferedImage big = ImageIO.read(url);

            // ย่อรูป (ใส่ ImageIcon ครอบไว้ เพื่อให้รูปโหลดเสร็จก่อนค่อยวาด)
            Image small = new ImageIcon(
                big.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
            ).getImage();

            washImage = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = washImage.createGraphics();
            g2.drawImage(small, 0, 0, null);
            g2.dispose();
        } catch (Exception e) {
            System.out.println("หาไฟล์รูปไม่เจอ: " + imagePath);
        }
    }

    // ย้อมรูปเครื่องซักผ้าเป็นสี color
    // รูปต้นฉบับพื้นขาว เส้นสีเขียว เลยดูว่าพิกเซลไหน "ไม่ขาว" = เป็นเส้น
    // แล้วเอาเส้นนั้นไปใส่สีใหม่ พื้นขาวให้โปร่งใส
    BufferedImage tintImage(Color color) {
        if (washImage == null) {
            return null;
        }

        BufferedImage result = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        int colorRgb = color.getRGB() & 0x00FFFFFF;

        for (int x = 0; x < iconSize; x++) {
            for (int y = 0; y < iconSize; y++) {
                int red = (washImage.getRGB(x, y) >> 16) & 0xFF;

                // red = 255 คือพื้นขาว, red = 76 คือเส้นเขียวเต็มๆ
                int alpha = (255 - red) * 255 / (255 - 76);
                if (alpha > 255) {
                    alpha = 255;
                }
                if (alpha < 0) {
                    alpha = 0;
                }

                result.setRGB(x, y, (alpha << 24) | colorRgb);
            }
        }
        return result;
    }

    // สีตัวหนังสือของป้ายสถานะ
    Color getStatusFg(String status) {
        if (status.equals("AVAILABLE")) {
            return new Color(22, 163, 74);   // เขียว
        } else if (status.equals("IN USE")) {
            return new Color(220, 38, 38);   // แดง
        } else {
            return new Color(180, 120, 0);   // เหลืองเข้ม (Pending)
        }
    }

    // สีพื้นของป้ายสถานะ
    Color getStatusBg(String status) {
        if (status.equals("AVAILABLE")) {
            return new Color(220, 252, 231); // เขียวอ่อน
        } else if (status.equals("IN USE")) {
            return new Color(254, 215, 215); // ชมพูอ่อน
        } else {
            return new Color(254, 240, 180); // เหลืองอ่อน
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardView().setVisible(true);
        });
    }
}