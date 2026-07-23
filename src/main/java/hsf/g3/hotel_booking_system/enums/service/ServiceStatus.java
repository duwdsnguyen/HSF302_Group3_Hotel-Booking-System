package hsf.g3.hotel_booking_system.enums.service;

public enum ServiceStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động");

    private final String label;

    ServiceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
