package hsf.g3.hotel_booking_system.enums.room;

public enum RoomTypeStatus {
    ACTIVE("Đang kinh doanh"),
    INACTIVE("Ngừng kinh doanh");

    private final String label;

    RoomTypeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
