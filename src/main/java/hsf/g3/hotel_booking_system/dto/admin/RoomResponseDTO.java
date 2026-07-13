package hsf.g3.hotel_booking_system.dto.admin;

import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RoomResponseDTO {
    private Integer roomId;
    private String roomNumber;
    private Integer roomTypeId;
    private String roomTypeName;
    private Integer floorNumber;
    private RoomStatus status;
    private BigDecimal basePrice;
    private String description;
}
