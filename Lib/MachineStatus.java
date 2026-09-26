package Lib;

/**
 * สีบนจอ = ค่าในนี้เท่านั้น ห้ามมีสถานะอื่น
 * AVAILABLE เขียว | RESERVED ส้ม | IN_USE แดง | PENDING เหลือง
 */
public enum MachineStatus {
    AVAILABLE,
    RESERVED,
    IN_USE,
    PENDING
}
