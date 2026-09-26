package Lib;

/**
 * นาฬิกา 1 วินาที — GUI ต้องเรียก controller.startTimer()
 * ห้าม start ในเทส App.java จะชนกับ tickAll() มือ
 */
public class TimerThread extends Thread {
    private final MachineController controller;
    private volatile boolean running;

    public TimerThread(MachineController controller) {
        this.controller = controller;
        this.running = false;
        setDaemon(true);
    }

    public void stopTimer() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
                return;
            }
            if (controller != null) {
                controller.tickAll();
            }
        }
    }
}
