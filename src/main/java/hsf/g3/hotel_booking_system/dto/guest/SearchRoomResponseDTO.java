package hsf.g3.hotel_booking_system.dto.guest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SearchRoomResponseDTO {
    private Integer roomId;
    private String roomNumber;
    private String typeName;
    private Integer maxGuests;
    private BigDecimal basePrice;
    private Integer floorNumber;
    private String description;
}
