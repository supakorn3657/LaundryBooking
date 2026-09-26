package Lib;

/**
 * โครงครั้งที่ 1 — ตู้ซักผ้า (ยังไม่เปลี่ยนสี / ยังไม่นับเวลา)
 */
public class WashingMachine extends Machine {
    public static final int HOLD_SECONDS = 15 * 60;
    public static final int WASH_SECONDS = 60 * 60;
    public static final int PENDING_SECONDS = 10 * 60;

    public WashingMachine(String machineId) {
        super(machineId);
    }

    public void reserve(String roomNo) throws InvalidOperationException {
    }

    public void startWash() throws InvalidOperationException {
    }

    public boolean tick() {
        return false;
    }

    public void collect() throws InvalidOperationException {
    }

    public int getTimeRemaining() {
        return 0;
    }

    public int getCycleDuration() {
        return 0;
    }

    public String getCurrentRoomNo() {
        return null;
    }
}
