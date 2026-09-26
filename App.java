import Lib.Booking;
import Lib.BookingManager;
import Lib.InvalidOperationException;
import Lib.LogService;
import Lib.MachineController;
import Lib.MachineStatus;
import Lib.WashingMachine;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * โครงเทส 10 คะแนน — ข้อความในคอนโซลเป็นอังกฤษ เพราะเทอร์มินัล Windows อ่านไทยเพี้ยน
 * รัน: javac -encoding UTF-8 App.java Lib\\*.java && java App
 */
public class App {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testSixMachines();
        testReserveNowGreenToOrange();
        testReserveNowBusyFails();
        testStartWashFromReserved();
        testStartWashFromGreenFails();
        testHoldExpireBackToGreen();
        testWashThenPendingThenCollect();
        testBookSlotSaved();
        testBookSlotOverlapFails();
        testUnknownMachineFails();
        testCannotBookCurrentHour();
        testCannotBookHourWhileWashing();
        testReserveNowNamesTheFutureSlot();

        System.out.println();
        System.out.println("passed " + passed + " / failed " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static MachineController newController() {
        BookingManager books = new BookingManager("bookings.csv");
        LogService silent = new LogService() {
            public void log(String machineId, String action, String detail) {
            }
        };
        ZoneId zone = ZoneId.of("Asia/Bangkok");
        Clock clock = Clock.fixed(
                LocalDateTime.of(2026, 9, 25, 8, 0, 0).atZone(zone).toInstant(),
                zone);
        return new MachineController(books, silent, clock);
    }

    private static void testSixMachines() {
        MachineController c = newController();
        check("6 machines", c.getMachines().size() == 6);
        check("id M-01", c.findMachine("M-01") != null);
        check("id M-06", c.findMachine("M-06") != null);
        check("start AVAILABLE", c.findMachine("M-01").getStatus() == MachineStatus.AVAILABLE);
    }

    private static void testReserveNowGreenToOrange() {
        MachineController c = newController();
        try {
            c.reserveNow("M-01", "101");
            WashingMachine m = c.findMachine("M-01");
            check("reserveNow -> RESERVED", m.getStatus() == MachineStatus.RESERVED);
            check("lock room 101", "101".equals(m.getCurrentRoomNo()));
            check("hold 15 min", m.getTimeRemaining() == WashingMachine.HOLD_SECONDS);
        } catch (InvalidOperationException e) {
            fail("reserveNow should succeed: " + e.getMessage());
        }
    }

    private static void testReserveNowBusyFails() {
        MachineController c = newController();
        try {
            c.reserveNow("M-02", "102");
            c.reserveNow("M-02", "103");
            fail("busy machine must reject second reserveNow");
        } catch (InvalidOperationException e) {
            check("double reserveNow throws", true);
        }
    }

    private static void testStartWashFromReserved() {
        MachineController c = newController();
        try {
            c.reserveNow("M-03", "201");
            c.startWash("M-03");
            WashingMachine m = c.findMachine("M-03");
            check("startWash -> IN_USE", m.getStatus() == MachineStatus.IN_USE);
            check("wash 60 min", m.getTimeRemaining() == WashingMachine.WASH_SECONDS);
        } catch (InvalidOperationException e) {
            fail("startWash from RESERVED should succeed: " + e.getMessage());
        }
    }

    private static void testStartWashFromGreenFails() {
        MachineController c = newController();
        try {
            c.startWash("M-04");
            fail("AVAILABLE must not startWash");
        } catch (InvalidOperationException e) {
            check("startWash from AVAILABLE throws", true);
        }
    }

    private static void testHoldExpireBackToGreen() {
        MachineController c = newController();
        try {
            c.reserveNow("M-05", "301");
            WashingMachine m = c.findMachine("M-05");
            int steps = m.getTimeRemaining();
            for (int i = 0; i < steps; i++) {
                c.tickAll();
            }
            check("hold expire -> AVAILABLE", m.getStatus() == MachineStatus.AVAILABLE);
            check("room unlocked", m.getCurrentRoomNo() == null);
        } catch (InvalidOperationException e) {
            fail("hold expire test failed: " + e.getMessage());
        }
    }

    private static void testWashThenPendingThenCollect() {
        MachineController c = newController();
        try {
            c.reserveNow("M-06", "401");
            c.startWash("M-06");
            WashingMachine m = c.findMachine("M-06");
            int washSteps = m.getTimeRemaining();
            for (int i = 0; i < washSteps; i++) {
                c.tickAll();
            }
            check("wash done -> PENDING", m.getStatus() == MachineStatus.PENDING);
            check("collect 10 min", m.getTimeRemaining() == WashingMachine.PENDING_SECONDS);

            c.collectClothes("M-06");
            check("collect -> AVAILABLE", m.getStatus() == MachineStatus.AVAILABLE);
        } catch (InvalidOperationException e) {
            fail("wash-pending-collect failed: " + e.getMessage());
        }
    }

    private static void testBookSlotSaved() {
        MachineController c = newController();
        try {
            c.bookSlot("M-01", "501", 13, 14);
            check("1 booking", c.getBookings().size() == 1);
            Booking b = c.getBookings().get(0);
            check("booked M-01", "M-01".equals(b.getMachineId()));
            check("hours 13-14", b.getStartHour() == 13 && b.getEndHour() == 14);
            check("slot keeps machine AVAILABLE", c.findMachine("M-01").isAvailable());
        } catch (InvalidOperationException e) {
            fail("bookSlot should succeed: " + e.getMessage());
        }
    }

    private static void testBookSlotOverlapFails() {
        MachineController c = newController();
        try {
            c.bookSlot("M-02", "601", 10, 12);
            c.bookSlot("M-02", "602", 11, 13);
            fail("overlapping slot must fail");
        } catch (InvalidOperationException e) {
            check("overlap throws", true);
        }
        try {
            c.bookSlot("M-03", "603", 11, 13);
            check("other machine same hour ok", c.getBookings().size() >= 2);
        } catch (InvalidOperationException e) {
            fail("other machine should not overlap: " + e.getMessage());
        }
    }

    private static void testCannotBookCurrentHour() {
        MachineController c = newController();
        try {
            c.bookSlot("M-01", "701", 8, 9);
            fail("current hour slot must fail");
        } catch (InvalidOperationException e) {
            check("current hour throws", true);
        }
    }

    private static void testCannotBookHourWhileWashing() {
        MachineController c = newController();
        try {
            c.reserveNow("M-04", "801");
            c.startWash("M-04");
            c.bookSlot("M-04", "802", 8, 9);
            fail("slot during wash must fail");
        } catch (InvalidOperationException e) {
            check("busy hour throws", true);
        }
        try {
            c.bookSlot("M-04", "803", 10, 11);
            check("later hour still free", c.getBookings().size() == 1);
        } catch (InvalidOperationException e) {
            fail("later hour should be free: " + e.getMessage());
        }
    }

    private static void testReserveNowNamesTheFutureSlot() {
        MachineController c = newController();
        try {
            c.bookSlot("M-04", "100", 9, 10);
            c.reserveNow("M-04", "101");
            fail("reserveNow over future slot must fail");
        } catch (InvalidOperationException e) {
            check("message has M-04", e.getMessage().contains("M-04"));
            check("message has 09:00-10:00", e.getMessage().contains("09:00-10:00"));
        }
    }

    private static void testUnknownMachineFails() {
        MachineController c = newController();
        try {
            c.reserveNow("M-99", "101");
            fail("unknown machine must fail");
        } catch (InvalidOperationException e) {
            check("unknown machine throws", true);
        }
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void fail(String name) {
        failed++;
        System.out.println("[FAIL] " + name);
    }
}
