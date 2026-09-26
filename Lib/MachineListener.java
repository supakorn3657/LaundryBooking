package Lib;

/**
 * จอต้องทำตามนี้ เมื่อตู้เปลี่ยน / นาฬิกาเดิน / ถึงชั่วโมงจอง
 */
public interface MachineListener {
    void onStatusChanged(WashingMachine machine);

    void onTimerTick(WashingMachine machine);

    void onSlotDue(Booking booking);

    /** นาฬิกาหลักของระบบ — จอขวาบนต้องโชว์ค่านี้ */
    void onClockTick(java.time.LocalTime now);
}
