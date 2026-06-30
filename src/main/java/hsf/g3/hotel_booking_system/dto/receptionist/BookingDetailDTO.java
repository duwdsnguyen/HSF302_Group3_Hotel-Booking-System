package hsf.g3.hotel_booking_system.dto.receptionist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import hsf.g3.hotel_booking_system.enums.user.BookingStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookingDetailDTO {
    private int bookingId;
    private String customerName;
    private String roomNumber;
    private String roomTypeName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private int numberOfGuest;
    private double totalAmount;
    private BookingStatus status;
}
