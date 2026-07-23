package hsf.g3.hotel_booking_system.enums.room;

public enum RoomStatus {
    AVAILABLE("Còn trống"),
    OCCUPIED("Đang sử dụng"),
    MAINTENANCE("Đang bảo trì");

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
