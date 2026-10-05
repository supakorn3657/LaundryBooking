package Lib;

/**
 * โหมดซัก — เวลาผูกกับโหมด ห้ามให้ผู้ใช้เลือกนาทีเอง
 *
 * ตัวเลขนี้เป็นค่าที่กลุ่มกำหนดไว้สำหรับเดโม ไม่ได้วัดจากเครื่องจริง
 * ปั่นเร็ว 15 นาที | ปั่นทั่วไป 45 นาที | ปั่นผ้านวม 70 นาที
 */
public enum WashMode {
    NORMAL("ปั่นทั่วไป", 45 * 60),
    FAST("ปั่นเร็ว", 15 * 60),
    COMFORTER("ปั่นผ้านวม", 70 * 60);

    private final String label;
    private final int seconds;

    WashMode(String label, int seconds) {
        this.label = label;
        this.seconds = seconds;
    }

    public String getLabel() {
        return label;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return seconds / 60;
    }
}
