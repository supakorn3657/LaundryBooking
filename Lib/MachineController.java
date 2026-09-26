package Lib;

import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * สมองกลางของระบบ — GUI เรียกเมธอดนี้เท่านั้น ห้ามแตะตู้ตรง ๆ
 * นาฬิกาขวาบนกับกติกาจองชั่วโมงใช้ now ตัวเดียวกัน
 */
public class MachineController {
    private final List<WashingMachine> machines;
    private final BookingManager bookingManager;
    private final LogService logService;
    private final List<MachineListener> listeners;
    private final TimerThread timerThread;
    private final Clock clock;
    private LocalTime now;
    private int lastHour;

    public MachineController(BookingManager bookingManager, LogService logService) {
        this(bookingManager, logService, Clock.systemDefaultZone());
    }

    public MachineController(BookingManager bookingManager, LogService logService, Clock clock) {
        this.bookingManager = bookingManager;
        this.logService = logService;
        this.clock = clock;
        this.now = LocalTime.now(clock);
        this.lastHour = -1;
        this.machines = new ArrayList<WashingMachine>();
        this.listeners = new ArrayList<MachineListener>();
        for (int i = 1; i <= 6; i++) {
            machines.add(new WashingMachine("M-" + String.format("%02d", i)));
        }
        this.timerThread = new TimerThread(this);
    }

    public LocalTime getNow() {
        return now;
    }

    /** จองทันที ตู้ต้องเขียว และห้ามทับใบจองชั่วโมงที่มีอยู่ */
    public void reserveNow(String machineId, String roomNo) throws InvalidOperationException {
        WashingMachine machine = requireMachine(machineId);
        rejectIfImmediateOverlapsBooking(machineId);
        machine.reserve(roomNo);
        log("RESERVE_NOW", machineId + " room=" + roomNo);
        fireStatus(machine);
    }

    /** นัดชั่วโมง — เก็บใบจอง ยังไม่เปลี่ยนสีตู้ จนกว่านาฬิกาจะถึงชั่วโมงเริ่ม */
    public void bookSlot(String machineId, String roomNo, int startHour, int endHour)
            throws InvalidOperationException {
        requireMachine(machineId);
        if (roomNo == null || roomNo.trim().isEmpty()) {
            throw new InvalidOperationException("ต้องมีเลขห้อง");
        }
        rejectIfSlotNotBookable(machineId, startHour, endHour);
        if (!bookingManager.isSlotFree(machineId, startHour, endHour)) {
            throw new InvalidOperationException("ช่วงเวลานี้มีคนจองแล้ว");
        }
        Booking booking = new Booking(
                bookingManager.nextBookingId(),
                roomNo.trim(),
                machineId,
                startHour,
                endHour);
        bookingManager.add(booking);
        log("BOOK_SLOT", booking.getBookingId() + " " + machineId + " " + startHour + "-" + endHour);
    }

    public void startWash(String machineId) throws InvalidOperationException {
        WashingMachine machine = requireMachine(machineId);
        machine.startWash();
        log("START_WASH", machineId);
        fireStatus(machine);
    }

    public void collectClothes(String machineId) throws InvalidOperationException {
        WashingMachine machine = requireMachine(machineId);
        machine.collect();
        log("COLLECT", machineId);
        fireStatus(machine);
    }

    public List<WashingMachine> getMachines() {
        return Collections.unmodifiableList(machines);
    }

    public List<Booking> getBookings() {
        return bookingManager.getAll();
    }

    public void addListener(MachineListener listener) {
        listeners.add(listener);
        listener.onClockTick(now);
    }

    public void startTimer() {
        if (!timerThread.isAlive()) {
            timerThread.start();
        }
    }

    public void shutdown() {
        timerThread.stopTimer();
    }

    public void tickAll() {
        now = LocalTime.now(clock);
        fireClock();
        for (WashingMachine machine : machines) {
            boolean changed = machine.tick();
            fireTick(machine);
            if (changed) {
                log("TICK_EXPIRE", machine.getMachineId() + " -> " + machine.getStatus());
                fireStatus(machine);
            }
        }
        int hour = now.getHour();
        if (lastHour != -1 && hour != lastHour) {
            activateDueSlots(hour);
        }
        lastHour = hour;
    }

