package hsf.g3.hotel_booking_system.enums.user;

public enum UserStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
