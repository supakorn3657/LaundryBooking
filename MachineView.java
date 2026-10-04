import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MachineView extends JFrame {

    // สี (ตั้งไว้ข้างบนจะได้แก้ที่เดียว)
    Color bgColor = new Color(247, 243, 236);
    Color darkText = new Color(17, 24, 39);
    Color grayText = new Color(107, 114, 128);
    Color lineColor = new Color(228, 224, 216);
    Color greenColor = new Color(34, 197, 94);
    Color lightGray = new Color(229, 231, 235);

    Font titleFont = new Font("Tahoma", Font.BOLD, 20);
    Font normalFont = new Font("Tahoma", Font.PLAIN, 14);
    Font boldFont = new Font("Tahoma", Font.BOLD, 14);
    Font smallFont = new Font("Tahoma", Font.PLAIN, 12);

    // รูปเครื่องซักผ้า (wash.png)
    // ที่อยู่ไฟล์รูป (ขึ้นต้นด้วย / = นับจากโฟลเดอร์หลักของโปรเจกต์)
    String imagePath = "/images/wash.png";
    BufferedImage washImage;
    int iconSize = 48;

    // ข้อมูลที่รับมาจากหน้า Dashboard
    String machineName;   // เช่น M-02
    String status;        // "AVAILABLE" / "IN USE" / "Pending"
    String machineRoom;   // ห้องที่ใช้เครื่องอยู่ (ถ้าว่างใส่ "--")
    String loginRoom;     // ห้องที่ล็อกอินอยู่
    String mode;          // โหมดที่เลือกไว้
    String leftText;      // ข้อความเวลาที่เหลือ
    String finishText;    // ข้อความเวลาซักเสร็จ

    // โหมดซักให้เลือก
    String[] modeName = {"ปั่นทั่วไป", "ปั่นเร็ว", "ปั่นผ้านวม"};
    String[] modeTime = {"45", "15", "70"};
    JRadioButton[] modeRadio = new JRadioButton[3];

    // ส่วนหัว
    JLabel topicMac = new JLabel("สถานะตู้ซักผ้า");
    JLabel clockMac = new JLabel("00:00:00"); // ยังไม่เดิน ไว้ทำทีหลัง
    JLabel roomtextMac = new JLabel("เลขห้อง");
    JLabel roomnumMac = new JLabel();

    public MachineView(String machineName, String status, String machineRoom,
                       String loginRoom, String mode, String leftText, String finishText) {

        this.machineName = machineName;
        this.status = status;
        this.machineRoom = machineRoom;
        this.loginRoom = loginRoom;
        this.mode = mode;
        this.leftText = leftText;
        this.finishText = finishText;

        // ตั้งค่าหน้าต่าง
        setTitle("Laundry Dorm - " + machineName);
        setSize(500, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // โหลดรูปเครื่องซักผ้า (ต้องทำก่อนสร้างการ์ด)
        loadWashImage();

        // พื้นที่หลัก
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);

        // ---------- ส่วนหัว (เหมือนหน้า Dashboard) ----------
        topicMac.setFont(titleFont);
        topicMac.setForeground(darkText);
        clockMac.setFont(titleFont);
        clockMac.setForeground(darkText);

        roomtextMac.setFont(normalFont);
        roomtextMac.setForeground(darkText);

        roomnumMac.setText(loginRoom);
        roomnumMac.setFont(new Font("Tahoma", Font.BOLD, 12));
        roomnumMac.setForeground(Color.WHITE);
        roomnumMac.setOpaque(true);
        roomnumMac.setBackground(new Color(156, 163, 175));
        roomnumMac.setBorder(
            BorderFactory.createEmptyBorder(3, 14, 3, 14)
        );

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(topicMac, BorderLayout.WEST);
        topRow.add(clockMac, BorderLayout.EAST);

        JPanel roomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        roomRow.setOpaque(false);
        roomRow.add(roomtextMac);
        roomRow.add(roomnumMac);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );
        headerPanel.add(topRow, BorderLayout.NORTH);
        headerPanel.add(roomRow, BorderLayout.CENTER);

        // ---------- เนื้อหา: การ์ดข้อมูลเครื่อง + การ์ดคำสั่ง ----------
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 20, 20)
        );

        bodyPanel.add(makeSummaryCard());
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(makeActionCard()); // การ์ดล่างเปลี่ยนไปตามสถานะ

        // ใส่ NORTH ไว้ การ์ดจะได้ไม่ถูกยืดเต็มหน้าต่าง
        JPanel wrapPanel = new JPanel(new BorderLayout());
        wrapPanel.setOpaque(false);
        wrapPanel.add(bodyPanel, BorderLayout.NORTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(wrapPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    // ---------------------------------------------------------------
    // การ์ดบน: ไอคอน ชื่อเครื่อง สถานะ ห้อง
    JPanel makeSummaryCard() {

        final Color statusFg = getStatusFg(status);
        final Color statusBg = getStatusBg(status);

        JPanel card = makeRoundPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // ไอคอนเครื่องซักผ้า (รูปย้อมสีตามสถานะ วางบนพื้นสีอ่อน)
        final BufferedImage iconImage = tintImage(statusFg);

        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(statusBg);
                g2.fillRoundRect(0, 0, 64, 64, 16, 16);

                if (iconImage != null) {
                    int pos = (64 - iconSize) / 2;
                    g2.drawImage(iconImage, pos, pos, null);
                }
                g2.dispose();
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(64, 64));

        // ชื่อเครื่อง + น้ำหนัก
        JLabel nameLabel = new JLabel(machineName);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        nameLabel.setForeground(darkText);

        JLabel kgLabel = new JLabel("10 kg");
        kgLabel.setFont(normalFont);
        kgLabel.setForeground(grayText);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0)); // เว้นระยะจากไอคอน
        namePanel.add(nameLabel);
        namePanel.add(kgLabel);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.add(icon);
        topRow.add(namePanel);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // ป้ายสถานะ
        JLabel chip = makeChip(status, statusFg, statusBg);

        // ห้อง (ถ้ารอเก็บผ้าจะเขียนว่า "ห้องบนเครื่อง")
        String roomText = "ห้อง: ";
        if (status.equals("Pending")) {
            roomText = "ห้องบนเครื่อง: ";
        }
        JLabel roomLabel = makeText(roomText + machineRoom, new Font("Tahoma", Font.BOLD, 15), darkText);

        card.add(topRow);
        card.add(Box.createVerticalStrut(10));
        card.add(chip);
        card.add(Box.createVerticalStrut(12));
        card.add(roomLabel);

        fixHeight(card);
        return card;
    }

    // ---------------------------------------------------------------
    // การ์ดล่าง: เปลี่ยนเนื้อหาตามสถานะ 4 แบบ
    //   1) AVAILABLE            -> เลือกโหมด + เริ่มซัก
    //   2) IN USE               -> เวลานับถอยหลัง + ปุ่มกลับ
    //   3) Pending ห้องตรง       -> ปุ่มเก็บผ้า
    //   4) Pending ห้องไม่ตรง     -> เก็บผ้าไม่ได้ + ปุ่มกลับ
    JPanel makeActionCard() {

        Color statusFg = getStatusFg(status);
        Color statusBg = getStatusBg(status);

        JPanel card = makeRoundPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        if (status.equals("AVAILABLE")) {

            // ----- แบบที่ 1: เครื่องว่าง เลือกโหมดแล้วกดเริ่มซัก -----
            card.add(makeText(machineName + "   เลือกโหมด เวลาซักตามโหมด", boldFont, darkText));
            card.add(Box.createVerticalStrut(10));

            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < modeName.length; i++) {
                modeRadio[i] = new JRadioButton(modeName[i] + " " + modeTime[i] + " นาที");
                modeRadio[i].setFont(normalFont);
                modeRadio[i].setForeground(darkText);
                modeRadio[i].setOpaque(false);
                modeRadio[i].setFocusPainted(false);
                modeRadio[i].setAlignmentX(Component.LEFT_ALIGNMENT);
                group.add(modeRadio[i]);
                card.add(modeRadio[i]);
            }

            JButton startButton = makeButton("เริ่มซัก", greenColor, Color.WHITE);
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // หาว่าเลือกโหมดไหนอยู่
                    int pick = -1;
                    for (int i = 0; i < modeRadio.length; i++) {
                        if (modeRadio[i].isSelected()) {
                            pick = i;
                        }
                    }

                    if (pick == -1) {
                        JOptionPane.showMessageDialog(
                            MachineView.this,
                            "กรุณาเลือกโหมดก่อน"
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            MachineView.this,
                            "เริ่มซัก " + machineName + " โหมด" + modeName[pick]
                        );
                    }
                }
            });

            card.add(Box.createVerticalStrut(14));
            card.add(startButton);
            card.add(Box.createVerticalStrut(8));
            card.add(makeBackButton("กลับไปตู้ทั้งหมด", lightGray, darkText));

        } else if (status.equals("IN USE")) {

            // ----- แบบที่ 2: กำลังซัก ดูเวลาอย่างเดียว -----
            card.add(makeText(machineName + "   กำลังซัก", boldFont, darkText));
            card.add(Box.createVerticalStrut(8));
            card.add(makeText("โหมดที่เลือก: " + mode, normalFont, darkText));
            card.add(Box.createVerticalStrut(8));
            card.add(makeText(leftText, new Font("Tahoma", Font.BOLD, 24), darkText));
            card.add(Box.createVerticalStrut(4));
            card.add(makeText(finishText, boldFont, darkText));
            card.add(Box.createVerticalStrut(16));
            card.add(makeBackButton("กลับ", statusBg, statusFg));

        } else {

            // ----- Pending: ซักเสร็จแล้ว รอเก็บผ้า -----
            card.add(makeText(machineName + "   รอเก็บผ้า", boldFont, darkText));
            card.add(Box.createVerticalStrut(8));
            card.add(makeText("ซักเสร็จแล้ว  โหมดที่เลือก: " + mode, normalFont, darkText));
            card.add(Box.createVerticalStrut(8));
            card.add(makeText(leftText, new Font("Tahoma", Font.BOLD, 24), darkText));
            card.add(Box.createVerticalStrut(16));

            if (loginRoom.equals(machineRoom)) {

                // ----- แบบที่ 3: เป็นห้องเดียวกัน เก็บผ้าได้ -----
                JButton pickupButton = makeButton("เก็บผ้า", statusBg, statusFg);
                pickupButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(
                            MachineView.this,
                            "เก็บผ้าจาก " + machineName + " เรียบร้อย"
                        );
                    }
                });
                card.add(pickupButton);
                card.add(Box.createVerticalStrut(8));

            } else {

                // ----- แบบที่ 4: คนละห้อง เก็บไม่ได้ -----
                card.add(makeText("ห้อง " + loginRoom + " เก็บผ้าไม่ได้", boldFont, new Color(220, 38, 38)));
                card.add(Box.createVerticalStrut(12));
            }

            card.add(makeBackButton("กลับ", lightGray, darkText));
        }

        fixHeight(card);
        return card;
    }

    // ---------------------------------------------------------------
    // ตัวช่วยสร้างส่วนประกอบ (เรียกใช้ซ้ำหลายที่)

    // การ์ดสีขาวมุมมน
    JPanel makeRoundPanel() {
        JPanel panel = new JPanel() {
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
        panel.setOpaque(false);
        return panel;
    }

    // ข้อความธรรมดา ชิดซ้าย
    JLabel makeText(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // ป้ายสถานะมุมมน
    JLabel makeChip(String text, final Color fg, final Color bg) {
        JLabel chip = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();

                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Tahoma", Font.BOLD, 12));
        chip.setForeground(fg);
        chip.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setMaximumSize(chip.getPreferredSize()); // ไม่ให้ป้ายถูกยืดเต็มการ์ด
        return chip;
    }

    // ปุ่มมุมมน เข้มขึ้นตอนเอาเมาส์ชี้ / กด
    JButton makeButton(String text, final Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed() || getModel().isRollover()) {
                    g2.setColor(bg.darker());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Tahoma", Font.BOLD, 15));
        button.setForeground(fg);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setPreferredSize(new Dimension(100, 44));
        return button;
    }

    // ปุ่มกลับ
    JButton makeBackButton(String text, Color bg, Color fg) {
        JButton button = makeButton(text, bg, fg);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ตอนรวมหน้าแล้วเปลี่ยนเป็นกลับไปหน้า Dashboard
                MachineView.this.dispose();
            }
        });
        return button;
    }

    // ล็อกความสูง ไม่ให้ BoxLayout ยืดการ์ดแนวตั้ง
    void fixHeight(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
    }

    // ---------------------------------------------------------------
    // โหลด wash.png แล้วย่อ (โค้ดเดียวกับหน้า Dashboard)
    void loadWashImage() {
        try {
            URL url = MachineView.class.getResource(imagePath);
            BufferedImage big = ImageIO.read(url);

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

    // ย้อมรูปเครื่องซักผ้าเป็นสี color (พื้นขาวให้โปร่งใส)
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

            // ตัวอย่างทั้ง 4 แบบ เปิดซ้อนกัน 4 หน้าต่าง (ลากแยกดูได้)
            // ลำดับพารามิเตอร์: ชื่อเครื่อง, สถานะ, ห้องบนเครื่อง, ห้องที่ล็อกอิน, โหมด, เวลาที่เหลือ, เวลาเสร็จ
            MachineView v1 = new MachineView("M-02", "AVAILABLE", "--", "100",
                "", "", "");
            MachineView v2 = new MachineView("M-02", "IN USE", "100", "100",
                "ปั่นทั่วไป", "เหลือเวลา : 45:00", "ซักเสร็จประมาณ : 17:00");
            MachineView v3 = new MachineView("M-05", "Pending", "102", "102",
                "ปั่นทั่วไป", "เหลือเวลาเก็บผ้า : 02:00", "");
            MachineView v4 = new MachineView("M-05", "Pending", "200", "100",
                "ปั่นทั่วไป", "เหลือเวลาเก็บผ้า : 02:00", "");

            MachineView[] all = {v1, v2, v3, v4};
            for (int i = 0; i < all.length; i++) {
                all[i].setLocation(30 + i * 60, 20 + i * 30);
                all[i].setVisible(true);
            }
        });
    }
}