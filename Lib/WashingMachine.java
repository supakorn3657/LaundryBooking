package Lib;

/**
 * ตู้ซักผ้า (ลูกของ Machine) — ซัก+ปั่นในเครื่องเดียว ล็อก 60 นาที
 *
 * นาฬิกา timeRemaining นับเป็นวินาที เพราะ TimerThread จะ tick ทุก 1 วินาที
 * ส้ม 15 นาที = 900 วิ | แดง 60 นาที = 3600 วิ | เหลือง 10 นาที = 600 วิ
 */
public class WashingMachine extends Machine {
    public static final int HOLD_SECONDS = 15 * 60;
    public static final int WASH_SECONDS = 60 * 60;
    public static final int PENDING_SECONDS = 10 * 60;

    private int timeRemaining;
    private int cycleDuration;
    private String currentRoomNo;

    public WashingMachine(String machineId) {
        super(machineId);
        this.timeRemaining = 0;
        this.cycleDuration = WASH_SECONDS;
        this.currentRoomNo = null;
        checkRep();
    }

    /** จองทันที: เขียว → ส้ม เริ่มนับ 15 นาที */
    public void reserve(String roomNo) throws InvalidOperationException {
        if (!isAvailable()) {
            throw new InvalidOperationException(getMachineId() + " ไม่ว่าง จองทันทีไม่ได้");
        }
        if (roomNo == null || roomNo.trim().isEmpty()) {
            throw new InvalidOperationException("ต้องมีเลขห้อง");
        }
        this.currentRoomNo = roomNo.trim();
        this.timeRemaining = HOLD_SECONDS;
        setStatus(MachineStatus.RESERVED);
        checkRep();
    }

    /** กดเริ่มซัก: ส้ม → แดง นับ 60 นาที (ซัก+ปั่น) */
    public void startWash() throws InvalidOperationException {
        if (getStatus() != MachineStatus.RESERVED) {
            throw new InvalidOperationException(getMachineId() + " ยังไม่ใช่ RESERVED เริ่มซักไม่ได้");
        }
        this.cycleDuration = WASH_SECONDS;
        this.timeRemaining = WASH_SECONDS;
        setStatus(MachineStatus.IN_USE);
        checkRep();
    }

    /**
     * เรียกทุก 1 วินาที
     * @return true ถ้าสถานะเปลี่ยนในรอบนี้ (หมดเวลา)
     */
    public boolean tick() {
        if (getStatus() == MachineStatus.AVAILABLE) {
            return false;
        }
        if (timeRemaining <= 0) {
            return expire();
        }
        timeRemaining--;
        if (timeRemaining <= 0) {
            return expire();
        }
        return false;
    }

    /** เหลือง กดเอาผ้าออก → เขียว */
    public void collect() throws InvalidOperationException {
        if (getStatus() != MachineStatus.PENDING) {
            throw new InvalidOperationException(getMachineId() + " ยังไม่ใช่ PENDING เก็บผ้าไม่ได้");
        }
        release();
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public int getCycleDuration() {
        return cycleDuration;
    }

    public String getCurrentRoomNo() {
        return currentRoomNo;
    }

    /** หมดเวลาส้ม/แดง/เหลือง — ใช้ร่วมกัน */
    private boolean expire() {
        MachineStatus before = getStatus();
        if (before == MachineStatus.RESERVED) {
            release();
            return true;
        }
        if (before == MachineStatus.IN_USE) {
            timeRemaining = PENDING_SECONDS;
            setStatus(MachineStatus.PENDING);
            checkRep();
            return true;
        }
        if (before == MachineStatus.PENDING) {
            release();
            return true;
        }
        return false;
    }

    private void release() {
        currentRoomNo = null;
        timeRemaining = 0;
        setStatus(MachineStatus.AVAILABLE);
        checkRep();
    }

    private void checkRep() {
        if (timeRemaining < 0) {
            throw new RuntimeException("timeRemaining ห้ามติดลบ");
        }
        if (getStatus() == MachineStatus.AVAILABLE && currentRoomNo != null) {
            throw new RuntimeException("ตู้เขียวต้องไม่มีห้องล็อกอยู่");
        }
    }
}
