package hsf.g3.hotel_booking_system.service.external;

public interface MailService {
    void sendResetPasswordEmail(String email,String resetUrl);
}
