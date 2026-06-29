package hsf.g3.hotel_booking_system.dto.room;

import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter
@NoArgsConstructor
public class RoomTypeResponseDTO {
    private Integer roomTypeId;
    private String typeName;
    private String description;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private RoomTypeStatus status;
}