    /** ยิงเมื่อนาฬิกาข้ามเข้าชั่วโมงใหม่เท่านั้น ไม่ยิงถ้าจองตอนชั่วโมงนั้นกำลังเดินอยู่ */
    void activateDueSlots(int hour) {
        for (Booking booking : bookingManager.getAll()) {
            if (booking.isNotified() || booking.getStartHour() != hour) {
                continue;
            }
            booking.markNotified();
            WashingMachine machine = findMachine(booking.getMachineId());
            if (machine != null && machine.isAvailable()) {
                try {
                    machine.reserve(booking.getRoomNo());
                    log("SLOT_DUE", booking.getBookingId() + " " + machine.getMachineId());
                    fireStatus(machine);
                } catch (InvalidOperationException ignored) {
                }
            }
            fireSlotDue(booking);
        }
    }

    public WashingMachine findMachine(String machineId) {
        for (WashingMachine m : machines) {
            if (m.getMachineId().equals(machineId)) {
                return m;
            }
        }
        return null;
    }

    public BookingManager getBookingManager() {
        return bookingManager;
    }

    private void rejectIfSlotNotBookable(String machineId, int startHour, int endHour)
            throws InvalidOperationException {
        int hour = now.getHour();
        if (startHour < hour) {
            throw new InvalidOperationException("ช่วงเวลานี้ผ่านไปแล้ว");
        }
        if (startHour == hour) {
            throw new InvalidOperationException("ชั่วโมงนี้เริ่มแล้ว จองชั่วโมงถัดไป");
        }
        if (overlapsCurrentUse(machineId, startHour, endHour)) {
            throw new InvalidOperationException("ตู้กำลังถูกใช้ช่วงนี้ จองชั่วโมงนี้ไม่ได้");
        }
    }

    private void rejectIfImmediateOverlapsBooking(String machineId) throws InvalidOperationException {
        int duration = WashingMachine.HOLD_SECONDS
                + WashingMachine.WASH_SECONDS
                + WashingMachine.PENDING_SECONDS;
        LocalTime until = now.plusSeconds(duration);
        int hour = now.getHour();
        for (Booking b : bookingManager.getByMachine(machineId)) {
            if (b.getStartHour() <= hour) {
                continue;
            }
            if (overlapsHours(b.getStartHour(), b.getEndHour(), now, until)) {
                throw new InvalidOperationException(
                        machineId + " มีคนจอง " + formatSlot(b.getStartHour(), b.getEndHour()) + " แล้ว");
            }
        }
    }

    private static String formatSlot(int startHour, int endHour) {
        int endShow = endHour == 24 ? 0 : endHour;
        return String.format("%02d:00-%02d:00", startHour, endShow);
    }

    private boolean overlapsCurrentUse(String machineId, int startHour, int endHour) {
        WashingMachine machine = findMachine(machineId);
        if (machine == null || machine.isAvailable()) {
            return false;
        }
        int seconds = machine.getTimeRemaining();
        if (machine.getStatus() == MachineStatus.IN_USE) {
            seconds += WashingMachine.PENDING_SECONDS;
        }
        return overlapsHours(startHour, endHour, now, now.plusSeconds(seconds));
    }

    /** ใบจอง [startHour, endHour) ทับช่วงนาฬิกา [from, until] หรือไม่ */
    static boolean overlapsHours(int startHour, int endHour, LocalTime from, LocalTime until) {
        int occStart = from.getHour();
        int occEnd = until.getHour();
        if (until.getMinute() > 0 || until.getSecond() > 0 || until.getNano() > 0) {
            occEnd++;
        }
        if (until.isBefore(from) || occEnd > 24) {
            occEnd = 24;
        }
        return startHour < occEnd && occStart < endHour;
    }

    private WashingMachine requireMachine(String machineId) throws InvalidOperationException {
        WashingMachine machine = findMachine(machineId);
        if (machine == null) {
            throw new InvalidOperationException("ไม่พบตู้ " + machineId);
        }
        return machine;
    }

    private void log(String action, String detail) {
        if (logService != null) {
            logService.log("CONTROLLER", action, detail);
        }
    }

    private void fireStatus(WashingMachine machine) {
        for (MachineListener listener : listeners) {
            listener.onStatusChanged(machine);
        }
    }

    private void fireTick(WashingMachine machine) {
        for (MachineListener listener : listeners) {
            listener.onTimerTick(machine);
        }
    }

    private void fireSlotDue(Booking booking) {
        for (MachineListener listener : listeners) {
            listener.onSlotDue(booking);
        }
    }

    private void fireClock() {
        for (MachineListener listener : listeners) {
            listener.onClockTick(now);
        }
    }
}
