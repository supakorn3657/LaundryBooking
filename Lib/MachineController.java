package Lib;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * โครงครั้งที่ 1 — สมองกลาง (ยังไม่จอง / ยังไม่นับเวลา)
 */
public class MachineController {
    public MachineController(BookingManager bookingManager, LogService logService) {
    }

    public LocalTime getNow() {
        return null;
    }

    public void reserveNow(String machineId, String roomNo) throws InvalidOperationException {
    }

    public void bookSlot(String machineId, String roomNo, int startHour, int endHour)
            throws InvalidOperationException {
    }

    public void startWash(String machineId) throws InvalidOperationException {
    }

    public void collectClothes(String machineId) throws InvalidOperationException {
    }

    public List<WashingMachine> getMachines() {
        return new ArrayList<WashingMachine>();
    }

    public List<Booking> getBookings() {
        return new ArrayList<Booking>();
    }

    public void addListener(MachineListener listener) {
    }

    public void startTimer() {
    }

    public void shutdown() {
    }

    public void tickAll() {
    }

    public WashingMachine findMachine(String machineId) {
        return null;
    }

    public BookingManager getBookingManager() {
        return null;
    }
}
