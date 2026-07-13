package hsf.g3.hotel_booking_system.dto.receptionist;

import hsf.g3.hotel_booking_system.entity.guest.BookingService;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BookingDetailDTO {
    private int bookingId;
    private String status;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private BigDecimal totalAmount;
    private int numberOfGuests;
    private LocalDateTime createdAt;

    private long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private String roomNumber;
    private String roomTypeName;
    private List<BookingService> services;
}
