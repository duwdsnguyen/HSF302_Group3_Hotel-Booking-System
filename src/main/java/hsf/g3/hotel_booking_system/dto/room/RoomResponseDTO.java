package hsf.g3.hotel_booking_system.dto.room;

import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String description;
}
