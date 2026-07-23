package hsf.g3.hotel_booking_system.dto.guest;

import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryDTO {
    private Integer id;
    private String roomNumber;
    private String roomTypeName;
    private List<String> roomImageUrls;
    private Integer floorNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private int numberOfGuests;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;

    // For detail page
    private List<BookingServiceDTO> services;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingServiceDTO {
        private String serviceName;
        private BigDecimal price;
        private int quantity;
    }
}