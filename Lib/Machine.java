package Lib;

/**
 * โครงครั้งที่ 1 — คลาสมาแม่ของตู้ (ยังไม่มี logic)
 */
public abstract class Machine {
    private final String machineId;
    private MachineStatus status;

    public Machine(String machineId) {
        this.machineId = machineId;
        this.status = MachineStatus.AVAILABLE;
    }

    public String getMachineId() {
        return machineId;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return false;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }
}
