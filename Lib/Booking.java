package Lib;

/**
 * ใบจอง 1 ใบ = ห้องไหน จองตู้ไหน กี่โมงถึงกี่โมง
 * ชั่วโมงใช้ 0-23 เช่น 13 ถึง 14 คือ 13:00-14:00
 */
public class Booking {
    private final String bookingId;
    private final String roomNo;
    private final String machineId;
    private final int startHour;
    private final int endHour;
    private boolean notified;

    public Booking(String bookingId, String roomNo, String machineId, int startHour, int endHour) {
        this.bookingId = bookingId;
        this.roomNo = roomNo;
        this.machineId = machineId;
        this.startHour = startHour;
        this.endHour = endHour;
        this.notified = false;
        checkRep();
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

    /** Timer ยิงป็อปอัพไปแล้วหรือยัง — กันเด้งทุกวินาที */
    public boolean isNotified() {
        return notified;
    }

    public void markNotified() {
        this.notified = true;
    }

    /** ทับช่วงไหม เช่น 13-15 ทับ 14-16 */
    public boolean overlaps(int otherStart, int otherEnd) {
        return startHour < otherEnd && otherStart < endHour;
    }

    private void checkRep() {
        if (bookingId == null || roomNo == null || machineId == null) {
            throw new RuntimeException("ใบจองต้องมี id / ห้อง / ตู้");
        }
        if (startHour < 0 || startHour > 23 || endHour < 1 || endHour > 24) {
            throw new RuntimeException("ชั่วโมงไม่ถูกต้อง");
        }
        if (endHour <= startHour) {
            throw new RuntimeException("endHour ต้องมากกว่า startHour");
        }
    }
}
