package hsf.g3.hotel_booking_system.dto.admin;

import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomTypeRequestDTO {
    private String typeName;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private RoomTypeStatus status;
}
