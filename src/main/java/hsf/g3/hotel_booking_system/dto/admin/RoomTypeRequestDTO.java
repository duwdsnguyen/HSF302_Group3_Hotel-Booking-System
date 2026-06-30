package hsf.g3.hotel_booking_system.dto.admin;

import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomTypeRequestDTO {
    private String typeName;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private RoomTypeStatus status;
}
