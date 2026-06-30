package hsf.g3.hotel_booking_system.dto.guest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchRoomRequestDTO {
    
    @NotNull(message = "Ngày nhận phòng không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate checkInDate;

    @NotNull(message = "Ngày trả phòng không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate checkOutDate;

    @NotNull(message = "Số lượng khách không được để trống")
    @Min(value = 1, message = "Số lượng khách tối thiểu là 1")
    Integer numberOfGuests;
}
