package GUI;

import Lib.Booking;
import Lib.BookingManager;
import Lib.FileLogService;
import Lib.InvalidOperationException;
import Lib.LogService;
import Lib.MachineController;
import Lib.MachineListener;
import Lib.MachineStatus;
import Lib.WashingMachine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * จอที่ต่อ MachineController แล้ว (จองเลย / จองชั่วโมง / เริ่มซัก / เก็บผ้า)
 * แยกจาก GUI/DashboardUI.java ของณัฐดนัยที่ยังไม่ต่อระบบ
 * เดโมรันคลาสนี้: GUI.DashboardApp
 */
public class DashboardApp implements MachineListener {

    private static final Font FONT_UI = pickThai(Font.PLAIN, 14);
    private static final Font FONT_BTN = pickThai(Font.BOLD, 13);

    private final JFrame f = new JFrame("ระบบจองตู้ซักผ้าหอพัก");
    private final MachineController controller;
    private final JTextField roomField = new JTextField(8);
    private final JLabel clockLabel = new JLabel("--:--:--");
    private final Map<String, MachineCard> cards = new HashMap<String, MachineCard>();

    public DashboardApp(MachineController controller) {
        this.controller = controller;
        this.controller.addListener(this);

        f.setSize(980, 780);
        f.setMinimumSize(new Dimension(900, 700));
        f.setResizable(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.shutdown();
                try {
                    controller.getBookingManager().save();
                } catch (Exception ignored) {
                }
            }
        });

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

        clockLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        clockLabel.setForeground(new Color(17, 24, 39));
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setVerticalAlignment(SwingConstants.TOP);
        clockLabel.setText(formatClock(controller.getNow()));

        header.add(left, BorderLayout.WEST);
        header.add(clockLabel, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(2, 3, 20, 20));
        grid.setBackground(new Color(245, 247, 250));
        grid.setBorder(new EmptyBorder(10, 20, 20, 20));

        for (int i = 1; i <= 6; i++) {
            String machineId = "M-" + String.format("%02d", i);
            MachineCard card = createMachineCard(machineId);
            cards.put(machineId, card);
            grid.add(card.panel);
            WashingMachine machine = controller.findMachine(machineId);
            if (machine != null) {
                applyStatus(machine);
            }
        }

        main.add(grid, BorderLayout.CENTER);
        f.setContentPane(main);
        f.setVisible(true);
        controller.startTimer();
    }

    private MachineCard createMachineCard(final String machineId) {
        MachineCard card = new MachineCard();

        card.panel.setLayout(new BoxLayout(card.panel, BoxLayout.Y_AXIS));
        card.panel.setBackground(Color.WHITE);
        card.panel.setBorder(new CompoundBorder(
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

        card.status.setOpaque(true);
        card.status.setBackground(new Color(220, 252, 231));
        card.status.setForeground(new Color(22, 163, 74));
        card.status.setBorder(new EmptyBorder(5, 10, 5, 10));
        card.status.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.room.setFont(FONT_UI);
        card.room.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.time.setFont(FONT_UI);
        card.time.setForeground(Color.GRAY);
        card.time.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel choose = new JLabel("Select Time");
        choose.setFont(new Font("Arial", Font.PLAIN, 12));
        choose.setForeground(Color.GRAY);
        choose.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        card.combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        stylePrimary(card.reserve, new Color(37, 99, 235));
        stylePrimary(card.book, new Color(22, 163, 74));
        stylePrimary(card.start, new Color(234, 88, 12));
        stylePrimary(card.collect, new Color(202, 138, 4));

        card.reserve.addActionListener(e -> callReserveNow(machineId));
        card.book.addActionListener(e -> callBookSlot(machineId, card.combo));
        card.start.addActionListener(e -> callStartWash(machineId));
        card.collect.addActionListener(e -> callCollect(machineId));

        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
        buttons.setBackground(Color.WHITE);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        buttons.add(card.reserve);
        buttons.add(card.book);
        buttons.add(card.start);
        buttons.add(card.collect);

        card.panel.add(top);
        card.panel.add(Box.createVerticalStrut(8));
        card.panel.add(card.status);
        card.panel.add(Box.createVerticalStrut(8));
        card.panel.add(card.room);
        card.panel.add(Box.createVerticalStrut(4));
        card.panel.add(card.time);
        card.panel.add(Box.createVerticalStrut(8));
        card.panel.add(choose);
        card.panel.add(Box.createVerticalStrut(4));
        card.panel.add(card.combo);
        card.panel.add(Box.createVerticalStrut(8));
        card.panel.add(buttons);

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

    private String typedRoom() throws InvalidOperationException {
        String roomNo = roomField.getText();
        if (roomNo == null || roomNo.trim().isEmpty()) {
            throw new InvalidOperationException("กรอกเลขห้องก่อน");
        }
        return roomNo.trim();
    }

    private void callReserveNow(String machineId) {
        try {
            controller.reserveNow(machineId, typedRoom());
        } catch (InvalidOperationException ex) {
            showError(ex.getMessage());
        }
    }

    private void callBookSlot(String machineId, JComboBox<String> combo) {
        try {
            String slot = (String) combo.getSelectedItem();
            int startHour = Integer.parseInt(slot.substring(0, 2));
            int endHour = Integer.parseInt(slot.substring(8, 10));
            if (endHour == 0) {
                endHour = 24;
            }
            controller.bookSlot(machineId, typedRoom(), startHour, endHour);
            JOptionPane.showMessageDialog(f,
                    "จอง " + machineId + " เวลา " + slot + " แล้ว",
                    "จองชั่วโมง",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidOperationException ex) {
            showError(ex.getMessage());
        }
    }

    private void callStartWash(String machineId) {
        try {
            controller.startWash(machineId);
        } catch (InvalidOperationException ex) {
            showError(ex.getMessage());
        }
    }

    private void callCollect(String machineId) {
        try {
            controller.collectClothes(machineId);
        } catch (InvalidOperationException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(f, message, "ทำรายการไม่ได้", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void onStatusChanged(final WashingMachine machine) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                applyStatus(machine);
            }
        });
    }

    @Override
    public void onTimerTick(final WashingMachine machine) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MachineCard card = cards.get(machine.getMachineId());
                if (card != null) {
                    card.time.setText("Time Left : " + formatTime(machine.getTimeRemaining()));
                }
            }
        });
    }

    @Override
    public void onClockTick(final LocalTime time) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                clockLabel.setText(formatClock(time));
            }
        });
    }

    @Override
    public void onSlotDue(final Booking booking) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(f,
                        "ถึงเวลาจองตู้ " + booking.getMachineId()
                                + "\nห้อง " + booking.getRoomNo()
                                + "\nกรุณากดเริ่มซักภายใน 15 นาที",
                        "ถึงชั่วโมงจอง",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void applyStatus(WashingMachine machine) {
        MachineCard card = cards.get(machine.getMachineId());
        if (card == null) {
            return;
        }
        MachineStatus status = machine.getStatus();
        card.status.setText(status.name());
        String roomNo = machine.getCurrentRoomNo();
        card.room.setText("Room : " + (roomNo == null ? "--" : roomNo));
        card.time.setText("Time Left : " + formatTime(machine.getTimeRemaining()));

        if (status == MachineStatus.AVAILABLE) {
            paintStatus(card.status, new Color(220, 252, 231), new Color(22, 163, 74));
            card.reserve.setEnabled(true);
            card.book.setEnabled(true);
            card.start.setEnabled(false);
            card.collect.setEnabled(false);
        } else if (status == MachineStatus.RESERVED) {
            paintStatus(card.status, new Color(255, 237, 213), new Color(234, 88, 12));
            card.reserve.setEnabled(false);
            card.book.setEnabled(true);
            card.start.setEnabled(true);
            card.collect.setEnabled(false);
        } else if (status == MachineStatus.IN_USE) {
            paintStatus(card.status, new Color(254, 226, 226), new Color(220, 38, 38));
            card.reserve.setEnabled(false);
            card.book.setEnabled(true);
            card.start.setEnabled(false);
            card.collect.setEnabled(false);
        } else {
            paintStatus(card.status, new Color(254, 249, 195), new Color(202, 138, 4));
            card.reserve.setEnabled(false);
            card.book.setEnabled(true);
            card.start.setEnabled(false);
            card.collect.setEnabled(true);
        }
    }

    private void paintStatus(JLabel label, Color bg, Color fg) {
        label.setBackground(bg);
        label.setForeground(fg);
    }

    private String formatClock(LocalTime time) {
        return String.format("%02d:%02d:%02d", time.getHour(), time.getMinute(), time.getSecond());
    }

    private String formatTime(int seconds) {
        if (seconds <= 0) {
            return "--:--";
        }
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
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
        UIManager.put("OptionPane.messageFont", FONT_UI);
        UIManager.put("OptionPane.buttonFont", FONT_BTN);
        UIManager.put("Button.font", FONT_BTN);
        UIManager.put("Label.font", FONT_UI);
        UIManager.put("TextField.font", FONT_UI);
        UIManager.put("ComboBox.font", FONT_UI);
    }

    public static void main(String[] args) {
        setupLook();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BookingManager books = new BookingManager("bookings.csv");
                try {
                    books.load();
                } catch (Exception ignored) {
                }
                LogService log = new FileLogService("laundry_log.txt");
                MachineController controller = new MachineController(books, log);
                new DashboardApp(controller);
            }
        });
    }

    private static class MachineCard {
        final JPanel panel = new JPanel();
        final JLabel status = new JLabel("AVAILABLE");
        final JLabel room = new JLabel("Room : --");
        final JLabel time = new JLabel("Time Left : --:--");
        final JComboBox<String> combo = new JComboBox<String>(new String[] {
                "00:00 - 01:00", "01:00 - 02:00", "02:00 - 03:00", "03:00 - 04:00",
                "04:00 - 05:00", "05:00 - 06:00", "06:00 - 07:00", "07:00 - 08:00",
                "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
                "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00",
                "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00"
        });
        final JButton reserve = new JButton("จองเลย");
        final JButton book = new JButton("จองชั่วโมง");
        final JButton start = new JButton("เริ่มซัก");
        final JButton collect = new JButton("เก็บผ้า");
    }
}
