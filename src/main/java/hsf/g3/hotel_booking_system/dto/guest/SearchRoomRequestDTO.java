package hsf.g3.hotel_booking_system.dto.guest;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchRoomRequestDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate checkOutDate;
}
