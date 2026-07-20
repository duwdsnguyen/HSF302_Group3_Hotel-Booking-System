package hsf.g3.hotel_booking_system.dto.guest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Integer roomId;
    private List<Long> serviceIds;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
    
    private Integer numberOfGuests;
}
