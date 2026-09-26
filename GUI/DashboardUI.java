package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class DashboardUI {

    JFrame f = new JFrame("Laundry Booking");

    public DashboardUI() {

        
        f.setSize(980, 720);
        f.setMinimumSize(new Dimension(900, 650));
        f.setResizable(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));

        // หัว
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(245, 247, 250));
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Laundry Booking");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        

        main.add(header, BorderLayout.NORTH);

        // ลูบวาง grid
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(2, 3, 20, 20));
        grid.setBackground(new Color(245, 247, 250));
        grid.setBorder(new EmptyBorder(10, 20, 20, 20));

        for (int i = 1; i <= 6; i++) {
            grid.add(createMachineCard(i));
        }

        main.add(grid, BorderLayout.CENTER);

        f.setContentPane(main);
        f.setVisible(true);
    }

    // ออกแบบเครื่องทั้งหมด
    public JPanel createMachineCard(int id) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

     
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(Color.WHITE);
    // รูป
        ImageIcon icon = new ImageIcon("images/wash.png");
        Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel machineIcon = new JLabel(new ImageIcon(img));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(Color.WHITE);

        JLabel machine = new JLabel("M-0" + id);
        machine.setFont(new Font("Arial", Font.BOLD, 17));

        JLabel weight = new JLabel("10 kg");
        weight.setFont(new Font("Arial", Font.PLAIN, 11));
        weight.setForeground(Color.GRAY);

        namePanel.add(machine);
        namePanel.add(weight);

        top.add(machineIcon);
        top.add(namePanel);

        // สถานะ
        JLabel status = new JLabel("AVAILABLE");
        status.setOpaque(true);
        status.setBackground(new Color(220, 252, 231));
        status.setForeground(new Color(22, 163, 74));
        status.setBorder(new EmptyBorder(5, 10, 5, 10));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        // เลขห้อง
        JLabel room = new JLabel("Room : --");
        room.setFont(new Font("Arial", Font.PLAIN, 13));
        room.setAlignmentX(Component.LEFT_ALIGNMENT);

        // เวลา
        JLabel time = new JLabel("Time Left : --:--");
        time.setFont(new Font("Arial", Font.PLAIN, 13));
        time.setForeground(Color.GRAY);
        time.setAlignmentX(Component.LEFT_ALIGNMENT);

        // เลือกเวลา
        JLabel choose = new JLabel("Select Time");
        choose.setFont(new Font("Arial", Font.PLAIN, 12));
        choose.setForeground(Color.GRAY);
        choose.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] slot = {
                "00:00 - 01:00",
                "01:00 - 02:00",
                "02:00 - 03:00",
                "03:00 - 04:00",
                "04:00 - 05:00",
                "05:00 - 06:00",
                "06:00 - 07:00",
                "07:00 - 08:00",
                "08:00 - 09:00",
                "09:00 - 10:00",
                "10:00 - 11:00",
                "11:00 - 12:00",
                "12:00 - 13:00",
                "13:00 - 14:00",
                "14:00 - 15:00",
                "15:00 - 16:00",
                "16:00 - 17:00",
                "17:00 - 18:00",
                "18:00 - 19:00",
                "19:00 - 20:00",
                "20:00 - 21:00",
                "21:00 - 22:00",
                "22:00 - 23:00",
                "23:00 - 00:00"
        };

        JComboBox<String> combo = new JComboBox<>(slot);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ปุ่มจอง
        JButton reserve = new JButton("จองเลย");
        reserve.setBackground(new Color(37, 99, 235));
        reserve.setForeground(Color.WHITE);
        reserve.setFocusPainted(false);
        reserve.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        reserve.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        reserve.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        card.add(top);
        card.add(Box.createVerticalStrut(12));
        card.add(status);
        card.add(Box.createVerticalStrut(12));
        card.add(room);
        card.add(Box.createVerticalStrut(8));
        card.add(time);
        card.add(Box.createVerticalStrut(15));
        card.add(choose);
        card.add(Box.createVerticalStrut(5));
        card.add(combo);
        card.add(Box.createVerticalStrut(15));
        card.add(reserve);

        return card;
    }

    public static void main(String[] args) {
        new DashboardUI();
    }
}