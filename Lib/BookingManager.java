package Lib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * โครงครั้งที่ 1 — สมุดจองชั่วโมง (ยังไม่เซฟไฟล์ / ยังไม่กันทับ)
 */
public class BookingManager {
    private final String filePath;

    public BookingManager(String filePath) {
        this.filePath = filePath;
    }

    public void add(Booking booking) throws InvalidOperationException {
    }

    public boolean isSlotFree(String machineId, int startHour, int endHour) {
        return false;
    }

    public Booking findDue(int nowHour) {
        return null;
    }

    public List<Booking> getByMachine(String machineId) {
        return new ArrayList<Booking>();
    }

    public List<Booking> getAll() {
        return new ArrayList<Booking>();
    }

    public String nextBookingId() {
        return null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void save() throws IOException {
    }

    public void load() throws IOException {
    }
}
