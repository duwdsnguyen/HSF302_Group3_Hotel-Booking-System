package hsf.g3.hotel_booking_system.dto.guest;

import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Integer id;
    private Integer customerId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private int numberOfGuests;
    private BigDecimal totalAmount;
    private BookingStatus status;
}
