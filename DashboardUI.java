package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * โครงจอครั้งที่ 1 — หน้าตาตามแบบที่จะทำจริง
 * ยังไม่ต่อ MachineController กดปุ่มแล้วยังไม่จอง
 */
public class DashboardUI {

    private static final Font FONT_UI = pickThai(Font.PLAIN, 14);
    private static final Font FONT_BTN = pickThai(Font.BOLD, 13);

    private final JFrame f = new JFrame("ระบบจองตู้ซักผ้าหอพัก");
    private final JTextField roomField = new JTextField(8);

    public DashboardUI() {
        f.setSize(980, 780);
        f.setMinimumSize(new Dimension(900, 700));
        f.setResizable(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 247, 250));
        header.setBorder(new EmptyBorder(16, 25, 8, 25));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Laundry Booking");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel roomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        roomRow.setBackground(new Color(245, 247, 250));
        roomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel roomLbl = new JLabel("เลขห้อง");
        roomLbl.setFont(FONT_UI);
        roomField.setFont(FONT_UI);
        roomRow.add(roomLbl);
        roomRow.add(roomField);

        left.add(title);
        left.add(Box.createVerticalStrut(8));
        left.add(roomRow);

        JLabel clockLabel = new JLabel("00:00:00");
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        clockLabel.setForeground(new Color(17, 24, 39));
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setVerticalAlignment(SwingConstants.TOP);

        header.add(left, BorderLayout.WEST);
        header.add(clockLabel, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(2, 3, 20, 20));
        grid.setBackground(new Color(245, 247, 250));
        grid.setBorder(new EmptyBorder(10, 20, 20, 20));

        for (int i = 1; i <= 6; i++) {
            String machineId = "M-" + String.format("%02d", i);
            grid.add(createMachineCard(machineId));
        }

        main.add(grid, BorderLayout.CENTER);
        f.setContentPane(main);
        f.setVisible(true);
    }

    public JPanel createMachineCard(String machineId) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(Color.WHITE);

        File iconFile = new File("images/wash.png");
        if (iconFile.isFile()) {
            ImageIcon icon = new ImageIcon(iconFile.getPath());
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            top.add(new JLabel(new ImageIcon(img)));
        }

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(Color.WHITE);
        JLabel machine = new JLabel(machineId);
        machine.setFont(new Font("Arial", Font.BOLD, 17));
        JLabel weight = new JLabel("10 kg");
        weight.setFont(new Font("Arial", Font.PLAIN, 11));
        weight.setForeground(Color.GRAY);
        namePanel.add(machine);
        namePanel.add(weight);
        top.add(namePanel);

        JLabel status = new JLabel("AVAILABLE");
        status.setOpaque(true);
        status.setBackground(new Color(220, 252, 231));
        status.setForeground(new Color(22, 163, 74));
        status.setBorder(new EmptyBorder(5, 10, 5, 10));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel room = new JLabel("Room : --");
        room.setFont(FONT_UI);
        room.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel time = new JLabel("Time Left : 00:00");
        time.setFont(FONT_UI);
        time.setForeground(Color.GRAY);
        time.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel choose = new JLabel("Select Time");
        choose.setFont(new Font("Arial", Font.PLAIN, 12));
        choose.setForeground(Color.GRAY);
        choose.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] slot = {
                "00:00 - 01:00", "01:00 - 02:00", "02:00 - 03:00", "03:00 - 04:00",
                "04:00 - 05:00", "05:00 - 06:00", "06:00 - 07:00", "07:00 - 08:00",
                "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
                "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00",
                "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00"
        };
        JComboBox<String> combo = new JComboBox<String>(slot);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton reserve = new JButton("จองเลย");
        JButton book = new JButton("จองชั่วโมง");
        JButton start = new JButton("เริ่มซัก");
        JButton collect = new JButton("เก็บผ้า");
        stylePrimary(reserve, new Color(37, 99, 235));
        stylePrimary(book, new Color(22, 163, 74));
        stylePrimary(start, new Color(234, 88, 12));
        stylePrimary(collect, new Color(202, 138, 4));
        start.setEnabled(false);
        collect.setEnabled(false);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
        buttons.setBackground(Color.WHITE);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        buttons.add(reserve);
        buttons.add(book);
        buttons.add(start);
        buttons.add(collect);

        card.add(top);
        card.add(Box.createVerticalStrut(8));
        card.add(status);
        card.add(Box.createVerticalStrut(8));
        card.add(room);
        card.add(Box.createVerticalStrut(4));
        card.add(time);
        card.add(Box.createVerticalStrut(8));
        card.add(choose);
        card.add(Box.createVerticalStrut(4));
        card.add(combo);
        card.add(Box.createVerticalStrut(8));
        card.add(buttons);
        return card;
    }

    private void stylePrimary(JButton button, Color bg) {
        button.setUI(new BasicButtonUI());
        button.setBackground(Color.WHITE);
        button.setForeground(bg);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg, 2, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        button.setFont(FONT_BTN);
    }

    private static Font pickThai(int style, int size) {
        String[] names = {
            "Leelawadee UI", "Leelawadee", "Tahoma", "Segoe UI",
            "Microsoft Sans Serif", "Cordia New", "Dialog"
        };
        for (int i = 0; i < names.length; i++) {
            Font font = new Font(names[i], style, size);
            if (font.canDisplayUpTo("กขคงจองเลยเก็บผ้า") == -1) {
                return font;
            }
        }
        return new Font("Tahoma", style, size);
    }

    private static void setupLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Button.font", FONT_BTN);
        UIManager.put("Label.font", FONT_UI);
        UIManager.put("TextField.font", FONT_UI);
        UIManager.put("ComboBox.font", FONT_UI);
    }

    public static void main(String[] args) {
        setupLook();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DashboardUI();
            }
        });
    }
}
