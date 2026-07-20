package hsf.g3.hotel_booking_system.exception;

public class DuplicateServiceNameException extends RuntimeException {
    public DuplicateServiceNameException(String message) {
        super(message);
    }
}
