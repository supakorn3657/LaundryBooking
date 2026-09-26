package Lib;

/**
 * โครงครั้งที่ 1 — ใบจอง 1 ใบ (ยังไม่เช็คทับช่วง)
 */
public class Booking {
    private final String bookingId;
    private final String roomNo;
    private final String machineId;
    private final int startHour;
    private final int endHour;

    public Booking(String bookingId, String roomNo, String machineId, int startHour, int endHour) {
        this.bookingId = bookingId;
        this.roomNo = roomNo;
        this.machineId = machineId;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getMachineId() {
        return machineId;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public boolean isNotified() {
        return false;
    }

    public void markNotified() {
    }

    public boolean overlaps(int otherStart, int otherEnd) {
        return false;
    }
}
