package hsf.g3.hotel_booking_system.enums.room;

public enum BookingStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    CHECKED_IN("Đã nhận phòng"),
    ROOM_CHANGE_PENDING("Chờ chấp nhận đổi phòng"),
    ROOM_CHANGE_APPROVED("Duyệt đổi phòng"),
    ROOM_CHANGE_REJECTED("Từ chối đổi phòng"),
    CHECKED_OUT("Đã trả phòng"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String label;

    BookingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
