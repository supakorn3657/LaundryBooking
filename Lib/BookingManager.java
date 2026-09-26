package Lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * สมุดนัดจองชั่วโมง — จำใน RAM แล้วเซฟลงไฟล์
 * รูปแบบไฟล์ (Dev 4 ปรับต่อได้): bookingId,roomNo,machineId,startHour,endHour
 */
public class BookingManager {
    private final List<Booking> bookings;
    private final String filePath;
    private int nextId;

    public BookingManager(String filePath) {
        this.filePath = filePath;
        this.bookings = new ArrayList<Booking>();
        this.nextId = 1;
    }

    public void add(Booking booking) throws InvalidOperationException {
        if (!isSlotFree(booking.getMachineId(), booking.getStartHour(), booking.getEndHour())) {
            throw new InvalidOperationException("ช่วงเวลานี้มีคนจองแล้ว");
        }
        bookings.add(booking);
        bumpNextId(booking.getBookingId());
    }

    public boolean isSlotFree(String machineId, int startHour, int endHour) {
        for (Booking b : bookings) {
            if (b.getMachineId().equals(machineId) && b.overlaps(startHour, endHour)) {
                return false;
            }
        }
        return true;
    }

    /** หาใบจองที่ถึงชั่วโมงนี้แล้ว ของตู้ไหนก็ได้ ใบแรกที่เจอ */
    public Booking findDue(int nowHour) {
        for (Booking b : bookings) {
            if (b.getStartHour() == nowHour) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> getByMachine(String machineId) {
        List<Booking> result = new ArrayList<Booking>();
        for (Booking b : bookings) {
            if (b.getMachineId().equals(machineId)) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Booking> getAll() {
        return Collections.unmodifiableList(bookings);
    }

    public String nextBookingId() {
        return "B" + String.format("%03d", nextId++);
    }

    public String getFilePath() {
        return filePath;
    }

    public void save() throws IOException {
        FileWriter writer = new FileWriter(filePath);
        try {
            writer.write("bookingId,roomNo,machineId,startHour,endHour\n");
            for (Booking b : bookings) {
                writer.write(b.getBookingId() + "," + b.getRoomNo() + "," + b.getMachineId()
                        + "," + b.getStartHour() + "," + b.getEndHour() + "\n");
            }
        } finally {
            writer.close();
        }
    }

    public void load() throws IOException {
        bookings.clear();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            return;
        }
        try {
            String header = reader.readLine();
            if (header == null) {
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(",");
                if (p.length < 5) {
                    continue;
                }
                Booking b = new Booking(p[0].trim(), p[1].trim(), p[2].trim(),
                        Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim()));
                bookings.add(b);
                bumpNextId(b.getBookingId());
            }
        } finally {
            reader.close();
        }
    }

    private void bumpNextId(String bookingId) {
        if (bookingId != null && bookingId.startsWith("B")) {
            try {
                int n = Integer.parseInt(bookingId.substring(1));
                if (n >= nextId) {
                    nextId = n + 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
