package Lib;

/**
 * โครงครั้งที่ 1 — จอต้องทำตามนี้ทีหลัง
 */
public interface MachineListener {
    void onStatusChanged(WashingMachine machine);

    void onTimerTick(WashingMachine machine);

    void onSlotDue(Booking booking);

    void onClockTick(java.time.LocalTime now);
}
